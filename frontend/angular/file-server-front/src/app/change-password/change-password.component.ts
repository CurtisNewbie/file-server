import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import {
  ChangePasswordParam,
  emptyChangePasswordParam,
} from "src/models/request-model";
import { HttpClientService } from "../http-client-service.service";
import { UserService } from "../user.service";
import { hasText } from "../util/str-util";

@Component({
  selector: "app-change-password",
  templateUrl: "./change-password.component.html",
  styleUrls: ["./change-password.component.css"],
})
export class ChangePasswordComponent implements OnInit {
  changePasswordParam: ChangePasswordParam = emptyChangePasswordParam();

  constructor(
    private httpService: HttpClientService,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit() {}

  changePassword() {
    if (
      !hasText(this.changePasswordParam.prevPassword) ||
      !hasText(this.changePasswordParam.newPassword)
    ) {
      window.alert("Please enter passwords");
      return;
    }

    if (
      this.changePasswordParam.prevPassword ===
      this.changePasswordParam.newPassword
    ) {
      window.alert("Passwords must be different");
      return;
    }

    this.httpService.changePassword(this.changePasswordParam).subscribe({
      next: (result) => {
        window.alert("Password changed, please login");
        this.userService.setLogout();
      },
      complete: () => {
        this.changePasswordParam = emptyChangePasswordParam();
      },
    });
  }
}
