import { Component, Input } from "@angular/core";
import { UserInfo } from "src/models/user-info";
import { UserService } from "./user.service";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent {
  isLoggedIn: boolean = false;
  constructor() {}
}
