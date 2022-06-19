import { Paging } from "./paging";

export interface FsGroup {
  id: number;
  name: string;
  baseFolder: string;
  mode: number;
  type: string;
  updateTime: string;
  updateBy: string;
}

export enum FsGroupMode {
  /** 1 read-only */
  READ = 1,

  /** 2 read/write */
  READ_WRITE = 2,
}

export const FS_GROUP_MODE_OPTIONS: FsGroupModeOption[] = [
  { name: "Read-only", value: FsGroupMode.READ },
  { name: "Read-write", value: FsGroupMode.READ_WRITE },
];

export interface FsGroupModeOption {
  name: string;
  value: FsGroupMode;
}

export function emptyFsGroup(): FsGroup {
  return {
    id: null,
    name: "",
    baseFolder: "",
    mode: null,
    type: null,
    updateBy: null,
    updateTime: null,
  };
}

export interface ListAllFsGroupReqVo {
  fsGroup: FsGroup;

  pagingVo: Paging;
}

export interface UpdateFsGroupModeReqVo {
  /** id of fs_group */
  id: number;

  /** mode */
  mode: number | FsGroupMode;
}

export interface ListAllFsGroupRespVo {
  payload: FsGroup[];

  pagingVo: Paging;
}
