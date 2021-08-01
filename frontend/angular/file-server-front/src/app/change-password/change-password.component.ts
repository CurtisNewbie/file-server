import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import {
  ChangePasswordParam,
  emptyChangePasswordParam,
} from "src/models/request-model";
import { HttpClientService } from "../http-client-service.service";
import { NotificationService } from "../notification.service";
import { UserService } from "../user.service";
import { hasText } from "../util/str-util";

@Component({
  selector: "app-change-password",
  templateUrl: "./change-password.component.html",
  styleUrls: ["./change-password.component.css"],
})
export class ChangePasswordComponent implements OnInit {
  changePasswordParam: ChangePasswordParam = emptyChangePasswordParam();
  newPasswordConfirm: string = null;

  constructor(
    private httpService: HttpClientService,
    private router: Router,
    private userService: UserService,
    private notifi: NotificationService
  ) {}

  ngOnInit() {}

  changePassword() {
    if (
      !hasText(this.changePasswordParam.prevPassword) ||
      !hasText(this.changePasswordParam.newPassword) ||
      !hasText(this.newPasswordConfirm)
    ) {
      this.notifi.toast("Please enter passwords");
      return;
    }

    if (this.changePasswordParam.newPassword !== this.newPasswordConfirm) {
      this.notifi.toast("Confirmed password is not matched");
      return;
    }

    if (
      this.changePasswordParam.prevPassword ===
      this.changePasswordParam.newPassword
    ) {
      this.notifi.toast("new password must be different");
      return;
    }

    this.httpService.changePassword(this.changePasswordParam).subscribe({
      next: (result) => {
        this.notifi.toast("Password changed, please login");
        this.userService.setLogout();
      },
      complete: () => {
        this.changePasswordParam = emptyChangePasswordParam();
        this.newPasswordConfirm = null;
      },
    });
  }
}
