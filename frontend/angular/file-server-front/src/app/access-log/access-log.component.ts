import { Component, OnInit } from "@angular/core";
import { AccessLog } from "src/models/access-log";
import { Paging, PagingConst, PagingController } from "src/models/paging";
import { HttpClientService } from "../http-client-service.service";

@Component({
  selector: "app-access-log",
  templateUrl: "./access-log.component.html",
  styleUrls: ["./access-log.component.css"],
})
export class AccessLogComponent implements OnInit {
  accessLogList: AccessLog[] = [];
  pagingController: PagingController = new PagingController();

  constructor(private httpClient: HttpClientService) {}

  ngOnInit() {
    this.fetchAccessLogList();
  }

  /**
   * Fetch access log list
   */
  fetchAccessLogList(): void {
    this.httpClient
      .fetchAccessLogList({
        pagingVo: this.pagingController.paging,
      })
      .subscribe({
        next: (resp) => {
          this.accessLogList = resp.data.accessLogInfoList;
          let total = resp.data.pagingVo.total;
          if (total != null) {
            this.pagingController.updatePages(total);
          }
        },
        error: (err) => {
          console.log(err);
        },
      });
  }

  /**
   * Set the specified page and access log list
   * @param page
   */
  gotoPage(page: number): void {
    this.pagingController.setPage(page);
    this.fetchAccessLogList();
  }

  /**
   * Set current page size and fetch the access log list
   * @param pageSize
   */
  setPageSize(pageSize: number): void {
    this.pagingController.setPageLimit(pageSize);
    this.fetchAccessLogList();
  }

  nextPage(): void {
    if (this.pagingController.canGoToNextPage()) {
      this.pagingController.nextPage();
      this.fetchAccessLogList();
    }
  }

  prevPage(): void {
    if (this.pagingController.canGoToPrevPage()) {
      this.pagingController.prevPage();
      this.fetchAccessLogList();
    }
  }

  dateToStr(date: Date): string {
    console.log(typeof date, date);
    let dd = date.getDate();
    let mm = date.getMonth();
    let yyyy = date.getFullYear();
    let HH = date.getHours();
    let MM = date.getMinutes();
    return `${dd}/${mm}/${yyyy} ${HH}:${MM}`;
  }
}
