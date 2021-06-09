import { Component, OnInit } from "@angular/core";
import { UserService } from "../user.service";

@Component({
  selector: "app-manager-user",
  templateUrl: "./manager-user.component.html",
  styleUrls: ["./manager-user.component.css"],
})
export class ManagerUserComponent implements OnInit {
  usernameToBeAdded: string = null;
  passswordToBeAdded: string = null;
  constructor(private userService: UserService) {}

  ngOnInit() {}

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
      });
  }
}
