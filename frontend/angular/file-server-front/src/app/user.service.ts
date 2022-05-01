import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { Resp } from "src/models/resp";
import { ChangePasswordParam, UserInfo } from "src/models/user-info";
import { NavigationService, NavType } from "./navigation.service";
import { NotificationService } from "./notification.service";
import { buildApiPath, buildOptions, setToken } from "./util/api-util";

@Injectable({
  providedIn: "root",
})
export class UserService {
  private roleSubject = new Subject<string>();
  private isLoggedInSubject = new Subject<boolean>();
  private userInfoSubject = new Subject<UserInfo>();

  userInfoObservable: Observable<UserInfo> =
    this.userInfoSubject.asObservable();
  roleObservable: Observable<string> = this.roleSubject.asObservable();
  isLoggedInObservable: Observable<boolean> =
    this.isLoggedInSubject.asObservable();

  constructor(
    private http: HttpClient,
    private nav: NavigationService,
    private notifi: NotificationService
  ) {}

  /**
   * Attempt to sign-in
   * @param username
   * @param password
   */
  public login(username: string, password: string): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/user/login", "auth-service"),
      {
        username: username,
        password: password,
      },
      {
        withCredentials: true,
      }
    );
  }

  /**
   * Logout current user
   */
  public logout(): void {
    setToken(null);
    this.notifyLoginStatus(false);
    this.nav.navigateTo(NavType.LOGIN_PAGE);
  }

  /**
   * Add user, only admin is allowed to add user
   * @param username
   * @param password
   * @returns
   */
  public addUser(
    username: string,
    password: string,
    userRole: string
  ): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/user/register", "auth-service"),
      { username, password, userRole },
      buildOptions()
    );
  }

  /**
   * Register user
   * @param username
   * @param password
   * @returns
   */
  public register(username: string, password: string): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/user/register/request", "auth-service"),
      { username, password },
      buildOptions()
    );
  }

  /**
   * Fetch user info
   */
  public fetchUserInfo(): void {
    this.http
      .get<Resp<UserInfo>>(
        buildApiPath("/user/info", "auth-service"),
        buildOptions()
      )
      .subscribe({
        next: (resp) => {
          if (resp.data != null) {
            this.notifyRole(resp.data.role);
            this.notifyLoginStatus(true);
            this.notifyUserInfo(resp.data);
          } else {
            this.notifi.toast("Please login first");
            setToken(null);
            this.nav.navigateTo(NavType.LOGIN_PAGE);
            this.notifyLoginStatus(false);
          }
        },
      });
  }

  private notifyUserInfo(userInfo: UserInfo): void {
    this.userInfoSubject.next(userInfo);
  }

  /** Notify the role of the user via observable */
  private notifyRole(role: string): void {
    this.roleSubject.next(role);
  }

  /** Notify the login status of the user via observable */
  private notifyLoginStatus(isLoggedIn: boolean): void {
    this.isLoggedInSubject.next(isLoggedIn);
  }

  /**
   * Fetch user details
   */
  public fetchUserDetails(): Observable<
    Resp<{
      id;
      username;
      role;
      registerDate;
    }>
  > {
    return this.http.get<Resp<any>>(
      buildApiPath("/user/detail", "auth-service"),
      buildOptions()
    );
  }

  /**
   * Change password
   */
  public changePassword(param: ChangePasswordParam): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/user/password/update"),
      param,
      buildOptions()
    );
  }
}
