-- script for creating the table
CREATE TABLE `file_extension` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(15) NOT NULL COMMENT 'name of file extension, e.g., txt',
  `is_enabled` int NOT NULL DEFAULT '0' COMMENT 'indicates whether current file extension is disabled, 0-enabled, 1-disabled',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  `update_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `file_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT 'name of the file',
  `uuid` varchar(64) NOT NULL COMMENT 'file''s uuid',
  `is_logic_deleted` int NOT NULL DEFAULT '0' COMMENT 'whether the file is logically deleted, 0-normal, 1-deleted',
  `is_physic_deleted` int NOT NULL DEFAULT '0' COMMENT 'whether the file is physically deleted, 0-normal, 1-deleted',
  `size_in_bytes` bigint NOT NULL COMMENT 'size of file in bytes',
  `uploader_id` int NOT NULL DEFAULT '0' COMMENT 'uploader id, i.e., user.id',
  `uploader_name` varchar(255) NOT NULL DEFAULT '' COMMENT 'uploader name',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'upload time',
  `logic_delete_time` datetime DEFAULT NULL COMMENT 'when the file is logically deleted',
  `physic_delete_time` datetime DEFAULT NULL COMMENT 'when the file is physically deleted',
  `user_group` int NOT NULL COMMENT 'the group that the file belongs to, 0-public, 1-private',
  `fs_group_id` int NOT NULL DEFAULT '0' COMMENT 'id of fs_group',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  `update_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
  `file_type` varchar(6) NOT NULL DEFAULT 'FILE' COMMENT 'file type: FILE, DIR',
  `parent_file` varchar(64) NOT NULL DEFAULT '' COMMENT 'parent file uuid',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid_uk` (`uuid`),
  KEY `parent_file_idx` (`parent_file`),
  KEY `name_idx` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `file_tag` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `file_id` int unsigned NOT NULL COMMENT 'id of file_info',
  `tag_id` int unsigned NOT NULL COMMENT 'id of tag',
  `user_id` int unsigned NOT NULL COMMENT 'id of user who created this file_tag relation',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  `update_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
  PRIMARY KEY (`id`),
  KEY `user_id_file_id_idx` (`user_id`,`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='join table between file_info and tag'

