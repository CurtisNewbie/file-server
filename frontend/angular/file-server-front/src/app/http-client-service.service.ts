import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { FileInfo } from "../models/file-info";
import { Resp } from "../models/resp";
import { UserInfo } from "src/models/user-info";

const headers = {
  headers: new HttpHeaders({
    "Content-Type": "application/json",
  }),
  withCredentials: true,
};

@Injectable({
  providedIn: "root",
})
export class HttpClientService {
  constructor(private http: HttpClient) {}

  /**
   * Fetch list of file info
   */
  public fetchFileInfoList(): Observable<Resp<FileInfo[]>> {
    return this.http.get<Resp<FileInfo[]>>(`/file/list`, headers);
  }

  /**
   * Fetch the supported file extensions
   */
  public fetchSupportedFileExtensions(): Observable<Resp<string[]>> {
    return this.http.get<Resp<string[]>>(`/file/extension`, headers);
  }

  /**
   * Login
   * @param username
   * @param password
   */
  public login(username: string, password: string): Observable<Resp<any>> {
    let formData = new FormData();
    formData.append("username", username);
    formData.append("password", password);
    return this.http.post<Resp<any>>(`/login`, formData, {
      withCredentials: true,
    });
  }

  /**
   * Fetch current user info
   */
  public fetchUserInfo(): Observable<UserInfo> {
    return this.http.get<UserInfo>("/user/info", { withCredentials: true });
  }

  /**
   * Logout current user
   */
  public logout(): Observable<void> {
    return this.http.get<void>("/logout", { withCredentials: true });
  }
}
