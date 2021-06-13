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
}

/** Constants class for FileInfo.userGroup */
export class FileUserGroupConst {
  /** public user group, anyone can access to th file */
  public static readonly USER_GROUP_PUBLIC = 0;

  /** private user group, only the uploader can access the file */
  public static readonly USER_GROUP_PRIVATE = 1;
}

/** Response model for fetching file info list */
export interface FetchFileInfoList {
  /** list of file info */
  fileInfoList: FileInfo[];

  /** paging vo */
  pagingVo: Paging;
}

/** Parameters for uploading a file */
export interface UploadFileParam {
  /** name of the file */
  name: string;
  /** file */
  file: File;
  /** user group that the file belongs to */
  userGruop: number;
}

/** Parameters for fetch list of file info */
export interface FetchFileInfoListParam {
  /** filename */
  filename: string;
  /** paging  */
  pagingVo: Paging;
}
