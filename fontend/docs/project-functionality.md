# Tài liệu mô tả chức năng project Chat Frontend

## 1. Tổng quan

Đây là một dự án frontend viết bằng **Angular 18** theo mô hình **standalone component**, dùng để xây dựng giao diện cho một ứng dụng **chat room thời gian thực**.

Project hiện tại tập trung vào các nhóm chức năng chính sau:

- Đăng nhập người dùng
- Đăng ký tài khoản mới
- Kiểm tra trạng thái xác thực tài khoản
- Vào phòng chat và rời phòng chat
- Tạo phòng chat mới
- Tìm kiếm phòng chat theo tên
- Xem lịch sử tin nhắn trong phòng
- Gửi tin nhắn thời gian thực qua WebSocket/STOMP
- Upload file và gửi file vào cuộc trò chuyện
- Tải file đã được chia sẻ trong phòng chat
- Kiểm soát truy cập route bằng guard
- Lưu phiên đăng nhập tạm thời bằng `localStorage`

Ngoài phần chat chính, project còn có một số màn hình **ví dụ/demo** để minh họa cách gọi API REST và truyền dữ liệu qua component.

---

## 2. Công nghệ và nền tảng sử dụng

### Frontend framework
- **Angular 18**
- **Standalone Components** thay cho module truyền thống
- **Angular Router** để điều hướng giữa các màn hình
- **Angular Forms** cho binding dữ liệu biểu mẫu
- **Angular HttpClient** để gọi REST API
- **Angular Material** cho một số thành phần giao diện cơ bản

### Realtime communication
- **SockJS**
- **STOMP client** (`@stomp/stompjs`)

### Server-side rendering
- Dự án có cấu hình **Angular SSR** với **Express** trong file `server.ts`
- Có thể phục vụ ứng dụng ở chế độ render phía server sau khi build SSR

### Backend integration
Frontend gọi backend mặc định qua:

- **REST API base URL**: `http://localhost:8080`
- **WebSocket endpoint**: `http://localhost:8080/ws`

Cấu hình này nằm trong file:

- `src/enviroment/environment.ts`

---

## 3. Cấu trúc chức năng chính của project

## 3.1. Điều hướng ứng dụng

File định nghĩa route: `src/app/app.routes.ts`

Các route hiện có:

| Route | Component | Mục đích |
|---|---|---|
| `/` | `LoginComponent` | Màn hình đăng nhập mặc định |
| `/login` | `LoginComponent` | Màn hình đăng nhập/đăng ký |
| `/example` | `ExampleComponent` | Màn hình demo lấy danh sách dữ liệu |
| `/send-data` | `ExampleSentComponent` | Màn hình demo gửi dữ liệu |
| `/chat` | `ChatComponent` | Màn hình chat chính |
| `/forgot-password` | `ForgotPasswordComponent` | Màn hình quên mật khẩu |

Trong đó route `/chat` được bảo vệ bằng `AuthGuard`.

---

## 3.2. Màn hình đăng nhập và đăng ký

File chính:

- `src/app/login/login.component.ts`
- `src/app/login/login.component.html`

### Chức năng
Màn hình này có 2 chế độ:

1. **Đăng nhập**
2. **Đăng ký tài khoản**

### Dữ liệu sử dụng
Đối tượng người dùng dùng model `User` trong file `src/app/model/Login.ts` gồm:

- `username`
- `password`
- `email`

### Luồng đăng nhập
Khi người dùng bấm **Login**:

1. Frontend gọi API `GET /user/find-username`
2. Gửi `username` và `password` qua query params
3. Nếu backend trả về dữ liệu người dùng:
   - Nếu `verified = true`: 
     - Lưu thông tin người dùng vào `localStorage`
     - Gắn thêm thời gian hết hạn phiên là **30 phút**
     - Chuyển hướng sang `/chat`
   - Nếu `verified = false` nhưng có `id`:
     - Hiển thị thông báo `Account not verified`
   - Nếu không hợp lệ:
     - Hiển thị thông báo `Name or password is incorrect`

### Luồng đăng ký
Khi người dùng bấm **Register** ở trạng thái đăng ký:

1. Hiển thị thêm trường `email`
2. Gọi API `POST /user/add`
3. Gửi object `User` lên backend để tạo tài khoản

