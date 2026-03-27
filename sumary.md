# Sumary du an Chat

## 1. Tong quan du an

Du an gom 2 phan chinh:

- `backend/`: Spring Boot + MongoDB + WebSocket (STOMP/SockJS)
- `fontend/`: Angular 18 (standalone component) + REST API + WebSocket client

Muc tieu chinh la xay dung ung dung chat theo phong (chat room) voi cac tinh nang:

- Dang ky, dang nhap, xac minh email
- Tao phong chat, tim phong, tham gia/roi phong
- Gui/nhan tin nhan realtime
- Upload va tai file trong cuoc hoi thoai
- Kiem soat truy cap route chat bang guard

---

## 2. Kien truc tong the

### 2.1. Mo hinh giao tiep

- REST API xu ly nghiep vu CRUD/tra cuu:
  - User, room, message history, file upload/download
- WebSocket STOMP xu ly realtime:
  - Client gui vao `/app/*`
  - Server broadcast qua `/topic/public`

### 2.2. Cau hinh ket noi

- Backend chay mac dinh: `http://localhost:8080` (`backend/src/main/resources/application.properties`)
- Frontend goi API qua: `environment.apiUrl = http://localhost:8080` (`fontend/src/enviroment/environment.ts`)
- WebSocket endpoint backend: `/ws` (`backend/src/main/java/com/codreal/chatservice/config/WebSocketConfig.java`)

### 2.3. Cong nghe chinh

**Backend**
- Spring Boot 2.4.1
- Spring Web, Spring Data MongoDB
- Spring WebSocket (STOMP)
- Spring Mail
- ModelMapper

**Frontend**
- Angular 18
- Angular Router, HttpClient, Forms
- Angular Material
- SockJS + `@stomp/stompjs`
- Co cau hinh SSR voi Express (`fontend/server.ts`)

---

## 3. Chuc nang backend (chi tiet)

## 3.1. Quan ly tai khoan

### Dang ky tai khoan
- API: `POST /user/add`
- Luong xu ly:
  1. Nhan `email`, `username`, `password`
  2. Kiem tra email da ton tai
  3. Tao user voi `verified=false`
  4. Tao verification token (han 5 phut)
  5. Gui email xac minh
- Xu ly tai:
  - `UserController.addUser`
  - `UserServiceImpl.registerUser`

### Xac minh email
- API: `GET /auth/verify?token=...`
- Luong xu ly:
  1. Tim token
  2. Neu hop le: set `user.verified=true`, xoa token
  3. Redirect ve trang login frontend (`http://localhost:4200/login`)
- Luu y quan trong:
  - Co truong `expiryDate` nhung kiem tra het han token dang bi comment trong code.

### Dang nhap
- API: `GET /user/find-username?username=...&password=...`
- Backend tim user theo username + password.
- Tra ve `UserDto` de frontend xu ly tiep.
- Luu y:
  - Hien tai backend dang so khop mat khau plain text.
  - Chua ep buoc nguoi dung phai `verified=true` ngay trong API login.

### Quen mat khau (muc co ban)
- API: `GET /auth/reset-password?email=...`
- Chuc nang hien co: trigger gui email reset.
- Chua co flow day du de dat lai mat khau (token reset + endpoint doi mat khau).

## 3.2. Quan ly phong chat

### Tao phong
- API: `POST /room/add-room`
- `roomId` duoc sinh tu sequence `CHAT_ROOM_SEQUENCE`.
- Xu ly tai `ChatRoomServiceImpl.addRoom`.

### Lay danh sach phong
- API: `GET /room/get-list-room`
- Tra ve toan bo phong (chua phan trang o backend).

### Tim phong theo ten
- API: `GET /room/get-list-room-by-name?roomName=...`
- Tim theo `contains` (partial match) qua Mongo repository.

### Cap nhat trang thai thanh vien phong
- API: `POST /room/change-member`
- Input gom `roomId`, `userId`, `status` (`JOIN`, `MOVE`, ...)
- Logic:
  - Gioi han toi da `30` thanh vien o trang thai `JOIN`
  - Neu full va request `JOIN` -> tra `-1`
  - Neu hop le -> luu/cap nhat ban ghi `chatRoomResource`
  - Tra ve so thanh vien dang `JOIN`

### Dem so thanh vien
- API: `GET /room/count-member?roomId=...`
- Dem theo so ban ghi `status=JOIN`.

## 3.3. Tin nhan va realtime

### Gui tin nhan realtime
- Client publish: `/app/chat.sendMessage`
- Backend:
  - Nhan `MessageDto`
  - Gan `id` + `timestamp`
  - Luu vao collection `messages`
  - Broadcast ve `/topic/public`
- Xu ly tai `ChatController.sendMessage` + `MessageServiceImpl.addMessge`.

### Broadcast cap nhat so thanh vien
- Client publish: `/app/chat.addUser`
- Backend nhan `CountMemberDto` va broadcast lai `/topic/public`.

### Lay lich su tin nhan
- API: `GET /message/get-mess-in-room?roomId=...`
- Tra ve danh sach message theo `roomId`, sap xep theo `timestamp` tang dan.

## 3.4. File dinh kem

### Upload file
- API: `POST /file/upload` (multipart `file`)
- Luu file vao thu muc `uploads/`
- Tra ve:
  - `fileName`
  - `fileUrl` (`/files/{fileName}`)
- Co scheduler xoa file sau 10 gio (`FileCleanupService`).

### Download file
- API: `GET /file/dowload/{id}`
- Backend tim message theo `id`, lay `fileName`, doc file trong `uploads/` va tra attachment.

---

## 4. Chuc nang frontend (chi tiet)

## 4.1. Dieu huong va guard

Route chinh trong `fontend/src/app/app.routes.ts`:

