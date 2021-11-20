import { Component, Inject, OnInit } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { PageEvent } from "@angular/material/paginator";
import { FileAccessGranted } from "src/models/file-info";
import { Paging, PagingController } from "src/models/paging";
import { FileInfoService } from "../file-info.service";
import { NotificationService } from "../notification.service";

export interface GrantAccessDialogData {
  fileId: number;
  fileName: string;
}

@Component({
  selector: "app-grant-access-dialog",
  templateUrl: "./grant-access-dialog.component.html",
  styleUrls: ["./grant-access-dialog.component.css"],
})
export class GrantAccessDialogComponent implements OnInit {
  readonly COLUMN_TO_BE_DISPLAYED: string[] = [
    "userId",
    "username",
    "createDate",
    "createdBy",
  ];
  grantedTo: string;
  grantedAccesses: FileAccessGranted[] = [];
  pagingController: PagingController = new PagingController();

  constructor(
    private fileService: FileInfoService,
    private notifi: NotificationService,
    public dialogRef: MatDialogRef<
      GrantAccessDialogComponent,
      GrantAccessDialogData
    >,
    @Inject(MAT_DIALOG_DATA) public data: GrantAccessDialogData
  ) {}

  ngOnInit() {
    this.fetchAccessGranted();
  }

  grantAccess() {
    if (!this.grantedTo) {
      this.notifi.toast("Enter username first");
      return;
    }

    this.fileService
      .grantFileAccess({
        fileId: this.data.fileId,
        grantedTo: this.grantedTo,
      })
      .subscribe({
        next: () => {
          this.notifi.toast("Access granted");
        },
      });
  }

  fetchAccessGranted() {
    this.fileService
      .listGrantedAccess({
        fileId: this.data.fileId,
        pagingVo: this.pagingController.paging,
      })
      .subscribe({
        next: (resp) => {
          this.grantedAccesses = resp.data.list;
          this.pagingController.updatePages(resp.data.pagingVo.total);
        },
      });
  }

  handle(e: PageEvent): void {
    this.pagingController.handle(e);
    this.fetchAccessGranted();
  }
}
