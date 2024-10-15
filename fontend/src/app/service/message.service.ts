import { User } from './../model/Login';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../enviroment/environment'
@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private apiUrl = environment.apiUrl; 

  constructor(private http: HttpClient) {}


  getMessInRoom(roomId: string): Observable<Object>{
    return this.http.get(`${this.apiUrl}/message/get-mess-in-room`, {
        params: {
            roomId : roomId,
           
        }
    });
  }
}