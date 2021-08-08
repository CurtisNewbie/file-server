import { Paging } from "./paging";

export interface OperateLog {
  /** name of operation */
  operateName: string;

  /** description of operation */
  operateDesc: string;

  /** when the operation happens */
  operateTime: string;

  /** parameters used for the operation */
  operateParam: string;

  /** username */
  username: string;

  /** primary key of user */
  userId: number;
}

export interface FetchOperateLogListResp {
  operateLogVoList: OperateLog[];

  pagingVo: Paging;
}
