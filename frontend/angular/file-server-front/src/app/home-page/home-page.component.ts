import { Component, ElementRef, Input, OnInit, ViewChild } from "@angular/core";
import {
  FileInfo,
  FileOwnershipConst,
  FileUserGroupConst,
} from "src/models/file-info";
import { Paging, PagingConst, PagingController } from "src/models/paging";
import {
  emptySearchFileInfoParam,
  emptyUploadFileParam,
  SearchFileInfoParam,
  UploadFileParam,
} from "src/models/request-model";
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
  readonly OWNERSHIP_ALL_FILES = FileOwnershipConst.FILE_OWNERSHIP_ALL_FILES;
  readonly OWNERSHIP_MY_FILES = FileOwnershipConst.FILE_OWNERSHIP_MY_FILES;
  readonly PRIVATE_GROUP = FileUserGroupConst.USER_GROUP_PRIVATE;
  readonly PUBLIC_GROUP = FileUserGroupConst.USER_GROUP_PUBLIC;

  fileExtSet: Set<string> = new Set();
  fileInfoList: FileInfo[] = [];
  searchParam: SearchFileInfoParam = emptySearchFileInfoParam();
  uploadParam: UploadFileParam = emptyUploadFileParam();
  isGuest: boolean = true;
  pagingController: PagingController = new PagingController();

  @ViewChild("uploadFileInput", { static: true })
  uploadFileInput: ElementRef<HTMLInputElement>;

  @ViewChild("uploadFileNameInput", { static: true })
  uploadFileNameInput: ElementRef<HTMLInputElement>;

  @ViewChild("defSearchUserGroup", { static: true })
  defaultSearchUserGroup: ElementRef<HTMLOptionElement>;

  @ViewChild("defSearchOwner", { static: true })
  defaultSearchOwner: ElementRef<HTMLOptionElement>;

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
    this.httpClient.fetchSupportedFileExtensionNames().subscribe({
      next: (resp) => {
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
    this.httpClient
      .fetchFileInfoList({
        pagingVo: this.pagingController.paging,
        filename: this.searchParam.name,
        userGroup: this.searchParam.userGroup,
        ownership: this.searchParam.ownership,
      })
      .subscribe({
        next: (resp) => {
          this.fileInfoList = resp.data.fileInfoList;
          let total = resp.data.pagingVo.total;
          if (total != null) {
            this.pagingController.updatePages(total);
          }
        },
        error: (err) => {
          console.log(err);
        },
      });
  }

  /** Concatenate url for downloading the file  */
  concatFileHref(uuid: string): string {
    return "file/download?uuid=" + uuid;
  }

  /** Convert number of bytes to apporpriate unit */
  resolveSize(sizeInBytes: number): string {
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
  upload(): void {
    if (!this.uploadParam.file) {
      window.alert("Please select a file to upload");
      return;
    }
    if (!this.uploadParam.name) {
      window.alert("File name cannot be empty");
      return;
    }
    if (this.uploadParam.userGruop == null) {
      // default private group
      this.uploadParam.userGruop = FileUserGroupConst.USER_GROUP_PRIVATE;
    }
    let fileExt = this.parseFileExt(this.uploadParam.name);
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
    this.httpClient.postFile(this.uploadParam).subscribe({
      next: (r) => {},
      complete: () => {
        this.uploadParam.file = null;
        this.uploadParam.name = null;
        this.uploadFileInput.nativeElement.value = null;
        this.uploadFileNameInput.nativeElement.value = null;
        this.fetchFileInfoList();
      },
      error: () => {
        window.alert("Failed to upload file");
      },
    });
  }

  /** Handle events on file selected/changed */
  onFileSelected(files: File[]): void {
    if (files.length > 0) {
      let firstFile: File = files[0];
      this.uploadParam.file = firstFile;
      this.uploadParam.name = firstFile.name;
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
  setUploadUserGroup(userGroup: number): void {
    this.uploadParam.userGruop = userGroup;
  }

  /**
   * Set the specified page and fetch the file info list
   * @param page
   */
  gotoPage(page: number): void {
    this.pagingController.setPage(page);
    this.fetchFileInfoList();
  }

  /**
   * Set current page size and fetch the file info list
   * @param pageSize
   */
  setPageSize(pageSize: number): void {
    this.pagingController.setPageLimit(pageSize);
    this.fetchFileInfoList();
  }

  nextPage(): void {
    if (this.pagingController.canGoToNextPage()) {
      this.pagingController.nextPage();
      this.fetchFileInfoList();
    }
  }

  prevPage(): void {
    if (this.pagingController.canGoToPrevPage()) {
      this.pagingController.prevPage();
      this.fetchFileInfoList();
    }
  }

  /** Reset all parameters used for searching */
  resetSearchParam(): void {
    this.searchParam = emptySearchFileInfoParam();
    this.defaultSearchUserGroup.nativeElement.selected = true;
    this.defaultSearchOwner.nativeElement.selected = true;
  }

  /** Set userGroup to the searching param */
  setSearchUserGroup(userGroup: number): void {
    this.searchParam.userGroup = userGroup;
  }

  /**
   * Delete file
   */
  deleteFile(uuid: string): void {
    this.httpClient.deleteFile(uuid).subscribe({
      next: (resp) => {
        this.fetchFileInfoList();
      },
    });
  }

  /**
   * Set ownership of files to the searching param
   * @param ownership
   */
  setSearchOwnership(ownership: number): void {
    this.searchParam.ownership = ownership;
  }

  searchNameInputKeyPressed(event: any): void {
    if (event.key === "Enter") {
      console.log("Pressed 'Enter' key, init search file list procedure");
      this.fetchFileInfoList();
    }
  }
}
