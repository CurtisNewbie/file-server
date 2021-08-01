import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";
import { Component, OnInit } from "@angular/core";
import { UserInfo, UserIsDisabledEnum } from "src/models/user-info";
import { NotificationService } from "../notification.service";
import { UserService } from "../user.service";

@Component({
  selector: "app-manager-user",
  templateUrl: "./manager-user.component.html",
  styleUrls: ["./manager-user.component.css"],
  animations: [
    trigger("detailExpand", [
      state("collapsed", style({ height: "0px", minHeight: "0" })),
      state("expanded", style({ height: "*" })),
      transition(
        "expanded <=> collapsed",
        animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)")
      ),
    ]),
  ],
})
export class ManagerUserComponent implements OnInit {
  readonly USER_IS_NORMAL = UserIsDisabledEnum.NORMAL;
  readonly USER_IS_DISABLED = UserIsDisabledEnum.IS_DISABLED;
  readonly COLUMNS_TO_BE_DISPLAYED = ["id", "name", "role", "status"];

  usernameToBeAdded: string = null;
  passswordToBeAdded: string = null;
  userRoleOfAddedUser: string = null;
  userInfoList: UserInfo[] = [];
  addUserPanelDisplayed: boolean = false;
  expandedElement: UserInfo = null;

  constructor(
    private userService: UserService,
    private notifi: NotificationService
  ) {}

  ngOnInit() {
    this.fetchUserInfoList();
  }

  /**
   * add user (only admin is allowed)
   */
  public addUser(): void {
    if (!this.usernameToBeAdded || !this.passswordToBeAdded) {
      this.notifi.toast("Please enter username and password");
      return;
    }
    this.userService
      .addUser(
        this.usernameToBeAdded,
        this.passswordToBeAdded,
        this.userRoleOfAddedUser
      )
      .subscribe({
        next: (resp) => {
          console.log("Successfully added guest:", this.usernameToBeAdded);
          this.usernameToBeAdded = null;
          this.passswordToBeAdded = null;
        },
        complete: () => {
          this.fetchUserInfoList();
        },
      });
  }

  public fetchUserInfoList(): void {
    this.userService.fetchUserList().subscribe({
      next: (resp) => {
        this.userInfoList = resp.data;
      },
    });
  }

  public disableUserById(id: number): void {
    this.userService.disableUserById(id).subscribe({
      next: (resp) => {
        this.fetchUserInfoList();
      },
    });
  }

  public enableUserById(id: number): void {
    this.userService.enableUserById(id).subscribe({
      next: (resp) => {
        this.fetchUserInfoList();
      },
    });
  }

  public setUserRole(userRole: string): void {
    this.userRoleOfAddedUser = userRole;
  }
}
