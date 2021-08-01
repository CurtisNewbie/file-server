import { Paging } from "./paging";

export interface UserInfo {
  /** id */
  id: number;
  /** username */
  username: string;
  /** role */
  role: string;
  /** whether the user is disabled, 0-normal, 1-disabled */
  isDisabled: number;
}

export interface FetchUserInfoResp {
  fileInfoList: UserInfo[];
  pagingVo: Paging;
}

export enum UserIsDisabledEnum {
  /**
   * User is in normal state
   */
  NORMAL = 0,

  /**
   * User is disabled
   */
  IS_DISABLED = 1,
}

export enum UserRoleEnum {
  /** Administrator */
  ADMIN = "admin",

  /** Normal user */
  USER = "user",

  /** Guest */
  GUEST = "guest",
}

export interface UserIsDisabledOption {
  name: string;
  value: number;
}

export const USER_IS_DISABLED_OPTIONS: UserIsDisabledOption[] = [
  { name: "normal", value: UserIsDisabledEnum.NORMAL },
  { name: "disabled", value: UserIsDisabledEnum.IS_DISABLED },
];

export interface UserRoleOption {
  name: string;
  value: string;
}

export const USER_ROLE_OPTIONS: UserRoleOption[] = [
  { name: "admin", value: UserRoleEnum.ADMIN },
  { name: "user", value: UserRoleEnum.USER },
  { name: "guest", value: UserRoleEnum.GUEST },
];
