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

/**
 * Parameters for changing password
 */
export interface ChangePasswordParam {
  /**
   * Previous password
   */
  prevPassword: string;

  /**
   * New password
   */
  newPassword: string;
}

/**
 * Empty object with all properties being null values
 */
export function emptyChangePasswordParam(): ChangePasswordParam {
  return {
    prevPassword: null,
    newPassword: null,
  };
}
