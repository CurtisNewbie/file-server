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

alter table file_tag drop index idx_user_id;
alter table file_tag add index user_id_file_id_idx (user_id, file_id);

INSERT INTO `task`
    (job_name, target_bean, cron_expr, app_group, enabled, concurrent_enabled, update_date)
VALUES
    ("GenerateUserFileAccessJob", "generateUserFileAccessJob", "0 0 0 ? * *", "file-server",1,0,CURRENT_TIMESTAMP);
