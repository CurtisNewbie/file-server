import { Paging } from "./paging";

export interface FileInfo {
  /**
   * uuid
   */
  uuid: string;
  /**
   * fileName
   */
  name: string;

  /**
   * size in bytes
   */
  sizeInBytes: number;

  /**
   * file user group, 0-public, 1-private
   */
  userGroup: number;

  /**
   * Where the file is owned by current user
   */
  isOwner: boolean;
}

/** Constants class for FileInfo.userGroup */
export class FileUserGroupConst {
  /** public user group, anyone can access to th file */
  public static readonly USER_GROUP_PUBLIC = 0;

  /** private user group, only the uploader can access the file */
  public static readonly USER_GROUP_PRIVATE = 1;
}

/** Constants class for file's ownership */
export class FileOwnershipConst {
  /** all files  */
  public static readonly FILE_OWNERSHIP_ALL_FILES = 0;
  /** my files  */
  public static readonly FILE_OWNERSHIP_MY_FILES = 1;
}

/** Response model for fetching file info list */
export interface FetchFileInfoList {
  /** list of file info */
  fileInfoList: FileInfo[];

  /** paging vo */
  pagingVo: Paging;
}
