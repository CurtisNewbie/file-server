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
}

export enum FileExtIsEnabled {
  /** enabled  */
  ENABLED = 0,
  /** disabled */
  DISABLED = 1,
}
