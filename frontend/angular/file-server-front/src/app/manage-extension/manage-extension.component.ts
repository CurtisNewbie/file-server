import { Component, OnInit } from "@angular/core";
import { FileExt, FileExtIsEnabled } from "src/models/file-ext";
import { PagingController } from "src/models/paging";
import {
  emptySearchFileExtParam,
  SearchFileExtParam,
} from "src/models/request-model";
import { HttpClientService } from "../http-client-service.service";

@Component({
  selector: "app-manage-extension",
  templateUrl: "./manage-extension.component.html",
  styleUrls: ["./manage-extension.component.css"],
})
export class ManageExtensionComponent implements OnInit {
  readonly FILE_EXT_ENABLED: number = FileExtIsEnabled.ENABLED;
  readonly FILE_EXT_DISABLED: number = FileExtIsEnabled.DISABLED;
  pagingController: PagingController = new PagingController();
  fileExt: FileExt[] = [];
  updateExt: FileExt;
  searchParam: SearchFileExtParam = emptySearchFileExtParam();

  constructor(private httpClient: HttpClientService) {}

  ngOnInit() {
    this.fetchSupportedExtensionsDetails();
  }

  /** fetch supported file extension */
  fetchSupportedExtensionsDetails(): void {
    this.searchParam.pagingVo = this.pagingController.paging;
    this.httpClient
      .fetchSupportedFileExtensionDetails(this.searchParam)
      .subscribe({
        next: (resp) => {
          this.fileExt = resp.data.fileExtList;
          this.pagingController.updatePages(resp.data.pagingVo.total);
        },
      });
  }

  /** Update file extension */
  updateFileExt(): void {
    this.httpClient.updateFileExtension(this.updateExt).subscribe({
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

  /**
   * Set the specified page and fetch the file info list
   * @param page
   */
  gotoPage(page: number): void {
    this.pagingController.setPage(page);
    this.fetchSupportedExtensionsDetails();
  }

  /**
   * Set current page size and fetch the file info list
   * @param pageSize
   */
  setPageSize(pageSize: number): void {
    this.pagingController.setPageLimit(pageSize);
    this.fetchSupportedExtensionsDetails();
  }

  nextPage(): void {
    if (this.pagingController.canGoToNextPage()) {
      this.pagingController.nextPage();
      this.fetchSupportedExtensionsDetails();
    }
  }

  prevPage(): void {
    if (this.pagingController.canGoToPrevPage()) {
      this.pagingController.prevPage();
      this.fetchSupportedExtensionsDetails();
    }
  }

  setIsEnabled(isEnabled: number): void {
    this.searchParam.isEnabled = isEnabled;
  }

  searchNameInputKeyPressed(event: any): void {
    if (event.key === "Enter") {
      console.log("Pressed 'Enter' key, init search file extension list");
      this.fetchSupportedExtensionsDetails();
    }
  }
}
