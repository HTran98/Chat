import { LoginComponent } from './login/login.component';
import { Routes } from '@angular/router';
import {ExampleComponent} from './component/exampleGetList/example.component'
import { ExampleSentComponent } from './component/exampleSenparam/exampleSent.component';
import { ChatComponent } from './chat/chat.component';
import { AuthGuard } from './auth.guard';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
export const routes: Routes = [
    {path:'',component: LoginComponent},
    {path:'login',component: LoginComponent},
    {path: 'example', component: ExampleComponent},
    {path: 'send-data', component: ExampleSentComponent},
    {path:'chat', component: ChatComponent, canActivate: [AuthGuard]},
    {path:'forgot-password', component: ForgotPasswordComponent},
];