CREATE TABLE `tag` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `name` varchar(50) NOT NULL COMMENT 'name of tag',
  `user_id` int unsigned NOT NULL COMMENT 'user who owns this tag (tags are isolated between different users)',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  `update_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag` (`user_id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='tag';

CREATE TABLE `file_sharing` (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_id` int NOT NULL COMMENT 'id of file_info',
  `user_id` int NOT NULL COMMENT 'user who now have access to the file',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  `update_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  `is_del` tinyint NOT NULL DEFAULT '0' COMMENT 'is deleted, 0: normal, 1: deleted',
  PRIMARY KEY (`id`),
  UNIQUE KEY `file_id` (`file_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='file''s sharing information';

CREATE TABLE `fs_group` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT 'group name',
  `base_folder` varchar(255) NOT NULL COMMENT 'base folder',
  `mode` int NOT NULL DEFAULT '2' COMMENT '1-read, 2-read/write',
  `type` varchar(32) NOT NULL DEFAULT 'USER' COMMENT 'FsGroup Type',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  `update_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
  `size` bigint NOT NULL DEFAULT '0' COMMENT 'size in bytes',
  `scan_time` timestamp NULL DEFAULT NULL COMMENT 'previous scan time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='FileSystem group, used to differentiate which base folder or mounted folder should be used';

CREATE TABLE `app_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT 'name of the file',
  `uuid` varchar(255) NOT NULL COMMENT 'file''s uuid',
  `size` bigint NOT NULL COMMENT 'size of file in bytes',
  `app_name` varchar(64) NOT NULL DEFAULT '' COMMENT 'app name',
  `user_id` int NOT NULL DEFAULT '0' COMMENT 'owner''s id',
  `fs_group_id` int NOT NULL COMMENT 'id of fs_group',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  `update_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Application File';

CREATE TABLE `vfolder` (
  `id` int NOT NULL AUTO_INCREMENT,
  `folder_no` varchar(64) NOT NULL COMMENT 'folder no',
  `name` varchar(255) NOT NULL COMMENT 'name of the folder',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  `update_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
  PRIMARY KEY (`id`),
  UNIQUE KEY `folder_no_uk` (`folder_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Virtual folder';

CREATE TABLE `user_vfolder` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_no` varchar(64) NOT NULL COMMENT 'user no',
  `folder_no` varchar(64) NOT NULL COMMENT 'folder no',
  `ownership` varchar(15) NOT NULL DEFAULT 'OWNER' COMMENT 'ownership',
  `granted_by` varchar(64) NOT NULL COMMENT 'granted by (user_no)',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  `update_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_folder_uk` (`user_no`,`folder_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User and Virtual folder join table';

CREATE TABLE `file_vfolder` (
  `id` int NOT NULL AUTO_INCREMENT,
  `folder_no` varchar(64) NOT NULL COMMENT 'folder no',
  `uuid` varchar(64) NOT NULL COMMENT 'file''s uuid',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  `update_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
  PRIMARY KEY (`id`),
  UNIQUE KEY `folder_file_uk` (`folder_no`,`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='File and vfolder join table';

CREATE TABLE `task` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `job_name` varchar(255) NOT NULL COMMENT 'job''s name',
  `target_bean` varchar(255) NOT NULL COMMENT 'name of bean that will be executed',
  `cron_expr` varchar(255) NOT NULL COMMENT 'cron expression',
  `app_group` varchar(255) NOT NULL COMMENT 'app group that runs this task',
  `last_run_start_time` timestamp NULL DEFAULT NULL COMMENT 'the last time this task was executed',
  `last_run_end_time` timestamp NULL DEFAULT NULL COMMENT 'the last time this task was finished',
  `last_run_by` varchar(255) DEFAULT NULL COMMENT 'app that previously ran this task',
  `last_run_result` varchar(255) DEFAULT NULL COMMENT 'result of last execution',
  `enabled` int NOT NULL DEFAULT '0' COMMENT 'whether the task is enabled: 0-disabled, 1-enabled',
  `concurrent_enabled` int DEFAULT '0' COMMENT 'whether the task can be executed concurrently: 0-disabled, 1-enabled',
  `update_date` timestamp NOT NULL COMMENT 'update time',
  `update_by` varchar(255) DEFAULT NULL COMMENT 'updated by',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='task';

CREATE TABLE `task_history` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `task_id` int DEFAULT NULL COMMENT 'task id',
  `start_time` timestamp NULL DEFAULT NULL COMMENT 'start time',
  `end_time` timestamp NULL DEFAULT NULL COMMENT 'end time',
  `run_by` varchar(255) DEFAULT NULL COMMENT 'task triggered by',
  `run_result` varchar(255) DEFAULT NULL COMMENT 'result of last execution',
  `create_time` datetime DEFAULT NULL COMMENT 'create time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='task history';

CREATE TABLE IF NOT EXISTS user_file_access (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT "primary key",
    user_no varchar(64) NOT NULL COMMENT 'user no',
    file_uuid varchar(64) NOT NULL COMMENT 'file key',
    access_type varchar(15) NOT NULL DEFAULT "OWNER" COMMENT 'User Access Type',
    create_time DATETIME NOT NULL DEFAULT NOW() COMMENT 'when the record is created',
    create_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
    update_time DATETIME NOT NULL DEFAULT NOW() COMMENT 'when the record is updated',
    update_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
    is_del TINYINT NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
    KEY user_no_file_uuid_idx (user_no, file_uuid)
) ENGINE=InnoDB COMMENT 'User file access';