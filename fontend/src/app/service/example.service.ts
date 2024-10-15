import { Category } from './../model/Category';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../enviroment/environment'
@Injectable({
  providedIn: 'root'
})
export class ExampleService {
  private apiUrl = environment.apiUrl; 

  constructor(private http: HttpClient) {}

  getData(category: Category): Observable<any> {
    return this.http.post(`${this.apiUrl}/category/test-list`, category);
  }
  createCategory(category: Category): Observable<Object>{
    return this.http.post(`${this.apiUrl}/category/create`, category);
  }
  getDataList(category: Category): Observable<any> {
    return this.http.get(`${this.apiUrl}/category/test-list`, {
      params: {
        categoryName : category.categoryName
      }
    });
  }
}