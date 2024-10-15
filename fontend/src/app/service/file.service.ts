import { User } from './../model/Login';
import { Category } from './../model/Category';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../enviroment/environment'
@Injectable({
  providedIn: 'root'
})
export class FileService {
  private apiUrl = environment.apiUrl; 

  constructor(private http: HttpClient) {}


  uploadFile(formData: FormData): Observable<Object>{
    return this.http.post(`${this.apiUrl}/file/upload`, formData);
  }
  dowloadFile(id : string):Observable<Object>{
    return this.http.get(`${this.apiUrl}/file/dowload/${id}`,{ responseType: 'blob' })
  }
}