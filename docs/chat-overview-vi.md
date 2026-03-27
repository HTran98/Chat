# Tong quan du an Chat

Tai lieu nay la ban tong quan cap cao (high-level), tap trung vao gia tri nghiep vu, luong su dung chinh, va cac thanh phan ky thuat cot loi.

## 1. Muc tieu he thong

Du an Chat duoc xay dung de cung cap nen tang giao tiep theo phong (chat room) voi 3 muc tieu chinh:

- Quan ly dinh danh va truy cap nguoi dung an toan o muc co ban.
- Ho tro thao tac phong chat nhanh, ro trang thai thanh vien.
- Cung cap trai nghiem nhan tin thoi gian thuc, kem chia se file.

Pham vi he thong gom:

- `backend/`: Spring Boot + MongoDB + WebSocket/STOMP.
- `fontend/`: Angular 18 + REST API + SockJS/STOMP client.

## 2. Kien truc tong the

He thong ket hop 2 kenh giao tiep:

- REST API: xu ly nghiep vu dang ky/dang nhap, phong chat, lich su tin nhan, upload/download file.
- WebSocket: xu ly su kien realtime (tin nhan moi, cap nhat so thanh vien).

Luot giao tiep tong quan:

1. Frontend goi REST de tai du lieu nen (user, room, message history).
2. Frontend mo ket noi WebSocket de nhan/phan phoi su kien tuc thoi.
3. Backend luu tru du lieu vao MongoDB va broadcast su kien qua `/topic/public`.

## 3. Nhom chuc nang He thong & Tai khoan

Day la lop dinh danh va quan ly session cho nguoi dung.

### 3.1 Dang ky tai khoan (Register)

- Nguoi dung nhap `username`, `email`, `password` tai `LoginComponent`.
- Frontend goi `POST /user/add`.
- Backend (`UserServiceImpl`) tao user moi voi `verified=false`.
- He thong sinh verification token va gui email kich hoat qua `EmailService`.

Gia tri nghiep vu:
- Chan dang nhap truc tiep bang tai khoan chua xac thuc.
- Giam tai khoan ao va nang do tin cay dinh danh.

### 3.2 Xac thuc tai khoan (Verification)

- Nguoi dung click link trong email.
- Backend xu ly `GET /auth/verify?token=...`.
- Neu token hop le, user duoc danh dau `verified=true` va dieu huong ve trang dang nhap.

Luu y hien trang:
- Token co `expiryDate`, nhung logic kiem tra han hien chua duoc enforce day du.

### 3.3 Dang nhap va quan ly session (Login & Session)

- Dang nhap qua `GET /user/find-username?username=...&password=...`.
- Frontend kiem tra trang thai `verified` de quyet dinh cho vao chat.
- Thong tin user duoc luu vao `localStorage` kem TTL 30 phut.
- Co co che tu dong het han session qua `getItemWithExpiry` va redirect ve `/login`.

Gia tri nghiep vu:
- Dam bao chi nguoi dung hop le moi vao luong chat.
- Giu trai nghiem on dinh voi co che session don gian, de theo doi.

### 3.4 Quen mat khau (Forgot Password)

- Backend co endpoint `GET /auth/reset-password?email=...` de gui email reset.
- Frontend da co man hinh `ForgotPasswordComponent` nhung chua hoan thien flow end-to-end.

## 4. Nhom chuc nang Phong Chat (Chat Room)

Nhom nay quan ly khong gian giao tiep va trang thai thanh vien.

### 4.1 Tao phong chat

- Nguoi dung tao phong trong popup tai `ChatComponent`.
- Frontend goi `POST /room/add-room`.
- Backend sinh `roomId` tang dan qua `SequenceGeneratorService`.

### 4.2 Tim kiem phong chat

- Ho tro tim theo ten phong qua `GET /room/get-list-room-by-name?roomName=...`.
- Co the kich hoat theo Enter/tuong tac tim kiem tu giao dien.

### 4.3 Phan trang danh sach phong

- Danh sach phong duoc chia trang o frontend, mac dinh `20` phong/trang.
- Muc tieu la giu UI gon, de dieu huong khi so phong lon.

### 4.4 Quan ly thanh vien phong

- Tham gia phong (Join):
  - Goi `POST /room/change-member` voi trang thai `JOIN`.
  - Backend gioi han toi da `30` thanh vien/room.
  - Neu day, tra ket qua am va UI hien thi `Chat room is full`.

