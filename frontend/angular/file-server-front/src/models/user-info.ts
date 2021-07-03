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
