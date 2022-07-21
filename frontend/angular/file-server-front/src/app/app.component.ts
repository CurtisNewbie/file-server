import { Component } from "@angular/core";
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
  activeLink: string = "";
  links: string[] = [
    "/home-page",
    "/manage-extension",
    "/manage-fsgroup",
    "/manage-tasks",
    "/task-history",
  ];
  /*

    <mat-menu #menu="matMenu">
        <button mat-menu-item routerLink="/home-page" routerLinkActive="active">Home
            Page</button>
        <ng-container *ngIf="isAdmin">
            <button mat-menu-item routerLink="/manage-extension" routerLinkActive="active">File
                Extensions</button>
            <button mat-menu-item routerLink="/manage-fsgroup" routerLinkActive="active">Manage FsGroups</button>
            <button mat-menu-item routerLink="/manage-tasks" routerLinkActive="active">Manage Tasks</button>
            <button mat-menu-item routerLink="/task-history" routerLinkActive="active">Task History</button>
        </ng-container>
  */

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.fetchUserInfo();
    this.userService.userInfoObservable.subscribe({
      next: (user) => {
        this.isAdmin = user.role === "admin";
        this.userInfo = user;
      },
    });
    this.userService.isLoggedInObservable.subscribe({
      next: (isLoggedIn) => {
        if (!isLoggedIn) {
          this.isAdmin = false;
          this.userInfo = null;
        }
      },
    });
  }
}
