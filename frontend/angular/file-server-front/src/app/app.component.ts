import { Component } from "@angular/core";
import { NLink, selectLinks } from "src/models/link";
import { UserInfo } from "src/models/user-info";
import { UserService } from "./user.service";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent {
  isAdmin: boolean = false;
  userInfo: UserInfo = null;
  activeRoute: string = "";
  links: NLink[] = [];
  base: string = "file-service";

  onNavClicked = (link: NLink): void => {
    this.activeRoute = link.route;
  };

  isActive = (link: NLink): boolean => {
    return this.activeRoute === link.route;
  };

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.fetchUserInfo();
    this.userService.userInfoObservable.subscribe({
      next: (user) => {
        this.isAdmin = user.role === "admin";
        this.userInfo = user;
        this.selectLinks();
      },
    });
    this.userService.isLoggedInObservable.subscribe({
      next: (isLoggedIn) => {
        if (!isLoggedIn) {
          this.isAdmin = false;
          this.userInfo = null;
          this.links = [];
        }
      },
    });
  }

  onBaseChange(base: string): void {
    this.base = base;
    this.selectLinks();
  }

  selectLinks(): void {
    this.links = selectLinks(
      this.base,
      this.userInfo ? this.userInfo.role : null
    );
  }
}
