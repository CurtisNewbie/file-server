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
        // login successful
        this.userService.fetchUserInfo();
        this.routeToHomePage();
      },
      complete: () => {
        this.passwordInput = "";
      },
    });
  }

  private routeToHomePage(): void {
    this.router.navigate(["/home-page"]);
  }

  passwordInputKeyPressed(event: any): void {
    if (event.key === "Enter") {
      console.log("Pressed enter, init sign-in procedure");
      this.login();
    }
  }
}
