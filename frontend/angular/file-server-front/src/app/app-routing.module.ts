import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { HomePageComponent } from "./home-page/home-page.component";
import { LoginComponent } from "./login/login.component";
import { ManagerUserComponent } from "./manager-user/manager-user.component";

// todo login component
// todo register component
const routes: Routes = [
  {
    path: "home-page",
    component: HomePageComponent,
  },
  {
    path: "login",
    component: LoginComponent,
  },
  {
    path: "manage-user",
    component: ManagerUserComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
