import { Paging } from "./paging";

/** Access log response vo*/
export interface AccessLog {
  /** when the user signed in */
  accessTime: string;

  /** ip address */
  ipAddress: string;

  /** username */
  username: string;

  /** primary key of user */
  userId: number;
}

export interface FetchAccessLogList {
  /** list of access log */
  accessLogInfoList: AccessLog[];

  /** paging */
  pagingVo: Paging;
}

/**
 * Parameters for fetching list of access log
 */
export interface FetchAccessLogListParam {
  pagingVo: Paging;
}
