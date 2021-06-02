import { Injectable, Output } from "@angular/core";
import { EventEmitter } from "@angular/core";
import { HttpClientService } from "./http-client-service.service";

@Injectable({
  providedIn: "root",
})
export class UserService {
  private isSignedIn: boolean = false;

  constructor(private httpClient: HttpClientService) {}

  /**
   * Attempt to signin
   * @param username
   * @param password
   */
  public login(username: string, password: string, onSuccessCallback: any) {
    this.httpClient.login(username, password).subscribe({
      next: (errMsg) => {
        console.log(errMsg);
        if (errMsg) {
          window.alert(errMsg);
          return;
        }
        this.isSignedIn = true;
        console.log("User successfully logged in");
        onSuccessCallback();
      },
    });
  }

  /**
   * Check if current user has signed in
   */
  public isUserSignedIn(): boolean {
    return this.isSignedIn;
  }

  /**
   * Logout current user
   */
  public logout(): void {
    this.httpClient.logout().subscribe({
      complete: () => {
        this.isSignedIn = false;
        console.log("User successfully logged out");
      },
    });
  }
}