### Ghi chú
- Sau khi đăng ký, hiện tại component chưa có xử lý thông báo thành công/thất bại rõ ràng.
- Nút "Forgot Password" đã có hàm điều hướng trong TypeScript nhưng phần link trong template đang bị comment.

---

## 3.3. Kiểm soát truy cập bằng AuthGuard

File: `src/app/auth.guard.ts`

### Mục đích
Ngăn người dùng chưa đăng nhập truy cập vào màn hình chat.

### Cách hoạt động
- Guard đọc `localStorage.getItem('user')`
- Nếu tồn tại dữ liệu thì cho phép vào route
- Nếu không có dữ liệu thì điều hướng về `/login`

### Lưu ý kỹ thuật
Guard hiện tại chỉ kiểm tra **sự tồn tại** của dữ liệu trong `localStorage`, chưa tự kiểm tra hạn dùng của phiên.
Việc kiểm tra hết hạn phiên hiện đang được xử lý bên trong `ChatComponent`.

---

## 3.4. Màn hình chat chính

File chính:

- `src/app/chat/chat.component.ts`
- `src/app/chat/chat.component.html`

Đây là phần quan trọng nhất của project.

### Chức năng chính

#### 1. Kết nối WebSocket khi vào màn hình
Khi component khởi tạo (`ngOnInit`):

- Lấy thông tin người dùng từ `localStorage`
- Tự kiểm tra phiên đăng nhập còn hạn hay không
- Kết nối WebSocket đến backend
- Subscribe luồng tin nhắn realtime
- Tải danh sách phòng chat

#### 2. Nhận dữ liệu realtime
Frontend lắng nghe message từ topic:

- `/topic/public`

Sau khi nhận message, component phân loại theo `type`:

- `CHAT`: thêm tin nhắn mới vào danh sách `messages`
- `COUNT`: cập nhật số lượng thành viên trong phòng `userCount`

#### 3. Hiển thị danh sách phòng chat
Component gọi API lấy danh sách phòng:

- `GET /room/get-list-room`

Danh sách phòng được phân trang ở frontend với:

- `pageSize = 20`
- `currentPage`
- `totalPages`

Người dùng có thể chuyển trang bằng nút `Previous` và `Next`.

#### 4. Tìm kiếm phòng chat theo tên
Khi nhập tên phòng và nhấn Enter, frontend gọi:

- `GET /room/get-list-room-by-name?roomName=...`

Kết quả trả về sẽ thay thế danh sách phòng hiện tại.

#### 5. Tạo phòng chat mới
Người dùng bấm nút **Create room** để mở popup.

Khi submit:

1. Tạo object `ChatRoom`
2. Gọi API `POST /room/add-room`
3. Nếu backend tạo thành công:
   - Lấy `roomId` từ phòng mới tạo
   - Tự động gửi yêu cầu tham gia phòng bằng API đổi trạng thái thành viên
   - Làm mới lại danh sách phòng

#### 6. Chọn phòng chat để tham gia
Khi người dùng click vào một phòng:

1. Tạo object `Member`
2. Gửi API `POST /room/change-member` với trạng thái `JOIN`
3. Nếu backend trả về số âm:
   - Hiển thị `Chat room is full`
4. Nếu thành công:
   - Lưu `curentRoomId`
   - Gọi API lấy lịch sử tin nhắn trong phòng
   - Cập nhật số lượng người trong phòng
   - Gửi message realtime loại `COUNT`

#### 7. Lấy lịch sử tin nhắn của phòng
Frontend gọi:

- `GET /message/get-mess-in-room?roomId=...`

Kết quả được gán vào danh sách `messages` để hiển thị trong khung chat.

#### 8. Gửi tin nhắn văn bản
Khi người dùng nhập nội dung và nhấn Enter hoặc nút **Send**:

- Frontend gọi `webSocketService.sendMessage(...)`
- Dữ liệu gửi đi gồm:
  - `sender`
  - `senderName`
  - `content`
  - `roomId`
  - `fileName`
  - `urlDowload`
  - `type = 'CHAT'`

Backend sẽ xử lý và phát lại tin nhắn qua topic realtime.

#### 9. Upload file trong cuộc trò chuyện
Khi người dùng bấm icon cộng:

1. Mở input file ẩn
2. Chọn file từ máy
3. Tạo `FormData`
4. Gọi API `POST /file/upload`
5. Nếu upload thành công:
   - Lấy `fileName` và `fileUrl` từ response
   - Gửi một message realtime để thông báo file đã được chia sẻ vào phòng chat

