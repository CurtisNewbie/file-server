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

/** Enum for FileInfo.userGroup */
export enum FileUserGroupEnum {
  /** public user group, anyone can access to th file */
  USER_GROUP_PUBLIC = 0,

  /** private user group, only the uploader can access the file */
  USER_GROUP_PRIVATE = 1,
}

/** Enum for file's ownership */
export enum FileOwnershipEnum {
  /** all files  */
  FILE_OWNERSHIP_ALL_FILES = 0,
  /** my files  */
  FILE_OWNERSHIP_MY_FILES = 1,
}

/** Response model for fetching file info list */
export interface FetchFileInfoList {
  /** list of file info */
  fileInfoList: FileInfo[];

  /** paging vo */
  pagingVo: Paging;
}

export interface FileUserGroupOption {
  name: string;
  value: FileUserGroupEnum | number;
}

export const FILE_USER_GROUP_OPTIONS: FileUserGroupOption[] = [
  { name: "Private Group", value: FileUserGroupEnum.USER_GROUP_PRIVATE },
  { name: "Public Group", value: FileUserGroupEnum.USER_GROUP_PUBLIC },
];

export interface FileOwnershipOption {
  name: string;
  value: FileOwnershipEnum | number;
}

export const FILE_OWNERSHIP_OPTIONS: FileOwnershipOption[] = [
  { name: "All", value: FileOwnershipEnum.FILE_OWNERSHIP_ALL_FILES },
  { name: "My Files", value: FileOwnershipEnum.FILE_OWNERSHIP_MY_FILES },
];
