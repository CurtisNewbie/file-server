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

export class UserIsDisabledConst {
  /**
   * User is in normal state
   */
  public static readonly NORMAL = 0;

  /**
   * User is disabled
   */
  public static readonly IS_DISABLED = 1;
}
