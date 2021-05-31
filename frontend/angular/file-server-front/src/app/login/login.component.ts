import { Component, OnInit } from "@angular/core";
import { HttpClientService } from "../http-client-service.service";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"],
})
export class LoginComponent implements OnInit {
  usernameInput: string = "";
  passwordInput: string = "";

  constructor(private httpClient: HttpClientService) {}

  ngOnInit() {}

  /**
   * add user (only admin is allowed)
   */
  public addUser(): void {}

  /**
   * login request
   */
  public login(): void {
    console.log(this.usernameInput, this.passwordInput);
    if (!this.usernameInput || !this.passwordInput) {
      window.alert("Please enter username and password");
    }

    this.httpClient.login(this.usernameInput, this.passwordInput).subscribe({
      next: (resp) => {
        if (resp.hasError) {
          window.alert(resp.msg);
          return;
        }
      },
    });
  }
}
