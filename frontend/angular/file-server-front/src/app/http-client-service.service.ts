import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { FetchFileInfoList, FileInfo } from "../models/file-info";
import { Resp } from "../models/resp";
import { UserInfo } from "src/models/user-info";
import { Paging } from "src/models/paging";
import {
  AddUserParam,
  ChangePasswordParam,
  FetchAccessLogListParam,
  FetchFileInfoListParam,
  SearchFileExtParam as FetchFileExtParam,
  UploadFileParam,
} from "src/models/request-model";
import { AccessLog, FetchAccessLogList } from "src/models/access-log";
import { FetchFileExtList, FileExt } from "src/models/file-ext";
import { buildApiPath } from "./util/api-util";

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
      buildApiPath("/file/list"),
      param,
      headers
    );
  }

  /**
   * Fetch list of file info
   */
  public fetchAccessLogList(
    param: FetchAccessLogListParam
  ): Observable<Resp<FetchAccessLogList>> {
    return this.http.post<Resp<FetchAccessLogList>>(
      buildApiPath("/access/history"),
      param,
      headers
    );
  }

  /**
   * Fetch the supported file extensions' name
   */
  public fetchSupportedFileExtensionNames(): Observable<Resp<string[]>> {
    return this.http.get<Resp<string[]>>(
      buildApiPath("/file/extension/name"),
      headers
    );
  }

  /**
   * Fetch the supported file extensions' details
   */
  public fetchSupportedFileExtensionDetails(
    param: FetchFileExtParam
  ): Observable<Resp<FetchFileExtList>> {
    return this.http.post<Resp<FetchFileExtList>>(
      buildApiPath("/file/extension/list"),
      param,
      headers
    );
  }

  /**
   * Update file extension
   */
  public updateFileExtension(param: FileExt): Observable<Resp<FileExt[]>> {
    return this.http.post<Resp<FileExt[]>>(
      buildApiPath("/file/extension/update"),
      param,
      headers
    );
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
    return this.http.post<Resp<any>>(buildApiPath("/login"), formData, {
      withCredentials: true,
    });
  }

  /**
   * Fetch current user info
   */
  public fetchUserInfo(): Observable<Resp<UserInfo>> {
    return this.http.get<Resp<UserInfo>>(buildApiPath("/user/info"), {
      withCredentials: true,
    });
  }

  /**
   * Logout current user
   */
  public logout(): Observable<void> {
    return this.http.get<void>(buildApiPath("/logout"), {
      withCredentials: true,
    });
  }

  /**
   * Add user
   * @param username
   * @param password
   */
  public addUser(param: AddUserParam): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/user/register"),
      param,
      headers
    );
  }

  /**
   * Fetch list of user infos
   */
  public fetchUserList(): Observable<Resp<UserInfo[]>> {
    return this.http.get<Resp<UserInfo[]>>(buildApiPath("/user/list"), headers);
  }

  /**
   * Delete user by id
   * @param id
   */
  public disableUserByid(id: number): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/user/disable"),
      {
        id: id,
      },
      headers
    );
  }

  /**
   * Enable user by id
   * @param id
   */
  public enableUserById(id: number): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/user/enable"),
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
    return this.http.post<any>(buildApiPath("/file/upload"), formData, {
      withCredentials: true,
    });
  }

  /**
   * Delete file
   * @param uuid
   */
  public deleteFile(uuid: string): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/file/delete"),
      { uuid: uuid },
      headers
    );
  }

  /**
   * Change password
   */
  public changePassword(param: ChangePasswordParam): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/user/password/update"),
      param,
      headers
    );
  }
}
