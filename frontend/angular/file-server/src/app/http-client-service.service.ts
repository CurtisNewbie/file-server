import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { FileInfo } from "../models/file-info";
import { Resp } from "../models/resp";

const BASE_URL: string = "https://localhost:8443";
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
  public login(username: string, password: string): Observable<any> {
    let formData = new FormData();
    formData.append("username", username);
    formData.append("password", password);
    return this.http.post<any>(`/login`, formData, { withCredentials: true });
  }
}
