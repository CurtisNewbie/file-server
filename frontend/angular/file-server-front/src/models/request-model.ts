import { Paging } from "./paging";

/** Parameters used for fetching list of file info */
export interface SearchFileInfoParam {
  name: string;
  userGroup: number;
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
  /** user group */
  userGroup: number;
  /** paging  */
  pagingVo: Paging;
}
