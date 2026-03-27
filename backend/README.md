# Chat Service Backend - Functional Description (Reviewed)

## Scope of this document

This document describes the current behavior of the backend in `backend/` and clarifies which items in the feature list are backend-confirmed, frontend-side, or partially implemented.

Legend:

- `Confirmed`: visible in backend code and API.
- `Partial`: exists but not fully enforced or missing a complete flow.
- `Frontend`: behavior belongs to Angular/UI layer, not backend code in this repo.

## 1) System & Account Features

### Register account

- Status: `Confirmed`
- Flow:
  1. Client calls `POST /user/add` with `email`, `username`, `password`.
  2. Backend checks duplicate email in `UserServiceImpl`.
  3. Backend creates user with `verified=false`.
  4. Backend generates verification token and sends email via `EmailServiceImpl`.

### Email verification

- Status: `Partial`
- Flow:
  1. User opens `GET /auth/verify?token=...`.
  2. Backend finds token, sets user `verified=true`, deletes used token, then redirects to frontend login page.
- Important note:
  - Token expiry is stored (`expiryDate`) but currently not enforced in `AuthController` (the check is commented).

### Login and session management

- Login check in backend: `Partial`
  - Existing API `GET /user/find-username?username=...&password=...` checks username/password.
  - `verified` status is currently not enforced in this API.
- Session TTL 30 minutes and `localStorage` handling (`getItemWithExpiry`): `Frontend`
  - This is client-side behavior, not implemented by backend.

### Forgot password

- Status: `Partial`
- Existing behavior:
  - `GET /auth/reset-password?email=...` triggers email sending with a reset link.
- Limitation:
  - No full reset-password token validation + password update endpoint is present in backend yet.

## 2) Chat Room Features

### Create chat room

- Status: `Confirmed`
- API: `POST /room/add-room`
- Backend auto-generates increasing `roomId` using `SequenceGeneratorService` (`CHAT_ROOM_SEQUENCE`).

### Search room by name

- Status: `Confirmed`
- API: `GET /room/get-list-room-by-name?roomName=...`

### Pagination (20 rooms/page)

- Status: `Frontend/Not implemented in backend`
- Backend currently returns full list from `GET /room/get-list-room`.
- If pagination is required server-side, add endpoint params like `page` and `size`.

### Join/Leave room and member limit

- Status: `Confirmed` (for data update), `Partial` (for realtime notification contract)
- API: `POST /room/change-member`
- Behavior in `ChatRoomResourceServiceImpl`:
  - Supports status updates (for example `JOIN`, `MOVE`, or other client-sent status values).
  - Maximum 30 members in `JOIN` state per room.
  - Returns `-1` if room is full on join request.
  - Returns current member count (`JOIN`) after update.

### Track online member count

- Status: `Confirmed`
- API: `GET /room/count-member?roomId=...`
- Count is derived from records with `status=JOIN` in `chatRoomResource`.

## 3) Messaging & Realtime Features

### Realtime chat (WebSocket STOMP/SockJS)

- Status: `Confirmed`
- WebSocket config:
  - Endpoint: `/ws` (SockJS enabled)
  - Client send prefix: `/app`
  - Broker topic: `/topic`
- Messaging:
  - Client sends to `/app/chat.sendMessage`.
  - Backend saves message, sets generated ID, broadcasts to `/topic/public`.

### Message history

- Status: `Confirmed`
- API: `GET /message/get-mess-in-room?roomId=...`
- Backend returns messages sorted by timestamp.

### Smart UI display (`checkShow`, left/right bubble, 5-minute grouping)

- Status: `Frontend`
- This is presentation logic and not part of backend implementation.

### File attachment flow

- Status: `Confirmed`
- Upload:
  - API: `POST /file/upload` (multipart `file`)
  - Stores file in `uploads/`
  - Returns `fileName` and static `fileUrl` (`/files/{fileName}`)
- Download:
  - API: `GET /file/dowload/{id}`
  - Resolves message by ID, then returns corresponding file as attachment.
- Cleanup:
  - `FileCleanupService` schedules deletion after 10 hours.

## 4) API Quick Reference

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

- Connect endpoint: `/ws`
- Send: `/app/chat.sendMessage`, `/app/chat.addUser`
- Subscribe: `/topic/public`

## 5) Data Model (MongoDB)

- `users`: id, email, username, password, verified
- `verification_tokens`: id, token, userId, expiryDate
- `chatRoom`: roomId, roomName
- `chatRoomResource`: id, roomId, userId, status
- `messages`: id, sender, senderName, content, roomId, timestamp, fileName, urlDowload
- `chats_database_sequences`: sequence counters for generated IDs

Note: legacy `Chat`/`ChatService` still exists (`chats` collection style), but current primary flow uses `ChatRoom` + `Message` + WebSocket.

## 6) Security and Improvement Notes

- Password is stored in plain text currently; use BCrypt and Spring Security.
- Move mail credentials out of `application.properties` to environment variables.
- Add strict login policy to block unverified users.
- Implement complete reset-password flow (token issue/verify/change password).
- Add server-side pagination for room and message lists if needed.