Điều này cho phép file xuất hiện như một message trong luồng hội thoại.

#### 10. Tải file đã chia sẻ
Khi bấm vào tên file trong chat:

- Frontend gọi `GET /file/dowload/{id}` với `responseType: 'blob'`
- Tạo `ObjectURL`
- Tự động tạo thẻ `a` và kích hoạt tải file về máy

#### 11. Hiển thị thông tin nhóm tin nhắn
Component có hàm `checkShow(index)` để quyết định có hiển thị tên người gửi hay không.

Tên người gửi sẽ được hiện lại khi:

- Đây là tin nhắn đầu tiên trong danh sách
- Người gửi khác với tin nhắn trước đó
- Hoặc cùng người gửi nhưng khoảng cách thời gian lớn hơn 5 phút

Cách này giúp giao diện chat gọn hơn, tránh lặp tên liên tục.

#### 12. Phân biệt tin nhắn gửi và nhận
Hàm `isSentByCurrentUser(index)` dùng để:

- So sánh `sender` của message với `user.id`
- Gắn class CSS khác nhau cho tin nhắn của mình và tin nhắn của người khác

#### 13. Rời phòng chat
Khi bấm **Leave Room**:

1. Tạo object `Member`
2. Gọi `POST /room/change-member` với trạng thái `MOVE`
3. Backend trả về số lượng thành viên mới
4. Frontend gửi lại thông tin đếm thành viên qua WebSocket
5. Reset `curentRoomId`

#### 14. Hết hạn phiên đăng nhập
Trong `ChatComponent` có hàm `getItemWithExpiry(key)`:

- Đọc dữ liệu từ `localStorage`
- Kiểm tra trường `expiry`
- Nếu quá hạn:
  - Xóa dữ liệu khỏi `localStorage`
  - Điều hướng về `/login`

Ngoài ra component còn chạy kiểm tra này theo chu kỳ **mỗi 1 phút**.

---

## 3.5. Màn hình quên mật khẩu

File chính:

- `src/app/forgot-password/forgot-password.component.ts`
- `src/app/forgot-password/forgot-password.component.html`

### Mục đích
Cho phép người dùng nhập email để yêu cầu khôi phục mật khẩu.

### Trạng thái hiện tại
Giao diện đã có:

- Ô nhập email
- Nút `Send Email`

Tuy nhiên logic hiện tại **chưa hoàn thiện**:

- `ngOnInit()` đang `throw new Error('Method not implemented.')`
- Hàm `updatePassword()` chưa có xử lý
- Chưa có service tương ứng để gửi yêu cầu quên mật khẩu lên backend

=> Có thể xem đây là chức năng đang được chuẩn bị, chưa dùng được ở thời điểm hiện tại.

---

## 3.6. Các màn hình demo/example

### `ExampleComponent`
File:
- `src/app/component/exampleGetList/example.component.ts`
- `src/app/component/exampleGetList/example.component.html`

Chức năng:
- Gọi API để lấy danh sách `Category`
- Hiển thị dữ liệu dạng bảng
- Điều hướng sang màn hình gửi dữ liệu `/send-data`

API được dùng:
- `POST /category/test-list`
- `GET /category/test-list?categoryName=...`

### `ExampleSentComponent`
File:
- `src/app/component/exampleSenparam/exampleSent.component.ts`
- `src/app/component/exampleSenparam/exampleSent.component.html`

Chức năng:
- Nhập dữ liệu `Category`
- Submit dữ liệu lên backend qua API tạo category
- Dùng `CustomInputComponent` để minh họa two-way binding với input tùy biến

API được dùng:
- `POST /category/create`

### Vai trò của nhóm màn hình demo
Nhóm này có vẻ phục vụ:

- thử nghiệm kết nối REST API
- minh họa form binding
- minh họa reusable component
- học tập hoặc kiểm thử giao tiếp frontend-backend

Đây không phải là phần nghiệp vụ chat cốt lõi.

---

## 4. Các service và trách nhiệm

## 4.1. `LoginService`
File: `src/app/service/login.service.ts`

Chịu trách nhiệm:
- Tạo tài khoản mới
- Tìm người dùng để đăng nhập

API:
- `POST /user/add`
- `GET /user/find-username`

---

## 4.2. `ChatRoomService`
File: `src/app/service/chatRoom.service.ts`

