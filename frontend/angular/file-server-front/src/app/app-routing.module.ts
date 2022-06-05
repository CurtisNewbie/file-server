import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { FsGroupComponent } from "./fs-group/fs-group.component";
import { HomePageComponent } from "./home-page/home-page.component";
import { ImageViewerComponent } from "./image-viewer/image-viewer.component";
import { LoginComponent } from "./login/login.component";
import { ManageExtensionComponent } from "./manage-extension/manage-extension.component";
import { ManageTasksComponent } from "./manage-tasks/manage-tasks.component";
import { PdfViewerComponent } from "./pdf-viewer/pdf-viewer.component";
import { TaskHistoryComponent } from "./task-history/task-history.component";

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
    path: "manage-extension",
    component: ManageExtensionComponent,
  },
  {
    path: "manage-fsgroup",
    component: FsGroupComponent,
  },
  {
    path: "manage-tasks",
    component: ManageTasksComponent,
  },
  {
    path: "task-history",
    component: TaskHistoryComponent,
  },
  {
    path: "pdf-viewer",
    component: PdfViewerComponent,
  },
  {
    path: "image-viewer",
    component: ImageViewerComponent,
  },
  { path: "**", redirectTo: "/login-page" },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
