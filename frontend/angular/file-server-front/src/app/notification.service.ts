import { Injectable } from "@angular/core";
import { MatSnackBar } from "@angular/material/snack-bar";

@Injectable({
  providedIn: "root",
})
export class NotificationService {
  constructor(private snackBar: MatSnackBar) {}

  public toast(msg: string) {
    this.toastv(msg, "Okay");
  }

  public toastv(msg: string, action: string) {
    this.snackBar.open(msg, action);
  }
}
