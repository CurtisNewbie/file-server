import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { AccessLogComponent } from "./access-log/access-log.component";
import { ChangePasswordComponent } from "./change-password/change-password.component";
import { FsGroupComponent } from "./fs-group/fs-group.component";
import { HomePageComponent } from "./home-page/home-page.component";
import { LoginComponent } from "./login/login.component";
import { ManageExtensionComponent } from "./manage-extension/manage-extension.component";
import { ManagerUserComponent } from "./manager-user/manager-user.component";

const routes: Routes = [
  {
    path: "home-page",
    component: HomePageComponent,
  },
  {
    path: "login-page",
    component: LoginComponent,
  },
  {
    path: "manage-user",
    component: ManagerUserComponent,
  },
  {
    path: "access-log",
    component: AccessLogComponent,
  },
  {
    path: "change-password",
    component: ChangePasswordComponent,
  },
  {
    path: "manage-extension",
    component: ManageExtensionComponent,
  },

  {
    path: "manage-fsgroup",
    component: FsGroupComponent,
  },
  { path: "**", redirectTo: "/login-page" },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
