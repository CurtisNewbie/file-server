import { Component, OnInit } from "@angular/core";
import { FileInfo, FileUserGroupConst } from "src/models/file-info";
import { Paging } from "src/models/paging";
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
  readonly pageLimitOptions: number[] = [5, 10, 20, 50];
  nameInput: string = "";
  fileExtSet: Set<string> = new Set();
  fileInfoList: FileInfo[] = [];
  uploadFileName: string = null;
  uploadFile: File = null;
  uploadUserGroup: number = null;
  isGuest: boolean = true;
  paging: Paging = { page: 1, limit: this.pageLimitOptions[0], total: 0 };
  pages: number[] = [1, 2, 3, 4, 5];

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
          this.fileExtSet.add(e.toLowerCase());
        }
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  /** fetch file info list */
  fetchFileInfoList(): void {
    this.httpClient.fetchFileInfoList(this.paging).subscribe({
      next: (resp) => {
        if (resp.hasError) {
          window.alert(resp.msg);
          return;
        }
        this.fileInfoList = resp.data.fileInfoList;
        let total = resp.data.pagingVo.total;
        if (total != null) {
          this.updatePages(total);
        }
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  /** Update the list of pages that it can select */
  private updatePages(total: number): void {
    this.pages = [];
    let maxPage = Math.floor(total / this.paging.limit);
    for (let i = 1; i <= maxPage; i++) {
      this.pages.push(i);
    }
    if (this.pages.length === 0) {
      this.pages.push(1);
    }
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
    fileExt = fileExt.toLowerCase();
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

  /** Set usergruop for the uploading file */
  setUserGroup(userGroup: number): void {
    this.uploadUserGroup = userGroup;
  }

  /**
   * Set the specified page and fetch the file info list
   * @param page
   */
  gotoPage(page: number): void {
    this.paging.page = page;
    this.fetchFileInfoList();
  }

  /**
   * Set current page size and fetch the file info list
   * @param pageSize
   */
  setPageSize(pageSize: number): void {
    this.paging.limit = pageSize;
    this.fetchFileInfoList();
  }

  nextPage(): void {
    if (this.paging.page < this.pages[this.pages.length - 1]) {
      this.gotoPage(this.paging.page + 1);
    }
  }

  prevPage(): void {
    if (this.paging.page > 1) {
      this.gotoPage(this.paging.page - 1);
    }
  }
}