- `/`, `/login` -> `LoginComponent`
- `/chat` -> `ChatComponent` (co `AuthGuard`)
- `/forgot-password` -> `ForgotPasswordComponent`
- `/example`, `/send-data` -> man hinh demo

`AuthGuard`:
- Kiem tra ton tai key `user` trong `localStorage`
- Neu khong co thi redirect `/login`
- Chua kiem tra expiry tai guard.

## 4.2. Dang nhap / dang ky

File: `fontend/src/app/login/login.component.ts`

- Dang ky:
  - Goi `POST /user/add`
- Dang nhap:
  - Goi `GET /user/find-username`
  - Neu `verified=true`:
    - Luu session vao `localStorage` voi TTL 30 phut
    - Dieu huong sang `/chat`
  - Neu chua verify -> bao `Account not verified`

## 4.3. Man hinh chat

File: `fontend/src/app/chat/chat.component.ts`

Khi vao trang chat (`ngOnInit`):
1. Doc user tu `localStorage` co check expiry
2. Bat interval moi 1 phut de kiem tra het han session
3. Ket noi WebSocket
4. Subscribe luong `/topic/public`
5. Tai danh sach phong

Xu ly message realtime:
- `type=CHAT` -> them vao danh sach `messages`
- `type=COUNT` -> cap nhat `userCount`

Tinh nang tai man hinh chat:
- Hien thi danh sach phong + phan trang client-side (20 phong/trang)
- Tim phong theo ten
- Tao phong moi
- Tham gia phong (`JOIN`) va roi phong (`MOVE`)
- Lay lich su tin nhan theo phong
- Gui tin nhan text realtime
- Upload file roi gui metadata file nhu mot message chat
- Download file theo message id
- Gom nhom hien thi ten nguoi gui theo quy tac 5 phut (`checkShow`)
- Phan biet bong bong tin nhan cua minh/nguoi khac (`isSentByCurrentUser`)

## 4.4. Services frontend

- `LoginService`: dang ky + dang nhap
- `ChatRoomService`: tao phong, lay/tim phong, doi member status, dem member
- `MessageService`: lay lich su message
- `FileService`: upload/download file
- `WebSocketService`: connect/disconnect, publish/subcribe STOMP

## 4.5. Quen mat khau frontend

File: `fontend/src/app/forgot-password/forgot-password.component.ts`

Trang thai hien tai: chua hoan thien.
- `ngOnInit()` dang `throw new Error(...)`
- Chua co logic goi backend de reset password

## 4.6. Man hinh demo

- `ExampleComponent`, `ExampleSentComponent`, `CustomInputComponent`
- Muc dich: demo goi API va binding
- Luu y: tai backend hien tai khong co endpoint `category/*`, nen nhom demo nay co the la code thu nghiem/du an phu.

---

## 5. Du lieu va collection MongoDB

Cac collection chinh:

- `users`: thong tin tai khoan + `verified`
- `verification_tokens`: token xac minh email
- `chatRoom`: thong tin phong
- `chatRoomResource`: quan he user-phong + trang thai (`JOIN`/`MOVE`)
- `messages`: noi dung chat + thong tin file + timestamp
- `chats_database_sequences`: bo dem sequence sinh id

---

## 6. API quick reference

### Auth
- `GET /auth/verify?token=...`
- `GET /auth/reset-password?email=...`

### User
- `POST /user/add`
- `GET /user/find-username?username=...&password=...`

### Room
- `POST /room/add-room`
- `GET /room/get-list-room`
- `GET /room/get-list-room-by-name?roomName=...`
- `POST /room/change-member`
- `GET /room/count-member?roomId=...`

### Message
- `GET /message/get-mess-in-room?roomId=...`

### File
- `POST /file/upload`
- `GET /file/dowload/{id}`
- `GET /files/{fileName}`

### WebSocket
- Endpoint: `/ws`
- Publish: `/app/chat.sendMessage`, `/app/chat.addUser`
- Subscribe: `/topic/public`

---

## 7. Diem manh hien tai

- Co du luong chat room realtime ro rang (REST + WebSocket)
- Co file sharing trong hoi thoai
- Co co che gioi han so thanh vien phong
- Kien truc tach backend/frontend ro, de mo rong
- Co ho tro SSR o frontend neu can trien khai

---

## 8. Han che va rui ro ky thuat can uu tien

1. Bao mat:
- Mat khau dang luu plain text
- Chua bat Spring Security
- Thong tin email credential dang dat truc tiep trong `application.properties`

2. Xac thuc va tai khoan:
- Kiem tra het han verification token chua duoc bat
- Login API chua enforce `verified`
- Quen mat khau chua co flow hoan chinh

3. Frontend:
- `ForgotPasswordComponent` chua dung duoc
- `AuthGuard` chua check expiry session
- Mot so ten field/ham sai chinh ta (`dowload`, `curentRoomId`, `urlDowload`)

4. API contract:
- Nhom man hinh demo `category/*` khong thay endpoint tuong ung o backend

---

## 9. De xuat huong phat trien tiep

- Ma hoa mat khau bang BCrypt + bo sung Spring Security/JWT
- Hoan thien reset password end-to-end
- Bat enforce `verified=true` trong luong dang nhap backend
- Chuan hoa API response + handling loi frontend
- Them phan trang server-side cho room/message neu du lieu lon
- Hoan thien bo test cho luong chat, auth va file

---

## 10. Ket luan

Day la du an chat room full-stack co nen tang kha day du cho mot san pham MVP:

- Da co luong tai khoan co xac minh email
- Da co chat realtime theo phong
- Da co upload/download file

Nhung de san sang production, du an can uu tien nang cap bao mat, hoan thien reset password, va chuan hoa contract giua frontend-backend.

