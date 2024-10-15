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
      debug: (str) => console.log(str),
    });

    this.stompClient.onConnect = (frame) => {
      console.log('Connected: ' + frame);
      this.stompClient.subscribe('/topic/public', (message: Message) => {
        this.onMessageReceived(message);
      });
    };

    this.stompClient.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };

    this.stompClient.activate();
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      console.log('Disconnected');
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

  onMessageReceived(message: Message) {
    this.messageSubject.next(message.body);
  }

  getMessage(): Observable<string> {
    return this.messageSubject.asObservable();
  }
}
