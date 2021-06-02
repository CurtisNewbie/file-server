import { Component, Input } from "@angular/core";
import { UserInfo } from "src/models/user-info";
import { UserService } from "./user.service";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent {
  constructor(private userService: UserService) {}

  /** logout user  */
  public logout(): void {
    this.userService.logout();
  }

  // todo optimise this, use boolean field instead, might need to find a way for chile-parent components to communicate
  public isUserLoggedIn(): boolean {
    return this.userService.isUserSignedIn();
  }
}
