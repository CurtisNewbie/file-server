export interface FsGroup {
  /** id */
  id: number;
  /** name */
  name: string;
  /** baseFolder */
  baseFolder: string;
  /** mode */
  mode: number;
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
  return { id: null, name: "", baseFolder: "", mode: null };
}
