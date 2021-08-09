import { Component, Inject } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { MAT_DIALOG_DATA } from "@angular/material/dialog";

export interface ConfirmDialogData {
  msg: string[];
}

@Component({
  selector: "confirm-dialog-component",
  templateUrl: "confirm-dialog.component.html",
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent, ConfirmDialogData>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData
  ) {}
}
