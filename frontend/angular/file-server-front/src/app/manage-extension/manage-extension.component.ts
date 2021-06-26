import { Component, OnInit } from "@angular/core";
import { FileExt, FileExtIsEnabled } from "src/models/file-ext";
import { HttpClientService } from "../http-client-service.service";

@Component({
  selector: "app-manage-extension",
  templateUrl: "./manage-extension.component.html",
  styleUrls: ["./manage-extension.component.css"],
})
export class ManageExtensionComponent implements OnInit {
  readonly FILE_EXT_ENABLED: number = FileExtIsEnabled.ENABLED;
  fileExt: FileExt[] = [];
  updateExt: FileExt;

  constructor(private httpClient: HttpClientService) {}

  ngOnInit() {
    this.fetchSupportedExtensionsDetails();
  }

  /** fetch supported file extension */
  fetchSupportedExtensionsDetails(): void {
    this.httpClient.fetchSupportedFileExtensionDetails().subscribe({
      next: (resp) => {
        this.fileExt = resp.data;
      },
    });
  }

  /** Update file extension */
  updateFileExt(): void {
    this.httpClient.updateFileExtension(this.updateExt).subscribe({
      next: (resp) => {
        this.updateExt = null;
        this.fetchSupportedExtensionsDetails();
      },
    });
  }

  enableFileExt(fe: FileExt) {
    this.updateFileIsEnabled(fe, FileExtIsEnabled.ENABLED);
  }

  disableFileExt(fe: FileExt) {
    this.updateFileIsEnabled(fe, FileExtIsEnabled.DISABLED);
  }

  updateFileIsEnabled(fe: FileExt, targetIsEnabled: number): void {
    this.updateExt = fe;
    // if it's the correct value already, skip it
    if (this.updateExt.isEnabled === targetIsEnabled) {
      return;
    }
    this.updateExt.isEnabled = targetIsEnabled;
    this.updateFileExt();
  }
}
