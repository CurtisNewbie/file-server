import { Component, OnInit } from "@angular/core";
import { PageEvent } from "@angular/material/paginator";
import { OperateLog } from "src/models/operate-log";
import { PagingController } from "src/models/paging";
import { HttpClientService } from "../http-client-service.service";

@Component({
  selector: "app-operate-history",
  templateUrl: "./operate-history.component.html",
  styleUrls: ["./operate-history.component.css"],
})
export class OperateHistoryComponent implements OnInit {
  operateLogList: OperateLog[] = [];
  pagingController: PagingController = new PagingController();
  COLUMNS_TO_BE_DISPLAYED = [
    "id",
    "user",
    "operateName",
    "operateDesc",
    "operateTime",
    "operateParam",
  ];

  constructor(private http: HttpClientService) {}

  ngOnInit() {
    this.fetchOperateLogList();
  }

  fetchOperateLogList(): void {
    this.http.fetchOperateLogList(this.pagingController.paging).subscribe({
      next: (resp) => {
        this.operateLogList = resp.data.operateLogVoList;
        this.pagingController.updatePages(resp.data.pagingVo.total);
      },
    });
  }

  handle(e: PageEvent): void {
    this.pagingController.handle(e);
    this.fetchOperateLogList();
  }
}
