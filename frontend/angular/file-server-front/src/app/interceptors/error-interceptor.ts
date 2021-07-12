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

/**
 * Intercept http error response
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private router: Router, private userService: UserService) {}

  intercept(
    httpRequest: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(httpRequest).pipe(
      catchError((e) => {
        if (e instanceof HttpErrorResponse) {
          console.log("Http error response status:", e.status);
          // status code 5xx or 0, redirect to login page
          if (e.status >= 500 || e.status == 0) {
            window.alert("Server is down");
            this.setLogout();
          } else if (e.status === 401 || e.status === 403) {
            // status code 401 or 403, redirect to login page
            window.alert("Please login first");
            this.setLogout();
          } else {
            // other status code
            window.alert("Unknown server error");
          }
          return throwError(e);
        }
      })
    );
  }

  private setLogout(): void {
    this.userService.setLogout();
  }
}
