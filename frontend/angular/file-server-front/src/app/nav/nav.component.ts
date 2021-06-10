import { Component } from "@angular/core";
import { Router } from "@angular/router";
import { UserService } from "../user.service";

@Component({
  selector: "app-nav",
  templateUrl: "./nav.component.html",
  styleUrls: ["./nav.component.css"],
})
export class NavComponent {
  shouldDisplayManagerUser: boolean = false;
  isLoggedIn: boolean = false;

  constructor(private userService: UserService, private router: Router) {
    this.userService.roleObservable.subscribe({
      next: (role) => {
        this.shouldDisplayManagerUser = role === "admin";
      },
    });
    this.userService.isLoggedInObservable.subscribe({
      next: (isLoggedIn) => {
        this.isLoggedIn = isLoggedIn;
      },
    });
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
