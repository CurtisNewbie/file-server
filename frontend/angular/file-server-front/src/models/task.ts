import { Paging } from "./paging";
import { Option } from "./select-util";

export interface Task {
  /** id */
  id: number;

  /** job's name */
  jobName: string;

  /** name of bean that will be executed */
  targetBean: string;

  /** cron expression */
  cronExpr: string;

  /** app group that runs this task */
  appGroup: string;

  /** the last time this task was executed */
  lastRunStartTime: string;

  /** the last time this task was finished */
  lastRunEndTime: string;

  /** app that previously ran this task */
  lastRunBy: string;

  /** result of last execution */
  lastRunResult: string;

  /** whether the task is enabled: 0-disabled, 1-enabled */
  enabled: string;

  /** whether the task can be executed concurrently: 0-disabled, 1-enabled */
  concurrentEnabled: string;

  /** update by */
  updateBy: string;

  /** update date */
  updateDate: string;
}

export interface ListTaskByPageReqVo {
  pagingVo: Paging;

  /** job's name */
  jobName: string;

  /** app group that runs this task */
  appGroup: string;

  /** whether the task is enabled: 0-disabled, 1-enabled */
  enabled: number | TaskEnabledEnum;
}

export function emptyListTaskByPageReqVo(): ListTaskByPageReqVo {
  return {
    pagingVo: null,
    jobName: "",
    appGroup: "",
    enabled: null,
  };
}

export interface ListTaskByPageRespVo {
  /** task lists */
  list: Task[];
  /** paging info */
  pagingVo: Paging;
}

export interface UpdateTaskReqVo {
  /** id */
  id: number;

  /** job's name */
  jobName: number;

  /** name of bean that will be executed */
  targetBean: string;

  /** cron expression */
  cronExpr: number;

  /** app group that runs this task */
  appGroup: string;

  /** whether the task is enabled: 0-disabled, 1-enabled */
  enabled: number;

  /** whether the task can be executed concurrently: 0-disabled, 1-enabled */
  concurrentEnabled: number;
}

export enum TaskEnabledEnum {
  /** 0-disabled */
  DISABLED = 0,

  /** 1-enabled */
  ENABLED = 1,
}

export enum TaskConcurrentEnabledEnum {
  /**
   * 0-disabled
   */
  DISABLED = 0,

  /**
   * 1-enabled
   */
  ENABLED = 1,
}

export const TASK_ENABLED_OPTIONS: Option[] = [
  { name: "Disabled", value: TaskEnabledEnum.DISABLED },
  { name: "Enabled", value: TaskEnabledEnum.ENABLED },
];

export const TASK_CONCURRENT_ENABLED_OPTIONS: Option[] = [
  { name: "Disabled", value: TaskConcurrentEnabledEnum.DISABLED },
  { name: "Enabled", value: TaskConcurrentEnabledEnum.ENABLED },
];

export interface TriggerTaskReqVo {
  id: number;
}
