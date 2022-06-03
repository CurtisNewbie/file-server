import { Component, Inject, OnInit } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { PageEvent } from "@angular/material/paginator";
import { Tag } from "src/models/file-info";
import { Paging, PagingController } from "src/models/paging";
import { FileInfoService } from "../file-info.service";
import { NotificationService } from "../notification.service";

export interface ManageTagDialogData {
  fileId: number;
  filename: string;
  autoComplete: string[];
}

@Component({
  selector: "app-manage-tag-dialog",
  templateUrl: "./manage-tag-dialog.component.html",
  styleUrls: ["./manage-tag-dialog.component.css"],
})
export class ManageTagDialogComponent implements OnInit {
  readonly COLUMN_TO_BE_DISPLAYED: string[] = [
    "id",
    "tagName",
    "createTime",
    "createBy",
    "removeButton",
  ];
  tagName: string = "";
  tags: Tag[] = [];
  acTags: string[] = [];
  filtered: string[] = [];
  pagingController: PagingController = new PagingController();

  constructor(
    private fileService: FileInfoService,
    private notifi: NotificationService,
    public dialogRef: MatDialogRef<
      ManageTagDialogComponent,
      ManageTagDialogData
    >,
    @Inject(MAT_DIALOG_DATA) public data: ManageTagDialogData
  ) {
    this.acTags = data.autoComplete;
    console.log(`Autocomplete: ${this.acTags}`);
  }

  ngOnInit() {
    this.fetchTags();
  }

  tagFile(): void {
    if (!this.tagName) {
      this.notifi.toast("Enter tag name first");
      return;
    }

    this.fileService
      .tagFile({ fileId: this.data.fileId, tagName: this.tagName })
      .subscribe({
        next: (resp) => {
          this.fetchTags();
        },
        complete: () => (this.tagName = null),
      });
  }

  fetchTags(): void {
    this.fileService
      .fetchTagsForFile(this.data.fileId, this.pagingController.paging)
      .subscribe({
        next: (resp) => {
          this.tags = resp.data.payload;
          this.pagingController.updatePages(resp.data.pagingVo.total);
        },
      });
  }

  handle(e: PageEvent): void {
    this.pagingController.handle(e);
    this.fetchTags();
  }

  untag(tagName: string): void {
    this.fileService
      .untagFile({ fileId: this.data.fileId, tagName: tagName })
      .subscribe({
        next: (resp) => {
          this.fetchTags();
        },
      });
  }

  onTagNameChanged() {
    this.filtered = this.filter(this.tagName);
    console.log(`Autocomplete: ${this.acTags}`);
  }

  private filter(value: string): string[] {
    if (!value) return this.acTags;

    return this.acTags.filter((option) =>
      option.toLowerCase().includes(value.toLowerCase())
    );
  }
}
