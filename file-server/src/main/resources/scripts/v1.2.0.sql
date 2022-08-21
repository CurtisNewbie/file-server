alter table file_info add constraint unique uuid_uk (uuid);
alter table file_info modify column uuid VARCHAR(64) NOT NULL COMMENT "file's uuid";

CREATE TABLE IF NOT EXISTS vfolder (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    folder_no VARCHAR(64) NOT NULL COMMENT 'folder no',
    name VARCHAR(255) NOT NULL COMMENT "name of the folder",
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
    create_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
    update_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
    is_del TINYINT NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
    UNIQUE folder_no_uk (folder_no)
) engine=innodb comment="Virtual folder";

CREATE TABLE IF NOT EXISTS user_vfolder (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_no VARCHAR(64) NOT NULL COMMENT 'user no',
    folder_no VARCHAR(64) NOT NULL COMMENT 'folder no',
    ownership VARCHAR(15) NOT NULL DEFAULT 'OWNER' COMMENT "ownership",
    granted_by VARCHAR(64) NOT NULL COMMENT "granted by (user_no)",
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
    create_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
    update_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
    is_del TINYINT NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
    UNIQUE user_folder_uk (user_no, folder_no)
) engine=innodb comment="User and Virtual folder join table";

CREATE TABLE IF NOT EXISTS file_folder (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    folder_no VARCHAR(64) NOT NULL COMMENT 'folder no',
    uuid VARCHAR(64) NOT NULL COMMENT "file's uuid",
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
    create_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
    update_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
    is_del TINYINT NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
    UNIQUE folder_file_uk (folder_no, uuid)
) engine=innodb comment="File and vfolder join table";
