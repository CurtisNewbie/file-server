import { Component, OnInit } from "@angular/core";
import { UserService } from "../user.service";

@Component({
  selector: "app-nav",
  templateUrl: "./nav.component.html",
  styleUrls: ["./nav.component.css"],
})
export class NavComponent implements OnInit {
  shouldDisplayManagerUser: boolean = false;

  constructor(private userService: UserService) {}

  ngOnInit() {
    if (this.userService.hasUserInfo()) {
      let userInfo = this.userService.getUserInfo();
      this.shouldDisplayManagerUser = userInfo.role === "admin";
    } else {
      console.log("User hasn't logged in yet, unable to find user info");
    }
  }
}
