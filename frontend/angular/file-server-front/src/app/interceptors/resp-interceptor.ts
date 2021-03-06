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
import { Observable } from "rxjs";
import { catchError, filter } from "rxjs/operators";
import { Resp } from "src/models/resp";
import { Router } from "@angular/router";

/**
 * Intercept http response with 'Resp' as body
 */
@Injectable()
export class RespInterceptor implements HttpInterceptor {
  constructor(private router: Router) {}

  intercept(
    httpRequest: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(httpRequest).pipe(
      filter((e, i) => {
        if (!(e instanceof HttpResponse || e instanceof HttpHeaderResponse)) {
          return true;
        }
        console.log("Intercept HttpResponse:", e);
        if (e instanceof HttpResponse) {
          // normal http response with body, check if it has any error by field 'hasError'
          let r: Resp<any> = e.body as Resp<any>;
          if (r.hasError) {
            console.log(`Intercept HttpResponse, found error: ${r.msg}`);
            window.alert(r.msg);
            // filter out this value
            return false;
          }
        }
        return true;
      })
    );
  }
}
