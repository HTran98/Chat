import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import  SockJS from 'sockjs-client';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient!: Client;
  private messageSubject: BehaviorSubject<string> = new BehaviorSubject<string>('');

  constructor() {}

  connect() {
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = new Client({
      webSocketFactory: () => socket as any,
      // debug: (str) => console.log(str),
    });

    this.stompClient.onConnect = (frame) => {
      this.stompClient.subscribe('/topic/public', (message: Message) => {
        this.onMessageReceived(message);
      });
    };

    this.stompClient.onStompError = (frame) => {

    };

    this.stompClient.activate();
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
  }

  sendMessage(sender: string,senderName: string, content: string, roomId: string, fileName: string, urlDowload: string, type: string = 'CHAT') {
    const chatMessage = {
      sender: sender,
      senderName : senderName,
      content: content,
      fileName : fileName,
      urlDowload: urlDowload,
      type: type,
      roomId: roomId
    };
    this.stompClient.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify(chatMessage),
    });
  }
  sendCountMember(roomId: string, countMember: number, type: string = 'COUNT') {
    const chatMessage = {
      roomId: roomId,
      countMember : countMember,
      type: type
    };
    this.stompClient.publish({
      destination: '/app/chat.addUser',
      body: JSON.stringify(chatMessage),
    });
  }

  onMessageReceived(message: Message) {
    this.messageSubject.next(message.body);
  }

  getMessage(): Observable<string> {
    return this.messageSubject.asObservable();
  }
}
