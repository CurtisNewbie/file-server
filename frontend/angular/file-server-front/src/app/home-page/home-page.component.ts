import { HttpEventType } from "@angular/common/http";
import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
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
import { NavigationService, NavType } from "../navigation.service";
import { isMobile } from "../util/env-util";

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

  readonly DESKTOP_COLUMNS = [
    "name",
    "uploader",
    "uploadTime",
    "size",
    "userGroup",
    "download",
  ];
  readonly MOBILE_COLUMNS = ["name", "size", "download"];
  readonly IMAGE_SUFFIX = new Set(["jpeg", "jpg", "gif", "png", "svg", "bmp"]);

  expandedElement: FileInfo;
  fileExtSet: Set<string> = new Set();
  fileInfoList: FileInfo[] = [];
  searchParam: SearchFileInfoParam = emptySearchFileInfoParam();
  isGuest: boolean = true;
  pagingController: PagingController = new PagingController();
  progress: string = null;
  tags: string[];
  isMobile: boolean = isMobile();

  /*
  ---------

  For uploading
  
  ---------
  */

  uploadParam: UploadFileParam = emptyUploadFileParam();
  displayedUploadName: string = null;
  isCompressed: boolean = false;
  isUploading: boolean = false;

  /** Always points to current file, so the next will be uploadIndex+1 */
  uploadIndex = -1;
  uploadSub: Subscription = null;

  @ViewChild("uploadFileInput", { static: true })
  uploadFileInput: ElementRef<HTMLInputElement>;

  @ViewChild("paginator", { static: true })
  paginator: MatPaginator;

  constructor(
    private userService: UserService,
    private notifi: NotificationService,
    private dialog: MatDialog,
    private fileService: FileInfoService,
    private nav: NavigationService
  ) {}

  ngOnInit() {
    this.userService.fetchUserInfo();
    this.userService.roleObservable.subscribe(
      (role) => (this.isGuest = role === "guest")
    );
    this._fetchSupportedExtensions();
    this.fetchFileInfoList();
    this._fetchTags();
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
        error: (err) => console.log(err),
      });
  }

  /** Convert number of bytes to appropriate unit */
  resolveSize(sizeInBytes: number): string {
    if (sizeInBytes > GB_UNIT) {
      return this._divideUnit(sizeInBytes, GB_UNIT) + " gb";
    }
    if (sizeInBytes > MB_UNIT) {
      return this._divideUnit(sizeInBytes, MB_UNIT) + " mb";
    }
    return this._divideUnit(sizeInBytes, KB_UNIT) + " kb";
  }

  /** Upload file */
  upload(): void {
    if (this.isUploading) {
      this.notifi.toast("Uploading, please wait a moment or cancel it first");
      return;
    }

    if (this.uploadParam.files.length < 1) {
      this.notifi.toast("Please select a file to upload");
      return;
    }

    let isSingleUpload = this._isSingleUpload();
    let isZipCompressed = this._isZipCompressed();

    // single file upload or multiple upload as a zip, name is required
    if (!this.displayedUploadName && (isSingleUpload || isZipCompressed)) {
      this.notifi.toast("File name cannot be empty");
      return;
    }

    if (this.uploadParam.userGroup == null)
      this.uploadParam.userGroup = FileUserGroupEnum.USER_GROUP_PRIVATE;

    /*
      if it's a single file upload, or it's zip compressed, we only validate the 
      displayedUploadName, otherwise we validate every single file 
    */
    if (isSingleUpload || isZipCompressed) {
      if (!this._validateFileExt(this.displayedUploadName)) return;

      this.isUploading = true;
      this.uploadParam.fileName = this.displayedUploadName;
      this._doUpload(this.uploadParam);
    } else {
      for (let f of this.uploadParam.files) {
        if (!this._validateFileExt(f.name)) return;
      }

      this.isUploading = true;
      this._doUpload(this._prepNextUpload());
    }
  }

  /** Handle events on file selected/changed */
  onFileSelected(files: File[]): void {
    if (this.isUploading) return; // files can't be changed while uploading

    if (files.length < 1) {
      this._resetFileUploadParam();
      console.log("files clear");
      return;
    }

    this.uploadParam.files = files;
    this._setDisplayedFileName();
  }

  onIsCompressedChanged() {
    if (!this.uploadParam || !this.uploadParam.files) return;
    this._setDisplayedFileName();
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

  /** Reset all parameters used for searching, and the fetch the list */
  resetSearchParam(): void {
    this.searchParam = emptySearchFileInfoParam();
    this.paginator.firstPage();
    this.fetchFileInfoList();
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
        data: {
          msg: [`You sure you want to delete '${name}'`],
          isNoBtnDisplayed: true,
        },
      });

    dialogRef.afterClosed().subscribe((confirm) => {
      console.log(confirm);
      if (confirm) {
        this.fileService
          .deleteFile(id)
          .subscribe((resp) => this.fetchFileInfoList());
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
    if (!this.isUploading) return;

    if (this.uploadSub != null && !this.uploadSub.closed) {
      this.uploadSub.unsubscribe();
      return;
    }

    this.isUploading = false;
    this._resetFileUploadParam();
    this.notifi.toast("File uploading cancelled");
  }

  /** Update file's info */
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

  /** Guess whether the file is displayable by its name */
  isDisplayable(filename: string): boolean {
    if (!filename) return false;

    if (this._isPdf(filename) || this._isImage(filename)) return true;

    // videos are not supported for now, these can be downloaded and then played right? :D

    return false;
  }

  /** Display the file */
  display(u: FileInfo): void {
    this.fileService.generateFileTempToken(u.id).subscribe({
      next: (resp) => {
        const token = resp.data;
        const url = buildApiPath(
          "/file/token/download?token=" + token,
          "file-service"
        );
        let navType = this._isPdf(u.name)
          ? NavType.PDF_VIEWER
          : NavType.IMAGE_VIEWER;

        this.nav.navigateTo(navType, [
          { name: u.name, url: url, uuid: u.uuid },
        ]);
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
            data: {
              msg: [
                `Link to download this file: ${this._concatTempFileDownloadUrl(
                  resp.data
                )}`,
              ],
              isNoBtnDisplayed: false,
            },
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
        data: { fileId: u.id, filename: u.name, autoComplete: this.tags },
      });

    dialogRef.afterClosed().subscribe((confirm) => {
      this._fetchTags();
      this.expandedElement = null;
    });
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
    if (this.isMobile) return null; // mobile should never expand

    return this.idEquals(this.expandedElement, row) ? null : this._copy(row);
  }

  /**
   * Fetch download url and open it in a new tab
   * @param fileId
   */
  jumpToDownloadUrl(fileId: number, event: any): void {
    event.stopPropagation();
    this.fileService.generateFileTempToken(fileId).subscribe({
      next: (resp) => {
        const token = resp.data;
        const url = buildApiPath(
          "/file/token/download?token=" + token,
          "file-service"
        );
        window.open(url, "_parent");
      },
    });
  }

  isFileNameInputDisabled(): boolean {
    return this.isUploading || this._isBatchUpload();
  }

  // -------------------------- private helper methods ------------------------

  /** fetch supported file extension */
  private _fetchSupportedExtensions(): void {
    this.fileService.fetchSupportedFileExtensionNames().subscribe({
      next: (resp) => {
        this.fileExtSet.clear();
        for (let e of resp.data) {
          this.fileExtSet.add(e.toLowerCase());
        }
      },
      error: (err) => console.log(err),
    });
  }

  private _fetchTags(): void {
    this.fileService.fetchTags().subscribe({
      next: (resp) => (this.tags = resp.data),
    });
  }

  private _copy(f: FileInfo): FileInfo {
    if (!f) return null;
    let copy = { ...f };
    return copy;
  }

  private _concatTempFileDownloadUrl(tempToken: string): string {
    return (
      window.location.protocol +
      "//" +
      window.location.host +
      buildApiPath("/file/token/download?token=" + tempToken, "file-service")
    );
  }

  private _isPdf(filename: string): boolean {
    return filename.indexOf(".pdf") != -1;
  }

  private _isImage(filename: string): boolean {
    let i = filename.lastIndexOf(".");
    if (i < 0 || i == filename.length - 1) return false;

    let suffix = filename.slice(i + 1);

    return this.IMAGE_SUFFIX.has(suffix);
  }

  /**
   * Get file extension
   * @param {*} path
   * @returns fileExtension, or "" if there isn't one
   */
  private _parseFileExt(path: string): string {
    if (!path || path.endsWith(".")) {
      return "";
    }
    let i = path.lastIndexOf(".");
    if (i <= 0) {
      return "";
    }
    return path.substring(i + 1);
  }

  private _setDisplayedFileName(): void {
    const files = this.uploadParam.files;

    const firstFile: File = files[0];
    if (this._isSingleUpload()) {
      this.displayedUploadName = firstFile.name;
    } else {
      const fn = firstFile.name;
      if (this._isZipCompressed()) {
        const j = fn.lastIndexOf(".");
        this.displayedUploadName = (j > 0 ? fn.slice(0, j) : fn) + ".zip";
      } else {
        this.displayedUploadName = `Batch Upload: ${files.length} files in total`;
      }
    }
  }

  private _resetFileUploadParam(): void {
    if (this.isUploading) return;

    this.isCompressed = false;
    this.uploadParam = emptyUploadFileParam();
    this.uploadFileInput.nativeElement.value = null;
    this.uploadIndex = -1;
    this.displayedUploadName = null;
    this.progress = null;
    this.paginator.firstPage();
  }

  private _prepNextUpload(): UploadFileParam {
    if (!this.isUploading) return null;
    if (this._isSingleUpload() || this._isZipCompressed()) return null;

    let i = this.uploadIndex; // if this is the first one, i will be -1
    let files = this.uploadParam.files;
    let next_i = i + 1;

    if (next_i >= files.length) return null;

    let next = files[next_i];
    if (!next) return null;

    this.uploadIndex = next_i;

    return {
      fileName: next.name,
      files: [next],
      userGroup: this.uploadParam.userGroup,
    };
  }

  private _validateFileExt(name: string): boolean {
    let file_ext = this._parseFileExt(name);
    if (!file_ext) {
      this.notifi.toast(`File extension must not be empty`);
      return false;
    }

    if (!this._isFileExtSupported(file_ext)) {
      this.notifi.toast(`File extension '${file_ext}' isn't supported`);
      return false;
    }
    return true;
  }

  private _isFileExtSupported(fileExt: string): boolean {
    if (!fileExt) return false;

    fileExt = fileExt.toLowerCase();
    if (!this.fileExtSet.has(fileExt)) {
      return false;
    }
    return true;
  }

  private _doUpload(uploadParam: UploadFileParam) {
    const name = uploadParam.fileName;
    this.uploadSub = this.fileService.postFile(uploadParam).subscribe({
      next: (event) => {
        if (event.type === HttpEventType.UploadProgress) {
          // how many files left
          let remaining;
          let index = this.uploadIndex;
          if (index == -1) remaining = "";
          else {
            let files = this.uploadParam.files;
            if (!files) remaining = "";
            else {
              let len = files.length;
              if (index >= len) remaining = "";
              else remaining = `${len - this.uploadIndex - 1} file remaining`;
            }
          }

          // upload progress
          let p = Math.round((100 * event.loaded) / event.total).toFixed(2);
          let ps;
          if (p == "100.00")
            ps = `Processing '${uploadParam.fileName}' ... ${remaining}`;
          else ps = `Uploading ${uploadParam.fileName} ${p}% ${remaining}`;
          this.progress = ps;
        }
      },
      complete: () => {
        // Delay this because the uploaded file may not yet be written to the database
        setTimeout(() => this.fetchFileInfoList(), 1_000);

        let next = this._prepNextUpload();
        if (!next) {
          this.progress = null;
          this.isUploading = false;
          this._resetFileUploadParam();
        } else {
          this._doUpload(next); // upload next file
        }
      },
      error: () => {
        this.progress = null;
        this.isUploading = false;
        this.notifi.toast(`Failed to upload file ${name}`);
        this._resetFileUploadParam();
      },
    });
  }

  private _isZipCompressed() {
    return this._isMultipleUpload() && this.isCompressed;
  }

  private _isBatchUpload() {
    return this._isMultipleUpload() && !this.isCompressed;
  }

  private _divideUnit(size: number, unit: number): string {
    return (size / unit).toFixed(1);
  }

  private _isSingleUpload() {
    return !this._isMultipleUpload();
  }

  private _isMultipleUpload() {
    return this.uploadParam.files.length > 1;
  }
}
