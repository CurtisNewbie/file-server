import { Injectable, Output } from "@angular/core";
import { EventEmitter } from "@angular/core";
import { Observable } from "rxjs";
import { Resp } from "src/models/resp";
import { UserInfo } from "src/models/user-info";
import { HttpClientService } from "./http-client-service.service";

@Injectable({
  providedIn: "root",
})
export class UserService {
  private userInfo: UserInfo = null;
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

  /**
   * Add guest user, only admin is allowed to add user
   * @param username
   * @param password
   * @returns
   */
  public addGuest(username: string, password: string): Observable<Resp<any>> {
    return this.httpClient.addGuest(username, password);
  }

  /**
   * Fetch user info
   */
  public fetchUserInfo(): Observable<Resp<UserInfo>> {
    return this.httpClient.fetchUserInfo();
  }

  /**
   * Set user info that is previously fetched
   */
  public setUserInfo(userInfo: UserInfo): void {
    this.userInfo = userInfo;
  }

  /**
   * Get user info that is previously fetched
   */
  public getUserInfo(): UserInfo {
    return this.userInfo;
  }

  /**
   * Check if the service has the user info already
   */
  public hasUserInfo(): boolean {
    return this.userInfo != null;
  }

  /**
   * Fetch list of user infos (only admin is allowed to do so)
   */
  public fetchUserList(): Observable<Resp<UserInfo[]>> {
    return this.httpClient.fetchUserList();
  }
}
