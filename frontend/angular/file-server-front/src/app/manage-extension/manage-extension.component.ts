import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";
import { Component, OnInit, Output } from "@angular/core";
import { PageEvent } from "@angular/material/paginator";
import {
  FileExt,
  FileExtIsEnabled,
  FileExtIsEnabledOption,
  FILE_EXT_IS_ENABLED_OPTIONS,
} from "src/models/file-ext";
import { PagingController } from "src/models/paging";
import {
  emptySearchFileExtParam,
  SearchFileExtParam,
} from "src/models/request-model";
import { HttpClientService } from "../http-client-service.service";
import { NotificationService } from "../notification.service";

@Component({
  selector: "app-manage-extension",
  templateUrl: "./manage-extension.component.html",
  styleUrls: ["./manage-extension.component.css"],
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
export class ManageExtensionComponent implements OnInit {
  readonly FILE_EXT_ENABLED: number = FileExtIsEnabled.ENABLED;
  readonly FILE_EXT_DISABLED: number = FileExtIsEnabled.DISABLED;
  readonly COLUMNS_TO_BE_DISPLAYED: string[] = ["id", "name", "status"];
  readonly FILE_EXT_IS_ENABLED_OPTIONS: FileExtIsEnabledOption[] =
    FILE_EXT_IS_ENABLED_OPTIONS;

  pagingController: PagingController = new PagingController();
  fileExt: FileExt[] = [];
  updateExt: FileExt;
  searchParam: SearchFileExtParam = emptySearchFileExtParam();
  expandedElement: FileExt = null;
  addExtPanelDisplayed: boolean = false;
  extToBeAdded: string = null;

  private isSearchParamChagned: boolean = false;

  constructor(
    private httpClient: HttpClientService,
    private notifi: NotificationService
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

  searchNameInputKeyPressed(event: any): void {
    if (event.key === "Enter") {
      console.log("Pressed 'Enter' key, init search file extension list");
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
    this.httpClient.addFileExtension(ext).subscribe({
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
