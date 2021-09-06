import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";
import { Component, OnInit } from "@angular/core";
import { PageEvent } from "@angular/material/paginator";
import {
  emptyFsGroup,
  FsGroup,
  FsGroupMode,
  FS_GROUP_MODE_OPTIONS,
} from "src/models/fs-group";
import { PagingController } from "src/models/paging";
import { animateElementExpanding } from "../../animate/animate-util";
import { FileInfoService } from "../file-info.service";
import { UserService } from "../user.service";

@Component({
  selector: "app-fs-group",
  templateUrl: "./fs-group.component.html",
  styleUrls: ["./fs-group.component.css"],
  animations: [animateElementExpanding()],
})
export class FsGroupComponent implements OnInit {
  readonly MODE_READ_ONLY: FsGroupMode = FsGroupMode.READ;
  readonly MODE_READ_WRITE: FsGroupMode = FsGroupMode.READ_WRITE;
  readonly COLUMNS_TO_BE_DISPLAYED: string[] = [
    "id",
    "name",
    "baseFolder",
    "mode",
  ];
  readonly FS_GROUP_MODE_SELECT_OPTIONS = FS_GROUP_MODE_OPTIONS;

  expandedElement: FsGroup = null;
  fsGroups: FsGroup[] = [];
  searchParam: FsGroup = emptyFsGroup();
  pagingController: PagingController = new PagingController();

  constructor(private fileService: FileInfoService) {}

  ngOnInit() {
    this.fetchFsGroups();
  }

  fetchFsGroups() {
    this.fileService
      .fetchFsGroups({
        fsGroup: this.searchParam,
        pagingVo: this.pagingController.paging,
      })
      .subscribe({
        next: (resp) => {
          this.fsGroups = resp.data.fsGroups;
          this.pagingController.updatePages(resp.data.pagingVo.total);
        },
      });
  }

  handle(e: PageEvent): void {
    this.pagingController.handle(e);
    this.fetchFsGroups();
  }

  /**
   * Look at the row, determine whether we should expand this row (return this row)
   *
   * @param row null value if we shouldn't expand this element, else a copy of this row
   * @returns expandedElement
   */
  determineExpandedElement(row: FsGroup): FsGroup {
    return this.idEquals(row, this.expandedElement) ? null : this.copy(row);
  }

  copy(f: FsGroup): FsGroup {
    if (!f) return null;
    return { ...f };
  }

  /**
   * Check if the two fs_group's id are equals
   */
  idEquals(fsl: FsGroup, fsr: FsGroup): boolean {
    if (fsl == null || fsr == null) return false;
    return fsl.id === fsr.id;
  }

  /** Update fs_group's mode */
  updateMode(fs: FsGroup): void {
    this.fileService
      .updateFsGroupMode({
        id: fs.id,
        mode: fs.mode,
      })
      .subscribe({
        next: (r) => {
          this.fetchFsGroups();
        },
      });
  }

  searchNameInputKeyPressed(event: any): void {
    if (event.key === "Enter") {
      console.log("enter");
      this.fetchFsGroups();
    }
  }
}
