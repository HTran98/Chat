import { User } from './../model/Login';
import { Component, OnInit } from '@angular/core';
import { ModuleCommon } from '../moduleCommon.component';
import { LoginService } from '../service/login.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ModuleCommon
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{
  user : User = new User()
  registFlag : boolean = false
  constructor(private loginService: LoginService, private router: Router) {
      
  }
  ngOnInit(): void {
    
  }
  registUser (){
    this.loginService.createUser(this.user).subscribe((data) => {
      
    });
  }
  login(){
        this.loginService.findUser(this.user).subscribe((data: any) => {
          if(data){
            if(data.verified){
              const now = new Date();
              // `TTL` (TIME TO LIVE) TÍNH BẰNG MILLISECONDS
              const ttl = 30 * 60 * 1000
              const item = {
                value: data,
                expiry: now.getTime() + ttl,
              };
              
              localStorage.setItem('user', JSON.stringify(item));
              this.router.navigate(['chat'])
            } else {
              if(data.id){
                alert("Account not verified")
              }else{
                alert("Name or password is incorrect")
              }
            } 
            
          } 
        })
  }
  goRegistUser(){
     this.registFlag = true
  }
  forgotPassword(){
    this.router.navigate(['forgot-password'])
  }
}
