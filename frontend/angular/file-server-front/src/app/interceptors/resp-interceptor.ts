import { Injectable } from "@angular/core";
import {
  HttpInterceptor,
  HttpEvent,
  HttpResponse,
  HttpRequest,
  HttpHandler,
} from "@angular/common/http";
import { Observable, Subject } from "rxjs";
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
    let s = new Subject<any>();
    let o = s.asObservable();

    next.handle(httpRequest).subscribe({
      next: (e) => {
        if (e instanceof HttpResponse) {
          let r: Resp<any> = e.body as Resp<any>;
          if (r.hasError) {
            window.alert(r.msg);
            // complete the subject, thus close all subscriptions
            s.complete();
            return;
          }
        }
        s.next(e);
      },
    });
    return o;
  }
}
