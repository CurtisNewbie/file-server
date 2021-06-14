import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { FetchFileInfoList, FileInfo } from "../models/file-info";
import { Resp } from "../models/resp";
import { UserInfo } from "src/models/user-info";
import { Paging } from "src/models/paging";
import {
  FetchFileInfoListParam,
  UploadFileParam,
} from "src/models/request-model";

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
  public fetchFileInfoList(
    param: FetchFileInfoListParam
  ): Observable<Resp<FetchFileInfoList>> {
    return this.http.post<Resp<FetchFileInfoList>>(
      `/file/list`,
      param,
      headers
    );
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
  public fetchUserInfo(): Observable<Resp<UserInfo>> {
    return this.http.get<Resp<UserInfo>>("/user/info", {
      withCredentials: true,
    });
  }

  /**
   * Logout current user
   */
  public logout(): Observable<void> {
    return this.http.get<void>("/logout", { withCredentials: true });
  }

  /**
   * Add user
   * @param username
   * @param password
   */
  public addUser(
    username: string,
    password: string,
    userRole: string
  ): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      "/user/register",
      JSON.stringify({
        username: username,
        password: password,
        userRole: userRole,
      }),
      headers
    );
  }

  /**
   * Fetch list of user infos
   */
  public fetchUserList(): Observable<Resp<UserInfo[]>> {
    return this.http.get<Resp<UserInfo[]>>("/user/list", headers);
  }

  /**
   * Delete user by id
   * @param id
   */
  public deleteUserById(id: number): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      "/user/delete",
      {
        id: id,
      },
      headers
    );
  }

  /**
   * Post file with given name
   * @param uploadName
   * @param uploadFile
   */
  public postFile(uploadParam: UploadFileParam): Observable<any> {
    let formData = new FormData();
    formData.append("fileName", uploadParam.name);
    formData.append("file", uploadParam.file);
    formData.append("userGroup", uploadParam.userGruop.toString());
    return this.http.post<any>("/file/upload", formData, {
      withCredentials: true,
    });
  }

  /**
   * Delete file
   * @param uuid
   */
  public deleteFile(uuid: string): Observable<Resp<any>> {
    return this.http.post<Resp<any>>("/file/delete", { uuid: uuid }, headers);
  }
}
