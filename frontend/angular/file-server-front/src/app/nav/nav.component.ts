import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { UserService } from "../user.service";

@Component({
  selector: "app-nav",
  templateUrl: "./nav.component.html",
  styleUrls: ["./nav.component.css"],
})
export class NavComponent implements OnInit {
  shouldDisplayManagerUser: boolean = false;

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit() {
    if (!this.userService.hasUserInfo()) {
      this.userService.fetchUserInfo().subscribe({
        next: (resp) => {
          if (resp.hasError) {
            window.alert(resp.msg);
            return;
          }
          this.userService.setUserInfo(resp.data);
          this.shouldDisplayManagerUser = resp.data.role === "admin";
        },
      });
    }
  }

  /** log out current user and navigate back to login page */
  logout(): void {
    this.userService.logout().subscribe({
      complete: () => {
        console.log("Logged out user, navigate back to login page");
        this.router.navigate(["/login-page"]);
      },
    });
  }
}