Chịu trách nhiệm:
- Tạo phòng chat
- Lấy danh sách phòng
- Tìm phòng theo tên
- Thay đổi trạng thái thành viên trong phòng
- Lấy số lượng thành viên của phòng

API:
- `POST /room/add-room`
- `GET /room/get-list-room`
- `GET /room/get-list-room-by-name`
- `POST /room/change-member`
- `GET /room/count-member`

---

## 4.3. `MessageService`
File: `src/app/service/message.service.ts`

Chịu trách nhiệm:
- Lấy lịch sử tin nhắn theo phòng

API:
- `GET /message/get-mess-in-room`

---

## 4.4. `FileService`
File: `src/app/service/file.service.ts`

Chịu trách nhiệm:
- Upload file
- Tải file về

API:
- `POST /file/upload`
- `GET /file/dowload/{id}`

---

## 4.5. `WebSocketService`
File: `src/app/service/websocket.service.ts`

Chịu trách nhiệm:
- Kết nối và ngắt kết nối WebSocket
- Subscribe kênh nhận message realtime
- Gửi tin nhắn chat
- Gửi cập nhật số lượng thành viên trong phòng

### WebSocket channel hiện dùng
- Kết nối: `http://localhost:8080/ws`
- Subscribe: `/topic/public`
- Publish chat: `/app/chat.sendMessage`
- Publish count member: `/app/chat.addUser`

---

## 5. Các model dữ liệu chính

## 5.1. `User`
File: `src/app/model/Login.ts`

Thuộc tính hiện có:
- `username`
- `password`
- `email`

Trong thực tế backend trả về có thể còn thêm các trường như:
- `id`
- `verified`

vì frontend đang sử dụng các trường này trong logic đăng nhập.

## 5.2. `ChatRoom`
File: `src/app/model/ChatRoom.ts`

Thuộc tính:
- `roomId`
- `roomName`

## 5.3. `Member`
File: `src/app/model/Member.ts`

Thuộc tính:
- `roomId`
- `status`
- `userId`

`status` hiện được dùng với các giá trị:
- `JOIN`
- `MOVE`

## 5.4. `Message`
File: `src/app/model/Message.ts`

Thuộc tính khai báo:
- `userName`
- `content`
- `timestamp`

Trong runtime, message thực tế còn được dùng thêm các field như:
- `sender`
- `senderName`
- `fileName`
- `urlDowload`
- `id`
- `type`
- `roomId`

## 5.5. `File`
File: `src/app/model/File.ts`

Thuộc tính:
- `fileName`
- `fileUrl`

## 5.6. `Category`
File: `src/app/model/Category.ts`

Thuộc tính:
- `categoryName`
- `describe`

Model này chủ yếu phục vụ phần demo.

---

## 6. Luồng nghiệp vụ chính của ứng dụng chat

### Luồng 1: Người dùng đăng nhập
1. Mở ứng dụng tại `/` hoặc `/login`
2. Nhập username và password
3. Frontend gọi API xác thực tài khoản
4. Nếu hợp lệ và đã verify:
   - lưu thông tin phiên vào `localStorage`
   - điều hướng tới `/chat`

### Luồng 2: Người dùng vào phòng chat
1. Frontend tải danh sách phòng
2. Người dùng chọn một phòng
3. Frontend báo backend rằng người dùng tham gia phòng
4. Nếu phòng còn chỗ:
   - lấy lịch sử tin nhắn
   - cập nhật số thành viên
   - bắt đầu trò chuyện realtime

### Luồng 3: Gửi tin nhắn realtime
1. Người dùng nhập nội dung
2. Frontend gửi message qua STOMP
3. Backend xử lý và broadcast
4. Tất cả client trong room nhận và hiển thị message

### Luồng 4: Chia sẻ file
1. Người dùng chọn file
2. Frontend upload file qua REST API
3. Backend trả về thông tin file
4. Frontend gửi message chat chứa metadata file
5. File xuất hiện trong hội thoại như một tin nhắn

### Luồng 5: Phiên đăng nhập hết hạn
1. Frontend đọc `expiry` của user trong `localStorage`
2. Nếu quá 30 phút:
   - xóa dữ liệu phiên
   - chuyển về màn hình đăng nhập

---

## 7. Thành phần giao diện dùng chung

File: `src/app/moduleCommon.component.ts`

