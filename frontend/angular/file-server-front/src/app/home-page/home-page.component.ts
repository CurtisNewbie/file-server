import { Component, OnInit } from "@angular/core";
import { FileInfo } from "src/models/file-info";
import { HttpClientService } from "../http-client-service.service";

@Component({
  selector: "app-home-page",
  templateUrl: "./home-page.component.html",
  styleUrls: ["./home-page.component.css"],
})
export class HomePageComponent implements OnInit {
  nameInput: string = "";
  fileExtList: string[] = [];
  fileInfoList: FileInfo[] = [];

  constructor(private httpClient: HttpClientService) {}

  ngOnInit() {
    this.fetchSupportedExtensions();
    this.fetchFileInfoList();
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
}
