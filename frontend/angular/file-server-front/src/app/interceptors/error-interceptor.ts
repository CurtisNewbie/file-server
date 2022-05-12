import { Injectable } from "@angular/core";
import {
  HttpInterceptor,
  HttpEvent,
  HttpResponse,
  HttpRequest,
  HttpHandler,
  HttpErrorResponse,
  HttpHeaderResponse,
} from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError, filter } from "rxjs/operators";
import { Resp } from "src/models/resp";
import { Router } from "@angular/router";
import { UserService } from "../user.service";
import { NotificationService } from "../notification.service";

/**
 * Intercept http error response
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private router: Router,
    private userService: UserService,
    private notifi: NotificationService
  ) {}

  intercept(
    httpRequest: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(httpRequest).pipe(
      catchError((e) => {
        if (e instanceof HttpErrorResponse) {
          console.log("Http error response status:", e.status);

          if (e.status === 401 || e.status === 403) {
            this.notifi.toast("Please login first");
            this.setLogout();
          } else {
            this.notifi.toast("Unknown server error, please try again later");
          }
          return throwError(e);
        }
      })
    );
  }

  private setLogout(): void {
    this.userService.logout();
  }
}
