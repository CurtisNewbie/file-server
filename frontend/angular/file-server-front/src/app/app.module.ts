import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";

import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { HomePageComponent } from "./home-page/home-page.component";
import { APP_BASE_HREF } from "@angular/common";
import { LoginComponent } from "./login/login.component";
import { ManagerUserComponent } from "./manager-user/manager-user.component";
import { HttpClientModule, HTTP_INTERCEPTORS } from "@angular/common/http";
import { FormsModule } from "@angular/forms";
import { NavComponent } from "./nav/nav.component";
import { AccessLogComponent } from "./access-log/access-log.component";
import { RespInterceptor } from "./interceptors/resp-interceptor";
import { ErrorInterceptor } from "./interceptors/error-interceptor";
import { ChangePasswordComponent } from './change-password/change-password.component';

@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    LoginComponent,
    ManagerUserComponent,
    NavComponent,
    AccessLogComponent,
    ChangePasswordComponent,
  ],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule, FormsModule],
  providers: [
    { provide: APP_BASE_HREF, useValue: "/" },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: RespInterceptor, multi: true },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
