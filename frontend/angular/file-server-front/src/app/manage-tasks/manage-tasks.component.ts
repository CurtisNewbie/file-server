import { Component, OnInit } from "@angular/core";
import { PagingConst, PagingController } from "src/models/paging";
import {
  emptyListTaskByPageReqVo,
  ListTaskByPageReqVo,
  Task,
  TaskConcurrentEnabledEnum,
  TaskEnabledEnum,
  TASK_ENABLED_OPTIONS,
  UpdateTaskReqVo,
} from "src/models/task";
import { HttpClientService } from "../http-client-service.service";
import { animateElementExpanding } from "../../animate/animate-util";
import { Option } from "src/models/select-util";
import { PageEvent } from "@angular/material/paginator";

@Component({
  selector: "app-manage-tasks",
  templateUrl: "./manage-tasks.component.html",
  styleUrls: ["./manage-tasks.component.css"],
  animations: [animateElementExpanding()],
})
export class ManageTasksComponent implements OnInit {
  readonly TASKS_ENABLED_OPTS: Option[] = TASK_ENABLED_OPTIONS;
  readonly TASK_ENABLED = TaskEnabledEnum.ENABLED;
  readonly TASK_DISABLED = TaskEnabledEnum.DISABLED;
  readonly CONCURRENT_ENABLED = TaskConcurrentEnabledEnum.ENABLED;
  readonly CONCURRENT_DISABLED = TaskConcurrentEnabledEnum.DISABLED;
  readonly COLUMNS_TO_BE_DISPLAYED = [
    "id",
    "jobName",
    "targetBean",
    "cronExpr",
    "appGroup",
    "lastRunStartTime",
    "lastRunEndTime",
    "lastRunBy",
    "lastRunResult",
    "enabled",
    "concurrentEnabled",
  ];

  tasks: Task[] = [];
  searchParam: ListTaskByPageReqVo = emptyListTaskByPageReqVo();
  updateParam: UpdateTaskReqVo;
  pagingController: PagingController = new PagingController();
  expandedElement: Task;

  constructor(private http: HttpClientService) {}

  ngOnInit() {
    this.fetchTaskList();
  }

  fetchTaskList(): void {
    this.searchParam.pagingVo = this.pagingController.paging;
    this.http.fetchTaskList(this.searchParam).subscribe({
      next: (resp) => {
        this.tasks = resp.data.list;
        this.pagingController.updatePages(resp.data.pagingVo.total);
      },
    });
  }

  copy(task: Task): Task {
    if (task == null) return null;
    return { ...task };
  }

  handle(e: PageEvent): void {
    this.pagingController.handle(e);
    this.fetchTaskList();
  }

  idEquals(tl: Task, tr: Task): boolean {
    if (tl == null || tr == null) return false;
    return tl.id === tr.id;
  }

  determineExpandedElement(row: Task): Task {
    if (this.idEquals(row, this.expandedElement)) return null;
    return this.copy(row);
  }
}
