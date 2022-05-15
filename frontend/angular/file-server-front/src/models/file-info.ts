import { Paging } from "./paging";

export interface FileInfo {
  /**
   * file's id
   */
  id: number;
  /**
   * uuid
   */
  uuid: string;
  /**
   * fileName
   */
  name: string;

  /** name of uploader */
  uploaderName: string;

  /** upload time */
  uploadTime: string;

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

/** Parameters used for fetching list of file info */
export interface SearchFileInfoParam {
  /** filename */
  name: string;
  /** user group */
  userGroup: number;
  /** ownership */
  ownership: number;
  /** name of tag */
  tagName: string;
}

/** Parameters for uploading a file */
export interface UploadFileParam {
  /** name of the file */
  fileName: string;
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
  /** tagName */
  tagName: string;
}

/** Parameters used for fetching list of file info */
export interface SearchFileInfoParam {
  /** filename */
  name: string;
  /** user group */
  userGroup: number;
  /** ownership */
  ownership: number;
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
    tagName: null,
  };
}

/**
 * Empty object with all properties being null values
 */
export function emptyUploadFileParam(): UploadFileParam {
  return {
    files: null,
    fileName: null,
    userGruop: null,
  };
}

export interface UpdateFileUserGroupParam {
  /** file's id*/
  id: number;

  /** file's userGroup */
  userGroup: number | FileUserGroupEnum;

  /** file's name */
  name: string;
}

export interface ListGrantedAccessResp {
  list: FileAccessGranted[];

  pagingVo: Paging;
}

export interface FileAccessGranted {
  /** id of this file_sharing record */
  id: number;
  /** id of user */
  userId: number;
  /** user who is granted access to this file*/
  username: string;
  /** the date that this access is granted */
  createDate: string;
  /** the access is granted by */
  createdBy: string;
}

export interface Tag {
  id: number;

  /** name of tag */
  name: string;

  /** when the record is created */
  createTime: string;

  /** who created this record */
  createBy: string;
}

export interface ListTagsForFileResp {
  pagingVo: Paging;
  payload: Tag[];
}
