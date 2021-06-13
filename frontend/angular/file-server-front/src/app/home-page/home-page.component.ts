import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { FileInfo, FileUserGroupConst } from "src/models/file-info";
import { HttpClientService } from "../http-client-service.service";
import { UserService } from "../user.service";

const KB_UNIT: number = 1024;
const MB_UNIT: number = 1024 * 1024;
const GB_UNIT: number = 1024 * 1024 * 1024;

@Component({
  selector: "app-home-page",
  templateUrl: "./home-page.component.html",
  styleUrls: ["./home-page.component.css"],
})
export class HomePageComponent implements OnInit {
  nameInput: string = "";
  fileExtSet: Set<string> = new Set();
  fileInfoList: FileInfo[] = [];
  uploadFileName: string = null;
  uploadFile: File = null;
  uploadUserGroup: number = null;
  isGuest: boolean = true;

  constructor(
    private httpClient: HttpClientService,
    private userService: UserService
  ) {}

  ngOnInit() {
    if (this.userService.hasUserInfo()) {
      this.isGuest = this.userService.getUserInfo().role === "guest";
    }
    this.userService.roleObservable.subscribe({
      next: (role) => {
        this.isGuest = role === "guest";
      },
    });
    this.fetchSupportedExtensions();
    this.fetchFileInfoList();
  }

  /** fetch supported file extension */
  private fetchSupportedExtensions(): void {
    this.httpClient.fetchSupportedFileExtensions().subscribe({
      next: (resp) => {
        if (resp.hasError) {
          window.alert(resp.msg);
          return;
        }
        this.fileExtSet.clear();
        for (let e of resp.data) {
          this.fileExtSet.add(e);
        }
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  /** fetch file info list */
  fetchFileInfoList(): void {
    this.httpClient.fetchFileInfoList().subscribe({
      next: (resp) => {
        if (resp.hasError) {
          window.alert(resp.msg);
          return;
        }
        this.fileInfoList = resp.data;
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  /** Concatenate url for downloading the file  */
  public concatFileHref(uuid: string): string {
    return "file/download?uuid=" + uuid;
  }

  /** Convert number of bytes to apporpriate unit */
  public resolveSize(sizeInBytes: number): string {
    if (sizeInBytes > GB_UNIT) {
      return this.divideUnit(sizeInBytes, GB_UNIT) + " gb";
    }
    if (sizeInBytes > MB_UNIT) {
      return this.divideUnit(sizeInBytes, MB_UNIT) + " mb";
    }
    return this.divideUnit(sizeInBytes, KB_UNIT) + " kb";
  }

  private divideUnit(size: number, unit: number): string {
    return (size / unit).toFixed(1);
  }

  /** Upload file */
  public upload(): void {
    if (!this.uploadFile) {
      window.alert("Please select a file to upload");
      return;
    }
    if (!this.uploadFileName) {
      window.alert("File name cannot be empty");
      return;
    }
    if (this.uploadUserGroup == null) {
      // default private group
      this.uploadUserGroup = FileUserGroupConst.USER_GROUP_PRIVATE;
    }
    let fileExt = this.parseFileExt(this.uploadFileName);
    console.log("Parsed file extension:", fileExt);
    if (!fileExt) {
      window.alert("Please specify file extension");
      return;
    }
    if (!this.fileExtSet.has(fileExt)) {
      window.alert(`File extension '${fileExt}' isn't supported`);
      return;
    }
    this.httpClient
      .postFile(this.uploadFileName, this.uploadFile, this.uploadUserGroup)
      .subscribe({
        next: (resp) => {
          if (resp.hasError) {
            window.alert(resp.msg);
          }
        },
        complete: () => {
          this.uploadFile = null;
          this.uploadFileName = null;
          this.fetchFileInfoList();
        },
        error: () => {
          window.alert("Failed to upload file");
        },
      });
  }

  /** Handle events on file selected/changed */
  public onFileSelected(event): void {
    console.log(event);
    if (event.target.files.length > 0) {
      this.uploadFile = event.target.files[0];
      this.uploadFileName = this.uploadFile.name;
    }
  }

  /**
   * Get file extension
   * @param {*} path
   * @returns fileExtension, or "" if there isn't one
   */
  private parseFileExt(path: string): string {
    if (!path || path.endsWith(".")) {
      return "";
    }
    let i = path.lastIndexOf(".");
    if (i <= 0) {
      return "";
    }
    return path.substring(i + 1);
  }

  /**
   * Convert userGroup in number to the corresponding name
   */
  resolveUserGroupName(userGroup: number): string {
    if (userGroup === FileUserGroupConst.USER_GROUP_PUBLIC) {
      return "public";
    } else if (userGroup === FileUserGroupConst.USER_GROUP_PRIVATE) {
      return "private";
    }
    return "";
  }

  setUserGroup(userGroup: number): void {
    this.uploadUserGroup = userGroup;
  }
}
