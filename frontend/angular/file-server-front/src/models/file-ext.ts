import { Paging } from "./paging";

/** File extension */
export interface FileExt {
  /**
   * id
   */
  id: number;

  /**
   * name of file extension, e.g., "txt"
   */
  name: string;

  /**
   * whether this file extension is enabled
   */
  isEnabled: number;

  createBy: string;

  createTime: string;

  updateBy: string;

  updateTime: string;
}

export interface FetchFileExtList {
  payload: FileExt[];

  pagingVo: Paging;
}

export enum FileExtIsEnabled {
  /** enabled  */
  ENABLED = 0,
  /** disabled */
  DISABLED = 1,
}

export interface FileExtIsEnabledOption {
  name: string;
  value: FileExtIsEnabled | number;
}

export const FILE_EXT_IS_ENABLED_OPTIONS: FileExtIsEnabledOption[] = [
  { name: "enabled", value: FileExtIsEnabled.ENABLED },
  { name: "disabled", value: FileExtIsEnabled.DISABLED },
];

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
