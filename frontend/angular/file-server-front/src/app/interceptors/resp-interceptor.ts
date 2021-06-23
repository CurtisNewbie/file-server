import { Injectable } from "@angular/core";
import {
  HttpInterceptor,
  HttpEvent,
  HttpResponse,
  HttpRequest,
  HttpHandler,
} from "@angular/common/http";
import { Observable } from "rxjs";
import { filter } from "rxjs/operators";
import { Resp } from "src/models/resp";

/**
 * Intercept http response, and filter out those with error
 */
@Injectable()
export class RespInterceptor implements HttpInterceptor {
  intercept(
    httpRequest: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(httpRequest).pipe(
      filter((e, i) => {
        if (e instanceof HttpResponse) {
          let r: Resp<any> = e.body as Resp<any>;
          if (r.hasError) {
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
