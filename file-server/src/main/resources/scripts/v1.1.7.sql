alter table file_info drop column upload_app;
alter table file_info drop column upload_type;
alter table fs_group add column type VARCHAR(32) NOT NULL DEFAULT 'USER' COMMENT 'FsGroup Type' after mode;

CREATE TABLE IF NOT EXISTS app_file (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL COMMENT 'name of the file',
    uuid VARCHAR(255) NOT NULL COMMENT "file's uuid",
    size BIGINT NOT NULL COMMENT "size of file in bytes",
    app_name VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'app name',
    user_id INT NOT NULL DEFAULT 0 COMMENT "owner's id",
    fs_group_id INT NOT NULL COMMENT 'id of fs_group',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
    create_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
    update_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
    is_del TINYINT NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted'
) engine=InnoDB COMMENT 'Application File';
