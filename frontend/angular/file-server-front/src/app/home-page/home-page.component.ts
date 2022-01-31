import { HttpEventType } from "@angular/common/http";
import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { PageEvent } from "@angular/material/paginator";
import { Subscription } from "rxjs";

import {
  emptySearchFileInfoParam,
  emptyUploadFileParam,
  FileInfo,
  FileOwnershipEnum,
  FileOwnershipOption,
  FileUserGroupEnum,
  FileUserGroupOption,
  FILE_OWNERSHIP_OPTIONS,
  FILE_USER_GROUP_OPTIONS,
  SearchFileInfoParam,
  UploadFileParam,
} from "src/models/file-info";
import { PagingController } from "src/models/paging";
import { ConfirmDialogComponent } from "../dialog/confirm/confirm-dialog.component";
import { NotificationService } from "../notification.service";
import { UserService } from "../user.service";
import { animateElementExpanding } from "../../animate/animate-util";
import { buildApiPath } from "../util/api-util";
import { FileInfoService } from "../file-info.service";
import { GrantAccessDialogComponent } from "../grant-access-dialog/grant-access-dialog.component";
import { ManageTagDialogComponent } from "../manage-tag-dialog/manage-tag-dialog.component";

const KB_UNIT: number = 1024;
const MB_UNIT: number = 1024 * 1024;
const GB_UNIT: number = 1024 * 1024 * 1024;

@Component({
  selector: "app-home-page",
  templateUrl: "./home-page.component.html",
  styleUrls: ["./home-page.component.css"],
  animations: [animateElementExpanding()],
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
    "uploader",
    "uploadTime",
    "size",
    "userGroup",
    "download",
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
  tags: string[];

  @ViewChild("uploadFileInput", { static: true })
  uploadFileInput: ElementRef<HTMLInputElement>;

  constructor(
    private userService: UserService,
    private notifi: NotificationService,
    private dialog: MatDialog,
    private fileService: FileInfoService
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
    this.fetchTags();
    // always select private group for uploading
    this.uploadParam.userGruop = FileUserGroupEnum.USER_GROUP_PRIVATE;
  }

  /** fetch supported file extension */
  private fetchSupportedExtensions(): void {
    this.fileService.fetchSupportedFileExtensionNames().subscribe({
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

  fetchTags(): void {
    this.fileService.fetchTags().subscribe({
      next: (resp) => {
        this.tags = resp.data;
      },
    });
  }

  /** fetch file info list */
  fetchFileInfoList(): void {
    this.fileService
      .fetchFileInfoList({
        pagingVo: this.pagingController.paging,
        filename: this.searchParam.name,
        userGroup: this.searchParam.userGroup,
        ownership: this.searchParam.ownership,
        tagName: this.searchParam.tagName,
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
  concatFileHref(id: number): string {
    return buildApiPath("/file/download?id=" + id);
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
    if (this.uploadParam.userGruop == null) {
      // default private group
      this.uploadParam.userGruop = FileUserGroupEnum.USER_GROUP_PRIVATE;
    }
    console.log("Upload file", this.uploadParam);

    // validate file extension by name
    let fileExt = this.parseFileExt(this.displayedUploadName);
    if (!fileExt) {
      this.notifi.toast(`File extension must not be empty`);
      return;
    }
    if (!this.isFileExtSupported(fileExt)) {
      this.notifi.toast(`File extension '${fileExt}' isn't supported`);
      return;
    }

    // the first one is always the one displayed
    this.uploadParam.names.unshift(this.displayedUploadName);
    this.fileUploadSubscription = this.fileService
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
  deleteFile(id: number, name: string): void {
    const dialogRef: MatDialogRef<ConfirmDialogComponent, boolean> =
      this.dialog.open(ConfirmDialogComponent, {
        width: "500px",
        data: { msg: [`You sure you want to delete '${name}'`] },
      });

    dialogRef.afterClosed().subscribe((confirm) => {
      console.log(confirm);
      if (confirm) {
        this.fileService.deleteFile(id).subscribe({
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

  /**
   * Set tag to the searching param
   */
  setTag(tag: string): void {
    this.searchParam.tagName = tag;
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
  update(u: FileInfo): void {
    if (!u) return;
    this.fileService
      .updateFile({
        id: u.id,
        userGroup: u.userGroup,
        name: u.name,
      })
      .subscribe({
        complete: () => {
          this.fetchFileInfoList();
          this.expandedElement = null;
        },
      });
  }

  /**
   * Generate temporary token for downloading
   */
  generateTempToken(u: FileInfo): void {
    if (!u) return;
    this.fileService.generateFileTempToken(u.id).subscribe({
      next: (resp) => {
        const dialogRef: MatDialogRef<ConfirmDialogComponent, boolean> =
          this.dialog.open(ConfirmDialogComponent, {
            width: "700px",
            data: { msg: ["Link to download this file:", resp.data] },
          });

        dialogRef.afterClosed().subscribe((confirm) => {
          // do nothing
        });
      },
    });
  }

  popToGrantAccess(u: FileInfo): void {
    if (!u) return;

    const dialogRef: MatDialogRef<GrantAccessDialogComponent, boolean> =
      this.dialog.open(GrantAccessDialogComponent, {
        width: "700px",
        data: { fileId: u.id, fileName: u.name },
      });

    dialogRef.afterClosed().subscribe((confirm) => {
      // do nothing
    });
  }

  popToManageTag(u: FileInfo): void {
    if (!u) return;

    const dialogRef: MatDialogRef<ManageTagDialogComponent, boolean> =
      this.dialog.open(ManageTagDialogComponent, {
        width: "700px",
        data: { fileId: u.id, filename: u.name },
      });

    dialogRef.afterClosed().subscribe((confirm) => {
      // do nothing
    });
  }

  copy(f: FileInfo): FileInfo {
    if (!f) return null;
    let copy = { ...f };
    return copy;
  }

  /**
   * Two non-null FileInfo are considered equals, when the id are equals, if any one of them is null, they are not equals
   */
  idEquals(fl: FileInfo, fr: FileInfo): boolean {
    if (fl == null || fr == null) return false;

    return fl.id === fr.id;
  }

  /**
   * Look at the row, determine whether we should expand this row (return this row)
   *
   * @param row null value if we shouldn't expand this element, else a copy of this row
   * @returns expandedElement
   */
  determineExpandedElement(row: FileInfo): FileInfo {
    if (!row.isOwner) return null;

    return this.idEquals(this.expandedElement, row) ? null : this.copy(row);
  }
}
