import { User } from './../model/Login';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../enviroment/environment'
import { ChatRoom } from '../model/ChatRoom';
@Injectable({
  providedIn: 'root'
})
export class ChatRoomService {
  private apiUrl = environment.apiUrl; 

  constructor(private http: HttpClient) {}


  createRoom(chatRoom: ChatRoom): Observable<Object>{
    return this.http.post(`${this.apiUrl}/room/add-room`, chatRoom);
  }
  getLstRoom(): Observable<Object>{
    return this.http.get(`${this.apiUrl}/room/get-list-room`);
  }
  getLstRoomByName(roomName: string): Observable<Object>{
    return this.http.get(`${this.apiUrl}/room/get-list-room-by-name`, {
        params: {
            roomName: roomName
        }
    });
  }
}