import { Component, Inject, OnInit } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
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
  grantedTo: string;

  constructor(
    private fileService: FileInfoService,
    private notifi: NotificationService,
    public dialogRef: MatDialogRef<
      GrantAccessDialogComponent,
      GrantAccessDialogData
    >,
    @Inject(MAT_DIALOG_DATA) public data: GrantAccessDialogData
  ) {}

  ngOnInit() {}

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
}
