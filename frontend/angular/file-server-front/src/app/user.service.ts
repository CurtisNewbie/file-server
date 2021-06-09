import { Injectable, Output } from "@angular/core";
import { EventEmitter } from "@angular/core";
import { Observable } from "rxjs";
import { Resp } from "src/models/resp";
import { HttpClientService } from "./http-client-service.service";

@Injectable({
  providedIn: "root",
})
export class UserService {
  constructor(private httpClient: HttpClientService) {}

  /**
   * Attempt to signin
   * @param username
   * @param password
   */
  public login(username: string, password: string): Observable<Resp<any>> {
    return this.httpClient.login(username, password);
  }

  /**
   * Logout current user
   */
  public logout(): Observable<void> {
    return this.httpClient.logout();
  }
}
