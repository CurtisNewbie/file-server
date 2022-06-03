import { HttpClient } from "@angular/common/http";
import { Component, OnInit, ViewChild } from "@angular/core";
import { ActivatedRoute, ParamMap, Params } from "@angular/router";
import { PdfJsViewerComponent } from "ng2-pdfjs-viewer";
import { environment } from "src/environments/environment";

@Component({
  selector: "app-viewer",
  templateUrl: "./pdf-viewer.component.html",
  styleUrls: ["./pdf-viewer.component.css"],
})
export class PdfViewerComponent implements OnInit {
  shortname: string;

  @ViewChild("pdfViewer", { static: true })
  pdfViewer: PdfJsViewerComponent;

  constructor(private route: ActivatedRoute, private httpClient: HttpClient) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {
      const name = params.get("name");
      this.shortname = this.shorten(name);

      const url = params.get("url");

      this.httpClient
        .get(url, {
          responseType: "blob",
          observe: "body",
        })
        .subscribe({
          next: (blob) => {
            this.pdfViewer.pdfSrc = blob;
            this.pdfViewer.refresh();
          },
        });
    });
  }

  private shorten(name: string): string {
    let delimiter = name.lastIndexOf("/");
    if (delimiter < name.length - 1) {
      return name.substring(delimiter + 1, name.length);
    }
  }
}
