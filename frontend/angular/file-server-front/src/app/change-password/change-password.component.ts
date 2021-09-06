import { Component, OnInit } from "@angular/core";
import {
  ChangePasswordParam,
  emptyChangePasswordParam,
} from "src/models/user-info";
import { NavigationService, NavType } from "../navigation.service";
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
    private nav: NavigationService,
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

    this.userService.changePassword(this.changePasswordParam).subscribe({
      next: (result) => {
        this.notifi.toast("Password changed");
        this.nav.navigateTo(NavType.HOME_PAGE);
      },
      complete: () => {
        this.changePasswordParam = emptyChangePasswordParam();
        this.newPasswordConfirm = null;
      },
    });
  }
}
