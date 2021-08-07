import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";
import { HttpEventType } from "@angular/common/http";
import { NullTemplateVisitor } from "@angular/compiler";
import {
  Inject,
  Component,
  ElementRef,
  OnInit,
  ViewChild,
} from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { PageEvent } from "@angular/material/paginator";
import { Subscription } from "rxjs";

import {
  FileInfo,
  FileOwnershipEnum,
  FileOwnershipOption,
  FileUserGroupEnum,
  FileUserGroupOption,
  FILE_OWNERSHIP_OPTIONS,
  FILE_USER_GROUP_OPTIONS,
} from "src/models/file-info";
import { PagingController } from "src/models/paging";
import {
  emptySearchFileInfoParam,
  emptyUploadFileParam,
  SearchFileInfoParam,
  UploadFileParam,
} from "src/models/request-model";
import { ConfirmDialogComponent } from "../dialog/confirm/confirm-dialog.component";
import { HttpClientService } from "../http-client-service.service";
import { NotificationService } from "../notification.service";
import { UserService } from "../user.service";
import { buildApiPath } from "../util/api-util";

const KB_UNIT: number = 1024;
const MB_UNIT: number = 1024 * 1024;
const GB_UNIT: number = 1024 * 1024 * 1024;

@Component({
  selector: "app-home-page",
  templateUrl: "./home-page.component.html",
  styleUrls: ["./home-page.component.css"],
  animations: [
    trigger("detailExpand", [
      state("collapsed", style({ height: "0px", minHeight: "0" })),
      state("expanded", style({ height: "*" })),
      transition(
        "expanded <=> collapsed",
        animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)")
      ),
    ]),
  ],
})
export class HomePageComponent implements OnInit {
  readonly OWNERSHIP_ALL_FILES = FileOwnershipEnum.FILE_OWNERSHIP_ALL_FILES;
  readonly OWNERSHIP_MY_FILES = FileOwnershipEnum.FILE_OWNERSHIP_MY_FILES;
  readonly PRIVATE_GROUP = FileUserGroupEnum.USER_GROUP_PRIVATE;
  readonly PUBLIC_GROUP = FileUserGroupEnum.USER_GROUP_PUBLIC;
  readonly USER_GROUP_OPTIONS: FileUserGroupOption[] = FILE_USER_GROUP_OPTIONS;
  readonly FILE_OWNERSHIP_OPTIONS: FileOwnershipOption[] =
    FILE_OWNERSHIP_OPTIONS;
  readonly COLUMN_TO_BE_DISPLAYED: string[] = [
    "name",
    "size",
    "userGroup",
    "download",
    "delete",
  ];

  expandedElement: FileInfo;
  fileExtSet: Set<string> = new Set();
  fileInfoList: FileInfo[] = [];
  searchParam: SearchFileInfoParam = emptySearchFileInfoParam();
  uploadParam: UploadFileParam = emptyUploadFileParam();
  isGuest: boolean = true;
  pagingController: PagingController = new PagingController();
  progress: string = null;
  displayedUploadName: string = null;
  fileUploadSubscription: Subscription = null;

  @ViewChild("uploadFileInput", { static: true })
  uploadFileInput: ElementRef<HTMLInputElement>;

  constructor(
    private httpClient: HttpClientService,
    private userService: UserService,
    private notifi: NotificationService,
    private dialog: MatDialog
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
    // always select private group for uploading
    this.uploadParam.userGruop = FileUserGroupEnum.USER_GROUP_PRIVATE;
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
    return buildApiPath("/file/download?uuid=" + uuid);
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
    if (this.isFileUploading()) {
      this.notifi.toast("Uplading file, please wait a moment");
      return;
    }
    if (this.uploadParam.files == null || this.uploadParam.files.length < 1) {
      this.notifi.toast("Please select a file to upload");
      return;
    }
    if (!this.displayedUploadName) {
      this.notifi.toast("File name cannot be empty");
      return;
    }
    this.uploadParam.names.unshift(this.displayedUploadName);
    if (this.uploadParam.names == null || this.uploadParam.names.length < 1) {
      this.notifi.toast("File name cannot be empty");
      return;
    }
    if (this.uploadParam.userGruop == null) {
      // default private group
      this.uploadParam.userGruop = FileUserGroupEnum.USER_GROUP_PRIVATE;
    }

    // validate file extension by names
    for (let name of this.uploadParam.names) {
      let fileExt = this.parseFileExt(name);
      if (!fileExt) {
        this.notifi.toast(`File extension must not be empty`);
        return;
      }
      if (!this.isFileExtSupported(fileExt)) {
        this.notifi.toast(`File extension '${fileExt}' isn't supported`);
        return;
      }
    }

    this.fileUploadSubscription = this.httpClient
      .postFile(this.uploadParam)
      .subscribe({
        next: (event) => {
          if (event.type === HttpEventType.UploadProgress) {
            this.progress =
              Math.round((100 * event.loaded) / event.total).toFixed(2) + "%";
          }
        },
        complete: () => {
          this.resetFileUploadParam();
          this.fetchFileInfoList();
        },
        error: () => {
          this.notifi.toast("Failed to upload file");
        },
      });
  }

