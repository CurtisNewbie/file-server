import { Injectable } from "@angular/core";
import { Router } from "@angular/router";

@Injectable({
  providedIn: "root",
})
export class NavigationService {
  constructor(private router: Router) {}

  /** Navigate to using Router*/
  public navigateTo(nt: NavType, extra?: any[]): void {
    let arr: any[] = [nt];
    if (extra != null) arr = arr.concat(extra);
    this.router.navigate(arr);
  }
}

/** Navigation Type (Where we are navigating to) */
export enum NavType {
  HOME_PAGE = "home-page",
  LOGIN_PAGE = "login-page",
  MANAGE_EXT = "manage-extension",
  MANAGE_USER = "manager-user",
  ACCESS_LOG = "access-log",
  CHANGE_PASSWORD = "change-password",
  TASK_HISTORY = "task-history",
  REGISTER_PAGE = "register",
}