- Roi phong (Leave):
  - Goi `POST /room/change-member` voi trang thai `MOVE`.
  - Sau khi cap nhat, frontend phat su kien cap nhat si so qua WebSocket.

### 4.5 Theo doi si so thuc te

- So nguoi dang online trong phong duoc hien thi qua `userCount`.
- Co the lay boi REST (`GET /room/count-member`) va dong bo realtime qua WebSocket.

Gia tri nghiep vu:
- Kiem soat suc chua phong, tranh qua tai.
- Minh bach so nguoi dang hien dien trong moi phong.

## 5. Nhom chuc nang Nhan tin & Tuong tac (Messaging & Real-time)

Day la loi nghiep vu trung tam cua ung dung.

### 5.1 Chat realtime (WebSocket)

- Frontend ket noi endpoint `/ws` bang SockJS/STOMP.
- Gui tin nhan den `/app/chat.sendMessage`.
- Backend luu message va broadcast ra `/topic/public`.
- Moi client subscribe cung kenh se nhan duoc cap nhat ngay lap tuc.

### 5.2 Lich su tin nhan

- Khi chon phong, frontend goi `GET /message/get-mess-in-room?roomId=...`.
- He thong tai lich su theo phong de nguoi dung tiep tuc hoi thoai.

### 5.3 Giao dien tin nhan thong minh

- Phan biet tin nhan gui/nhan de canh trai-phai ro rang.
- Ham `checkShow` chi hien ten nguoi gui khi:
  - la tin nhan dau chuoi, hoac
  - doi nguoi gui, hoac
  - cung nguoi gui nhung cach nhau qua 5 phut.

Tac dung:
- Giam nhieu giao dien, tang kha nang doc hoi thoai dai.

### 5.4 Truyen tai file trong chat

- Upload file qua `POST /file/upload` (`FileService`).
- Sau khi upload thanh cong, frontend gui metadata file (`fileName`, `fileUrl`) qua WebSocket nhu 1 chat message.
- Nguoi dung click ten file de tai qua `GET /file/dowload/{id}`.
- Backend co `FileCleanupService` tu dong xoa file sau 10 gio de tiet kiem tai nguyen.

## 6. Luong nghiep vu tieu bieu (end-to-end)

### Luong A: Dang ky va kich hoat

1. User dang ky tai khoan.
2. He thong gui email verification.
3. User click link kich hoat.
4. User quay lai dang nhap va vao chat.

### Luong B: Vao phong va chat

1. User dang nhap hop le.
2. Chon phong (hoac tao phong moi).
3. Join phong, tai lich su tin nhan.
4. Gui/nhan tin nhan realtime.
5. Roi phong khi ket thuc.

### Luong C: Chia se file

1. User chon file trong khung chat.
2. File upload len server.
3. Metadata file duoc phat vao hoi thoai qua WebSocket.
4. Thanh vien khac click de tai file.

## 7. Trang thai hien tai va diem can uu tien

Nhung diem da co gia tri su dung tot:

- Full luong chat room realtime co ban da hoat dong.
- Da co co che gioi han thanh vien phong.
- Da co chia se file trong hoi thoai.

Nhung diem can uu tien nang cap:

- Bao mat mat khau (hien plain text), bo sung BCrypt/Spring Security.
- Enforce chat che hon cho verification token va login policy.
- Hoan thien flow Forgot Password day du.
- Chuan hoa API contract va xu ly loi o frontend.

## 8. Ban do thanh phan tham chieu nhanh

- Tai khoan: `UserController`, `AuthController`, `UserServiceImpl`, `LoginComponent`
- Session/Guard: `AuthGuard`, `ChatComponent.getItemWithExpiry`
- Phong chat: `ChatRoomController`, `ChatRoomServiceImpl`, `ChatRoomResourceServiceImpl`
- Messaging: `ChatController`, `MessageController`, `MessageServiceImpl`, `WebSocketService`
- File: `FileController`, `FileService`, `FileCleanupService`

---

Tai lieu nay duoc viet theo muc tieu tong quan de onboarding nhanh PM/BA/dev. De xem chi tiet API va hanh vi tung man hinh, tham chieu them `sumary.md` va `fontend/docs/project-functionality.md`.

