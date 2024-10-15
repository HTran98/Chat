import { User } from './../model/Login';
import { Category } from './../model/Category';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../enviroment/environment'
@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private apiUrl = environment.apiUrl; 

  constructor(private http: HttpClient) {}


  createUser(user: User): Observable<Object>{
    return this.http.post(`${this.apiUrl}/user/add`, user);
  }
  findUser(user: User): Observable<Object>{
    return this.http.get(`${this.apiUrl}/user/find-username`, {
        params: {
            username : user.username,
            password : user.password
        }
    });
  }
}