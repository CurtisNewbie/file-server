import { Component, OnInit } from "@angular/core";
import { Output, EventEmitter } from "@angular/core";
import { UserInfo } from "src/models/user-info";
import { UserService } from "../user.service";

export interface BaseOpt {
  base: string;
  name: string;
}

@Component({
  selector: "app-nav",
  templateUrl: "./nav.component.html",
  styleUrls: ["./nav.component.css"],
})
export class NavComponent implements OnInit {
  isAdmin: boolean = false;
  userInfo: UserInfo = null;
  baseOptions: BaseOpt[] = [
    {
      base: "file-service",
      name: "File Service",
    },
    // {
    //   base: "fantahsea",
    //   name: "Fantahsea",
    // },
  ];
  base: string = "file-service";

  @Output() baseChangeEvent = new EventEmitter<string>();

  constructor(private userService: UserService) {}

  emitBaseChangeEvent(event: string): void {
    this.base = event;
    this.baseChangeEvent.emit(this.base);
  }

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

  /** log out current user and navigate back to login page */
  logout(): void {
    this.userService.logout();
  }
}
