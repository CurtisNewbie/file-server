import { Component, OnInit } from "@angular/core";
import { PageEvent } from "@angular/material/paginator";
import {
  emptySearchFileExtParam,
  FileExt,
  FileExtIsEnabled,
  FileExtIsEnabledOption,
  FILE_EXT_IS_ENABLED_OPTIONS,
  SearchFileExtParam,
} from "src/models/file-ext";
import { PagingController } from "src/models/paging";
import { NotificationService } from "../notification.service";
import { animateElementExpanding } from "../../animate/animate-util";
import { FileInfoService } from "../file-info.service";
import { isMobile } from "../util/env-util";

@Component({
  selector: "app-manage-extension",
  templateUrl: "./manage-extension.component.html",
  styleUrls: ["./manage-extension.component.css"],
  animations: [animateElementExpanding()],
})
export class ManageExtensionComponent implements OnInit {
  readonly FILE_EXT_ENABLED: number = FileExtIsEnabled.ENABLED;
  readonly FILE_EXT_DISABLED: number = FileExtIsEnabled.DISABLED;
  readonly DESKTOP_COLUMNS_TO_BE_DISPLAYED: string[] = [
    "id",
    "name",
    "status",
    "createBy",
    "createTime",
    "updateBy",
    "updateTime",
  ];
  readonly MOBILE_COLUMNS_TO_BE_DISPLAYED: string[] = ["id", "name", "status"];
  readonly FILE_EXT_IS_ENABLED_OPTIONS: FileExtIsEnabledOption[] =
    FILE_EXT_IS_ENABLED_OPTIONS;

  pagingController: PagingController = new PagingController();
  fileExt: FileExt[] = [];
  updateExt: FileExt;
  searchParam: SearchFileExtParam = emptySearchFileExtParam();
  expandedElement: FileExt = null;
  addExtPanelDisplayed: boolean = false;
  extToBeAdded: string = null;
  isMobile: boolean = isMobile();

  private isSearchParamChagned: boolean = false;

  constructor(
    private notifi: NotificationService,
    private fileService: FileInfoService
  ) {}

  ngOnInit() {
    this.fetchSupportedExtensionsDetails();
  }

  /** fetch supported file extension */
  fetchSupportedExtensionsDetails(): void {
    if (this.isSearchParamChagned) {
      this.isSearchParamChagned = false;
      this.pagingController.resetCurrentPage();
    }
    this.searchParam.pagingVo = this.pagingController.paging;
    this.fileService
      .fetchSupportedFileExtensionDetails(this.searchParam)
      .subscribe({
        next: (resp) => {
          this.fileExt = resp.data.payload;
          this.pagingController.updatePages(resp.data.pagingVo.total);
        },
      });
  }

  /** Update file extension */
  updateFileExt(): void {
    this.fileService.updateFileExtension(this.updateExt).subscribe({
      next: (resp) => {
        this.updateExt = null;
        this.fetchSupportedExtensionsDetails();
      },
    });
  }

  enableFileExt(fe: FileExt) {
    this.updateFileIsEnabled(fe, FileExtIsEnabled.ENABLED);
  }

  disableFileExt(fe: FileExt) {
    this.updateFileIsEnabled(fe, FileExtIsEnabled.DISABLED);
  }

  updateFileIsEnabled(fe: FileExt, targetIsEnabled: number): void {
    this.updateExt = fe;
    // if it's the correct value already, skip it
    if (this.updateExt.isEnabled === targetIsEnabled) {
      return;
    }
    this.updateExt.isEnabled = targetIsEnabled;
    this.updateFileExt();
  }

  searchNameInputKeyPressed(event: any): void {
    if (event.key === "Enter") {
      this.fetchSupportedExtensionsDetails();
    }
  }

  handle(e: PageEvent): void {
    this.pagingController.handle(e);
    this.fetchSupportedExtensionsDetails();
  }

  addFileExt(): void {
    let ext: string = this.extToBeAdded;
    if (ext == null || ext.trim() == "") {
      this.notifi.toast("Please enter file extension");
      return;
    }
    ext = ext.trim();
    if (!ext.match(/[0-9a-zA-Z]+/)) {
      this.notifi.toast(
        "File extension should only contains alphabets and numbers"
      );
      return;
    }
    this.fileService.addFileExtension(ext).subscribe({
      next: (resp) => {
        this.notifi.toast(`File extension '${ext}' added`);
      },
      complete: () => {
        this.addExtPanelDisplayed = false;
        this.fetchSupportedExtensionsDetails();
      },
    });
  }
}
