import { Paging } from "./paging";

/** Parameters used for fetching list of file info */
export interface SearchFileInfoParam {
  /** filename */
  name: string;
  /** user group */
  userGroup: number;
  /** ownership */
  ownership: number;
}

/** Parameters for uploading a file */
export interface UploadFileParam {
  /** name of the file, if it's a zip file, the first one will be the zip file's name */
  names: string[];
  /** file */
  files: File[];
  /** user group that the file belongs to */
  userGruop: number;
}

/** Parameters for fetching list of file info */
export interface FetchFileInfoListParam {
  /** filename */
  filename: string;
  /** user group */
  userGroup: number;
  /** paging  */
  pagingVo: Paging;
  /** ownership */
  ownership: number;
}

/**
 * Empty object with all properties being null values
 */
export function emptySearchFileInfoParam(): SearchFileInfoParam {
  return {
    name: null,
    userGroup: null,
    ownership: null,
  };
}

/**
 * Empty object with all properties being null values
 */
export function emptyUploadFileParam(): UploadFileParam {
  return {
    files: null,
    name: null,
    userGruop: null,
  };
}

/**
 * Parameters for fetching list of access log
 */
export interface FetchAccessLogListParam {
  pagingVo: Paging;
}

/**
 * Parameters for adding a new user
 */
export interface AddUserParam {
  /** username */
  username: string;
  /** password */
  password: string;
  /** user role */
  userRole: string;
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

/**
 * Parameters for search file extensions
 */
export interface SearchFileExtParam {
  /**
   * name of file extension, e.g., "txt"
   */
  name: string;

  /**
   * whether this file extension is enabled
   */
  isEnabled: number;

  /** paging  */
  pagingVo: Paging;
}

export function emptySearchFileExtParam(): SearchFileExtParam {
  return {
    name: null,
    isEnabled: null,
    pagingVo: null,
  };
}
