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
  ListTagsForFileResp,
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
import { buildApiPath, buildOptions, getToken } from "./util/api-util";

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
      buildOptions()
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
      buildOptions()
    );
  }

  /**
   * Fetch list of file tags
   */
  public fetchTags(): Observable<Resp<string[]>> {
    return this.http.get<Resp<string[]>>(
      buildApiPath("/file/tag/list/all"),
      buildOptions()
    );
  }

  /**
   * Fetch tags for current file
   * @param fileId
   * @returns
   */
  public fetchTagsForFile(
    fileId: number,
    pagingVo: Paging
  ): Observable<Resp<ListTagsForFileResp>> {
    return this.http.post<Resp<ListTagsForFileResp>>(
      buildApiPath("/file/tag/list-for-file"),
      { fileId: fileId, pagingVo: pagingVo },
      buildOptions()
    );
  }

  /**
   * Tag current file
   * @param param
   */
  public tagFile(param: {
    fileId: number;
    tagName: string;
  }): Observable<Resp<void>> {
    return this.http.post<Resp<void>>(
      buildApiPath("/file/tag/"),
      param,
      buildOptions()
    );
  }

  /**
   * Untag current file
   * @param param
   */
  public untagFile(param: {
    fileId: number;
    tagName: string;
  }): Observable<Resp<void>> {
    return this.http.post<Resp<void>>(
      buildApiPath("/file/untag/"),
      param,
      buildOptions()
    );
  }

  /**
   * Fetch the supported file extensions' name
   */
  public fetchSupportedFileExtensionNames(): Observable<Resp<string[]>> {
    return this.http.get<Resp<string[]>>(
      buildApiPath("/file/extension/name"),
      buildOptions()
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
      buildOptions()
    );
  }

  /**
   * Get file's download url
   */
  public getDownloadUrl(fileId: number): Observable<Resp<string>> {
    return this.http.get<Resp<string>>(
      buildApiPath("/file/url?id=" + fileId),
      buildOptions()
    );
  }

  /**
   * Update file extension
   */
  public updateFileExtension(param: FileExt): Observable<Resp<FileExt[]>> {
    return this.http.post<Resp<FileExt[]>>(
      buildApiPath("/file/extension/update"),
      param,
      buildOptions()
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
      buildOptions()
    );
  }

  /**
   * Post file with given name
   * @param uploadName
   * @param uploadFile
   */
  public postFile(uploadParam: UploadFileParam): Observable<HttpEvent<any>> {
    let formData = new FormData();
    formData.append("fileName", uploadParam.fileName);
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
        headers: new HttpHeaders({
          Authorization: getToken(),
        }),
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
      buildOptions()
    );
  }

  /**
   * Update file's info
   */
  public updateFile(param: UpdateFileUserGroupParam): Observable<Resp<any>> {
    return this.http.post<Resp<any>>(
      buildApiPath("/file/info/update"),
      param,
      buildOptions()
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
      buildOptions()
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
      buildOptions()
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
      buildOptions()
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
      buildOptions()
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
      buildOptions()
    );
  }
}
