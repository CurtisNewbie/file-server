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
   * login request
   */
  public login(): void {
    if (!this.usernameInput || !this.passwordInput) {
      window.alert("Please enter username and password");
    }
    this.userService.login(this.usernameInput, this.passwordInput).subscribe({
      next: (resp) => {
        if (resp.hasError) {
          window.alert(resp.msg);
          return;
        }
        // login successful
        this.userService.fetchUserInfo();
        this.routeToHomePage();
      },
      error: () => {
        window.alert("Unknown error occurred, please try again later");
        this.passwordInput = "";
      },
    });
  }

  private routeToHomePage(): void {
    this.router.navigate(["/home-page"]);
  }
}
