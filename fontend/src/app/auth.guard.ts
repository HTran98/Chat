// import { CanActivateFn } from '@angular/router';

// export const authGuard: CanActivateFn = (route, state) => {
//   return true;
// };
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    // Lấy thông tin người dùng từ local storage
    const user = JSON.parse(localStorage.getItem('user') || 'null');

    // Kiểm tra xem người dùng có tồn tại không
    if (user) {
      return true; // Cho phép truy cập nếu đã đăng nhập
    } else {
      this.router.navigate(['/login']); // Chuyển hướng về màn hình đăng nhập
      return false; // Không cho phép truy cập
    }
  }
}

