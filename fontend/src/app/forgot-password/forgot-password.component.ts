import { Component, OnInit } from '@angular/core';
import { ModuleCommon } from '../moduleCommon.component';
import { User } from '../model/Login';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [ModuleCommon],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent implements OnInit{
  user : User = new User()
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }
  updatePassword(){

  }
}
