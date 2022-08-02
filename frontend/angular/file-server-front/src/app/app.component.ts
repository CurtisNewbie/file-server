import { Component } from "@angular/core";
import { links, NLink } from "src/models/link";
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
  base: string = "file-service"; // todo, select this in nav-app ?

  isPermitted(link: NLink): boolean {
    if (!this.userInfo || !this.userInfo.role) return false;
    return link.permitRoles.has(this.userInfo.role);
  }

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
        this.links = links.filter((v, i, a) =>
          this.isPermitted(v) ? v : null
        );
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
}
