import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, ParamMap } from "@angular/router";

@Component({
  selector: "app-image-viewer",
  templateUrl: "./image-viewer.component.html",
  styleUrls: ["./image-viewer.component.css"],
})
export class ImageViewerComponent implements OnInit {
  name: string;
  url: string;
  imgHeight: number = 600;
  readonly delta = 100;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this.name = params.get("name");
      this.url = params.get("url");
    });
  }

  makeImageLarger() {
    this.imgHeight += this.delta;
  }

  makeImageSmaller() {
    if (this.imgHeight > this.delta) this.imgHeight -= this.delta;
  }
}