Module dùng chung export các thành phần:
- `FormsModule`
- `CommonModule`
- `MatInputModule`
- `MatButtonModule`
- `MatCardModule`
- `MatIconModule`
- `MatListModule`

Mục tiêu là giúp các standalone component tái sử dụng nhanh các dependency giao diện phổ biến.

---

## 8. Khởi tạo ứng dụng

### Bootstrap frontend
- `src/main.ts` dùng `bootstrapApplication(AppComponent, appConfig)`
- `AppComponent` chỉ đóng vai trò khung chứa `router-outlet`

### App config
File `src/app/app.config.ts` cung cấp:
- router
- hydration
- http client
- animation async
- zone change detection

---

## 9. SSR và server Express

File: `server.ts`

Project có hỗ trợ SSR thông qua Angular Universal/Angular SSR:

- Dùng `CommonEngine`
- Dùng `Express` để phục vụ file tĩnh và render Angular ở server
- Port mặc định: `4000`

Điều này có thể hỗ trợ:
- cải thiện thời gian render ban đầu
- thân thiện hơn với SEO
- triển khai ứng dụng ở chế độ server-rendered

Tuy nhiên từ góc nhìn nghiệp vụ, phần này chủ yếu là **hạ tầng chạy ứng dụng**, không phải chức năng người dùng cuối.

---

## 10. Những điểm nổi bật của project

Project này có một số điểm đáng chú ý:

1. **Chat room realtime** là chức năng trung tâm
2. Kết hợp **REST API + WebSocket** khá rõ ràng
3. Có hỗ trợ **chia sẻ file trong phòng chat**
4. Có cơ chế **bảo vệ route đăng nhập**
5. Có cơ chế **phiên đăng nhập có thời hạn**
6. Có phần **demo API** để thử nghiệm nghiệp vụ riêng
7. Cấu trúc tương đối đơn giản, dễ mở rộng thêm tính năng

---

## 11. Hạn chế và phần chưa hoàn thiện

Dựa trên mã nguồn hiện tại, project còn một số điểm cần lưu ý:

1. **Chức năng quên mật khẩu chưa hoàn tất**
2. `AuthGuard` chưa kiểm tra expiry của phiên, chỉ kiểm tra sự tồn tại của `localStorage`
3. Một số model TypeScript chưa phản ánh đầy đủ dữ liệu thực tế backend trả về
4. Sau khi đăng ký tài khoản, giao diện chưa phản hồi rõ ràng cho người dùng
5. Tên một số field/hàm có lỗi chính tả như:
   - `curentRoomId`
   - `dowloadFile`
   - `urlDowload`
6. Chưa thấy xử lý lỗi HTTP/WebSocket đầy đủ trong nhiều service/component
7. Chưa có mô tả nghiệp vụ backend đi kèm nên frontend đang phụ thuộc vào contract ngầm giữa hai phía

---

## 12. Đề xuất hướng mở rộng

Nếu tiếp tục phát triển, có thể bổ sung các chức năng sau:

- Hoàn thiện quên mật khẩu và đặt lại mật khẩu
- Đăng xuất chủ động
- Tự refresh session hoặc redirect an toàn khi hết hạn
- Hiển thị trạng thái online/offline của thành viên
- Tạo phòng riêng tư hoặc phòng có mật khẩu
- Hiển thị danh sách thành viên trong phòng
- Thu hồi, sửa hoặc xóa tin nhắn
- Preview file/image trước khi tải
- Thông báo lỗi/thành công thân thiện hơn thay cho `alert`
- Kiểm tra form và validation đầy đủ hơn
- Tách interface/type rõ ràng cho response từ backend

---

## 13. Kết luận

Project này là một **frontend chat room realtime** xây dựng bằng Angular, phục vụ đăng nhập, quản lý phòng chat, gửi nhận tin nhắn tức thời và chia sẻ file. Phần chat đã có luồng chức năng chính tương đối rõ ràng, trong khi một số phần như quên mật khẩu và tinh chỉnh xác thực vẫn đang ở mức khởi tạo hoặc cần hoàn thiện thêm.

Nếu nhìn theo phạm vi hiện tại, đây là một dự án phù hợp cho:

- học và demo Angular standalone
- thực hành tích hợp REST + WebSocket
- xây dựng ứng dụng chat cơ bản theo room
- làm nền tảng để phát triển hệ thống chat hoàn chỉnh hơn trong tương lai

