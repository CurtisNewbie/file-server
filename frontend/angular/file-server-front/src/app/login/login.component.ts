import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { UserService } from "../user.service";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"],
})
export class LoginComponent implements OnInit {
  usernameInput: string = "";
  passwordInput: string = "";

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit() {}

  /**
   * add user (only admin is allowed)
   */
  public addUser(): void {}

  /**
   * login request
   */
  public login(): void {
    if (!this.usernameInput || !this.passwordInput) {
      window.alert("Please enter username and password");
    }
    this.userService.login(
      this.usernameInput,
      this.passwordInput,
      this.routeToHomePage
    );
  }

  private routeToHomePage(): void {
    this.router.navigateByUrl("home-page");
  }
}
