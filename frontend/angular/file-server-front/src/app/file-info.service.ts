import { HttpClient, HttpEvent, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import {
  FetchFileExtList,
  FileExt,
  SearchFileExtParam,
} from "src/models/file-ext";
import {
  FetchFileInfoList,
  FetchFileInfoListParam,
  ListGrantedAccessResp,
  UpdateFileUserGroupParam,
  UploadFileParam,
} from "src/models/file-info";
import {
  ListAllFsGroupReqVo,
  ListAllFsGroupRespVo,
  UpdateFsGroupModeReqVo,
} from "src/models/fs-group";
import { Paging } from "src/models/paging";
import { Resp } from "src/models/resp";
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
export class FileInfoService {
  constructor(private http: HttpClient) {}

  /**
   * Delete file
   */
  public deleteFile(id: number): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/file/delete"),
      { id: id },
      headers
    );
  }

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
    param: SearchFileExtParam
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
   * Add new file extension
   * @param name
   * @returns
   */
  public addFileExtension(name: string): Observable<Resp<void>> {
    return this.http.post<Resp<void>>(
      buildApiPath("/file/extension/add"),
      { name: name },
      headers
    );
  }

  /**
   * Post file with given name
   * @param uploadName
   * @param uploadFile
   */
  public postFile(uploadParam: UploadFileParam): Observable<HttpEvent<any>> {
    let formData = new FormData();
    for (let name of uploadParam.names) {
      formData.append("fileName", name);
    }
    for (let f of uploadParam.files) {
      formData.append("file", f);
    }
    formData.append("userGroup", uploadParam.userGruop.toString());
    return this.http.post<HttpEvent<any>>(
      buildApiPath("/file/upload"),
      formData,
      {
        observe: "events",
        reportProgress: true,
        withCredentials: true,
      }
    );
  }

  /**
   * Generate file temporary token
   */
  public generateFileTempToken(id: number): Observable<Resp<string>> {
    return this.http.post<Resp<string>>(
      buildApiPath("/file/token/generate"),
      { id: id },
      headers
    );
  }

  /**
   * Update file's info
   */
  public updateFile(param: UpdateFileUserGroupParam): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/file/info/update"),
      param,
      headers
    );
  }

  /**
   * Fetch fs_group
   */
  public fetchFsGroups(
    param: ListAllFsGroupReqVo
  ): Observable<Resp<ListAllFsGroupRespVo>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/fsgroup/list"),
      param,
      headers
    );
  }

  /**
   * Update fs_group's mode
   */
  public updateFsGroupMode(
    param: UpdateFsGroupModeReqVo
  ): Observable<Resp<void>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/fsgroup/mode/update"),
      param,
      headers
    );
  }

  /**
   * Grant file's access to a user
   */
  public grantFileAccess(param: {
    fileId: number;
    grantedTo: string;
  }): Observable<Resp<void>> {
    return this.http.post<Resp<void>>(
      buildApiPath("/file/grant-access"),
      param,
      headers
    );
  }

  /** List accesses granted to the file */
  public listGrantedAccess(param: {
    fileId: number;
    pagingVo: Paging;
  }): Observable<Resp<ListGrantedAccessResp>> {
    return this.http.post<Resp<ListGrantedAccessResp>>(
      buildApiPath("/file/list-granted-access"),
      param,
      headers
    );
  }

  /** Remove accesses granted to the file */
  public removeGrantedAccess(param: {
    fileId: number;
    userId: number;
  }): Observable<Resp<void>> {
    return this.http.post<Resp<void>>(
      buildApiPath("/file/remove-granted-access"),
      param,
      headers
    );
  }
}
