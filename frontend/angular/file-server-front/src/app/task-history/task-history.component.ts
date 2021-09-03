import { Component, OnInit } from "@angular/core";
import { PageEvent } from "@angular/material";
import { ActivatedRoute } from "@angular/router";
import { PagingController } from "src/models/paging";
import { TaskHistory } from "src/models/task";
import { HttpClientService } from "../http-client-service.service";
import { TaskService } from "../task.service";

export interface TaskHistoryData {
  taskId: number;
}

@Component({
  selector: "app-task-history",
  templateUrl: "./task-history.component.html",
  styleUrls: ["./task-history.component.css"],
})
export class TaskHistoryComponent implements OnInit {
  readonly COLUMNS_TO_BE_DISPLAYED: string[] = [
    "taskId",
    "jobName",
    "startTime",
    "endTime",
    "runBy",
    "runResult",
  ];

  jobName: string = null;
  taskId: number = null;
  runBy: string = "";
  pagingController: PagingController = new PagingController();
  taskHistoryList: TaskHistory[] = [];
  startDate: Date = null;
  endDate: Date = null;

  constructor(
    private taskService: TaskService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    // let ti = this.route.snapshot.paramMap.get("id");
    // if (ti != null) this.taskId = Number(ti);
    this.route.paramMap.subscribe((params) => {
      let ti = params.get("taskId");
      if (ti != null) this.taskId = Number(ti);
    });
    this.fetchHistoryList();
  }

  fetchHistoryList() {
    let epochStart = null;
    let epochEnd = null;
    if (this.startDate != null) epochStart = this.startDate.getTime();
    if (this.endDate != null) epochEnd = this.endDate.getTime();
    if (epochStart > epochEnd) {
      let temp = epochStart;
      epochStart = epochEnd;
      epochEnd = temp;
    }

    this.taskService
      .fetchTaskHistory({
        pagingVo: this.pagingController.paging,
        jobName: this.jobName,
        taskId: this.taskId,
        startTime: epochStart,
        endTime: epochEnd,
        runBy: this.runBy,
      })
      .subscribe({
        next: (resp) => {
          this.taskHistoryList = resp.data.list;
          this.pagingController.updatePages(resp.data.pagingVo.total);
        },
      });
  }

  handle(e: PageEvent): void {
    this.pagingController.handle(e);
    this.fetchHistoryList();
  }

  onDatePickerChanged(): void {
    if (this.startDate == null || this.endDate == null) return;
    if (this.startDate > this.endDate) {
      let temp = this.endDate;
      this.endDate = this.startDate;
      this.startDate = temp;
    }
  }

  reset(): void {
    this.jobName = null;
    this.taskId = null;
    this.startDate = null;
    this.endDate = null;
  }

  onEnterKeyPressed(event: any): void {
    if (event.key === "Enter") {
      this.fetchHistoryList();
    }
  }
}
