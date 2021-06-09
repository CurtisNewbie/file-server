import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { FileInfo } from "src/models/file-info";
import { HttpClientService } from "../http-client-service.service";
import { UserService } from "../user.service";

const KB_UNIT: number = 1024;
const MB_UNIT: number = 1024 * 1024;
const GB_UNIT: number = 1024 * 1024 * 1024;

@Component({
  selector: "app-home-page",
  templateUrl: "./home-page.component.html",
  styleUrls: ["./home-page.component.css"],
})
export class HomePageComponent implements OnInit {
  nameInput: string = "";
  fileExtList: string[] = [];
  fileInfoList: FileInfo[] = [];

  constructor(
    private httpClient: HttpClientService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    this.fetchSupportedExtensions();
    this.fetchFileInfoList();
  }

  /** log out current user and navigate back to login page */
  logout(): void {
    this.userService.logout().subscribe({
      complete: () => {
        console.log("Logged out user, navigate back to login page");
        this.router.navigate(["/login-page"]);
      },
    });
  }

  /** fetch supported file extension */
  private fetchSupportedExtensions(): void {
    this.httpClient.fetchSupportedFileExtensions().subscribe({
      next: (resp) => {
        if (resp.hasError) {
          window.alert(resp.msg);
          return;
        }
        this.fileExtList = resp.data;
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  /** fetch file info list */
  private fetchFileInfoList(): void {
    this.httpClient.fetchFileInfoList().subscribe({
      next: (resp) => {
        if (resp.hasError) {
          window.alert(resp.msg);
          return;
        }
        this.fileInfoList = resp.data;
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  /** Concatenate url for downloading the file  */
  public concatFilePath(fileName: string): string {
    return "file/download?filePath=" + fileName;
  }

  /** Convert number of bytes to apporpriate unit */
  public resolveSize(sizeInBytes: number): string {
    if (sizeInBytes > GB_UNIT) {
      return this.divideUnit(sizeInBytes, GB_UNIT) + " gb";
    }
    if (sizeInBytes > MB_UNIT) {
      return this.divideUnit(sizeInBytes, MB_UNIT) + " mb";
    }
    return this.divideUnit(sizeInBytes, KB_UNIT) + " kb";
  }

  private divideUnit(size: number, unit: number): string {
    return (size / unit).toFixed(1);
  }
}