  isFileExtSupported(fileExt: string): boolean {
    if (!fileExt) return false;

    fileExt = fileExt.toLowerCase();
    if (!this.fileExtSet.has(fileExt)) {
      return false;
    }
    return true;
  }

  /** Handle events on file selected/changed */
  onFileSelected(files: File[]): void {
    if (files.length < 1) {
      this.resetFileUploadParam();
      return;
    }

    // always use the name of the first file
    let firstFile: File = files[0];
    this.uploadParam.files = files;

    if (files.length > 1) {
      this.displayedUploadName = firstFile.name + ".zip";
      // the entries
      let fileNames: string[] = [];
      for (let n of files) {
        fileNames.push(n.name);
      }
      this.uploadParam.names = fileNames;
    } else {
      this.displayedUploadName = firstFile.name;
      this.uploadParam.names = [];
    }
    console.log(this.uploadParam);
  }

  /**
   * Get file extension
   * @param {*} path
   * @returns fileExtension, or "" if there isn't one
   */
  parseFileExt(path: string): string {
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
    if (userGroup === FileUserGroupEnum.USER_GROUP_PUBLIC) {
      return "public";
    } else if (userGroup === FileUserGroupEnum.USER_GROUP_PRIVATE) {
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

  /** Reset all parameters used for searching */
  resetSearchParam(): void {
    this.searchParam = emptySearchFileInfoParam();
  }

  /** Set userGroup to the searching param */
  setSearchUserGroup(userGroup: number): void {
    this.searchParam.userGroup = userGroup;
  }

  /**
   * Delete file
   */
  deleteFile(uuid: string, name: string): void {
    const dialogRef: MatDialogRef<ConfirmDialogComponent, boolean> =
      this.dialog.open(ConfirmDialogComponent, {
        width: "350px",
        data: { msg: `You sure you want to delete '${name}'` },
      });

    dialogRef.afterClosed().subscribe((confirm) => {
      console.log(confirm);
      if (confirm) {
        this.httpClient.deleteFile(uuid).subscribe({
          next: (resp) => {
            this.fetchFileInfoList();
          },
        });
      }
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

  resetFileUploadParam(): void {
    this.uploadParam.files = null;
    this.uploadParam.names = null;
    this.uploadFileInput.nativeElement.value = null;
    this.displayedUploadName = null;
    this.progress = null;
  }

  handle(e: PageEvent): void {
    this.pagingController.handle(e);
    this.fetchFileInfoList();
  }

  fileExtToolTip(): string {
    return this.subSetToStr(this.fileExtSet, this.fileExtSet.size);
  }

  subSetToStr(set: Set<string>, maxCount: number): string {
    let s: string = "";
    let i: number = 0;
    for (let e of set) {
      if (i++ >= maxCount) break;

      s += e + ", ";
    }
    return s.substr(0, s.length - ", ".length);
  }

  /** Cancel the file uploading */
  cancelFileUpload(): void {
    if (!this.isFileUploading()) {
      return;
    }
    this.fileUploadSubscription.unsubscribe();
    this.resetFileUploadParam();
    this.notifi.toast("File uploading cancelled");
  }

  /** Is a file being loaded currently */
  isFileUploading(): boolean {
    return (
      this.fileUploadSubscription != null && !this.fileUploadSubscription.closed
    );
  }

  /** Update file's userGroup */
  updateUserGroup(u: FileInfo): void {
    if (!u) return;
    this.httpClient
      .updateFileUserGroup({
        uuid: u.uuid,
        userGroup: u.userGroup,
      })
      .subscribe({
        complete: () => {
          this.fetchFileInfoList();
          this.expandedElement = null;
        },
      });
  }

  copy(f: FileInfo): FileInfo {
    if (!f) return null;
    let copy = { ...f };
    return copy;
  }

  uuidEquals(fl: FileInfo, fr: FileInfo): boolean {
    if (fl == null || fr == null) return false;

    return fl.uuid === fr.uuid;
  }
}
