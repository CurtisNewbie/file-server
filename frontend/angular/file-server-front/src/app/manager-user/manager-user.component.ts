import { Component, OnInit } from "@angular/core";
import { UserInfo } from "src/models/user-info";
import { UserService } from "../user.service";

@Component({
  selector: "app-manager-user",
  templateUrl: "./manager-user.component.html",
  styleUrls: ["./manager-user.component.css"],
})
export class ManagerUserComponent implements OnInit {
  usernameToBeAdded: string = null;
  passswordToBeAdded: string = null;
  userInfoList: UserInfo[] = [];
  constructor(private userService: UserService) {}

  ngOnInit() {
    this.fetchUserInfoList();
  }

  /**
   * add user (only admin is allowed)
   */
  public addUser(): void {
    if (!this.usernameToBeAdded || !this.passswordToBeAdded) {
      window.alert("Please enter username and password");
    }
    this.userService
      .addGuest(this.usernameToBeAdded, this.passswordToBeAdded)
      .subscribe({
        next: (resp) => {
          if (resp.hasError) {
            window.alert(resp.msg);
            return;
          }
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
        if (resp.hasError) {
          window.alert(resp.msg);
          return;
        }
        this.userInfoList = resp.data;
      },
    });
  }

  public deleteUserById(id: number): void {
    this.userService.deleteUserById(id).subscribe({
      next: (resp) => {
        if (resp.hasError) {
          window.alert(resp.msg);
          return;
        }
        this.fetchUserInfoList();
      },
    });
  }
}
