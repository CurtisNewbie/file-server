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
  /** name of the file */
  name: string;
  /** file */
  file: File;
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
    file: null,
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
