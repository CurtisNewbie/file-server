CREATE TABLE IF NOT EXISTS file_task (
  id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT "primary key",
  task_no varchar(32) NOT NULL DEFAULT '' COMMENT 'task no',
  user_no VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'user no',
  type varchar(20) NOT NULL DEFAULT '' COMMENT 'task type',
  status varchar(20) NOT NULL DEFAULT '' COMMENT 'task status',
  description varchar(100) NOT NULL DEFAULT '' COMMENT 'task description',
  file_key VARCHAR(32) NOT NULL DEFAULT '' comment 'file key',
  start_time TIMESTAMP NULL COMMENT 'start time',
  end_time TIMESTAMP NULL COMMENT 'end time',
  remark VARCHAR(128) NOT NULL DEFAULT '' COMMENT 'remark',
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  create_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  update_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  is_del TINYINT NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
  KEY (task_no),
  KEY (file_key)
) ENGINE=InnoDB COMMENT 'File Task';
