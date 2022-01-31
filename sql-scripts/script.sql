-- script for creating the table
CREATE TABLE IF NOT EXISTS file_extension (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(15) NOT NULL COMMENT 'name of file extension, e.g., txt',
    is_enabled INT NOT NULL DEFAULT 0 COMMENT 'indicates whether current file extension is disabled, 0-enabled, 1-disabled'
);

CREATE TABLE IF NOT EXISTS file_info (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL COMMENT "name of the file",
    uuid VARCHAR(255) NOT NULL COMMENT "file's uuid",
    is_logic_deleted INT NOT NULL DEFAULT 0 COMMENT "whether the file is logically deleted, 0-normal, 1-deleted",
    is_physic_deleted INT NOT NULL DEFAULT 0 COMMENT "whether the file is physically deleted, 0-normal, 1-deleted",
    size_in_bytes BIGINT NOT NULL COMMENT "size of file in bytes",
    uploader_id INT NOT NULL COMMENT "uploader id, i.e., user.id",
    uploader_name VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'uploader name',
    upload_time DATETIME NOT NULL DEFAULT NOW() COMMENT "upload time",
    logic_delete_time DATETIME DEFAULT NOW() COMMENT "when the file is logically deleted",
    physic_delete_time DATETIME DEFAULT NOW() COMMENT "when the file is physically deleted",
    user_group INT NOT NULL COMMENT "the group that the file belongs to, 0-public, 1-private",
    fs_group_id INT NOT NULL COMMENT 'id of fs_group'
);

CREATE TABLE IF NOT EXISTS file_tag (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT "primary key",
    file_id INT UNSIGNED NOT NULL COMMENT "id of file_info",
    tag_id INT UNSIGNED NOT NULL COMMENT "id of tag",
    user_id INT UNSIGNED NOT NULL COMMENT 'id of user who created this file_tag relation',
    create_time DATETIME NOT NULL DEFAULT NOW() COMMENT 'when the record is created',
    create_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
    update_time DATETIME NOT NULL DEFAULT NOW() COMMENT 'when the record is updated',
    update_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
    is_del TINYINT NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
    CONSTRAINT uk_file_tag UNIQUE (file_id, tag_id)
) ENGINE=InnoDB comment 'join table between file_info and tag';

CREATE TABLE IF NOT EXISTS tag (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT "primary key",
    name VARCHAR(50) NOT NULL COMMENT 'name of tag',
    user_id INT UNSIGNED NOT NULL COMMENT 'user who owns this tag (tags are isolated between different users)',
    create_time DATETIME NOT NULL DEFAULT NOW() COMMENT 'when the record is created',
    create_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
    update_time DATETIME NOT NULL DEFAULT NOW() COMMENT 'when the record is updated',
    update_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
    is_del TINYINT NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
    CONSTRAINT uk_user_tag UNIQUE (user_id, name)
) ENGINE=InnoDB comment 'tag';

CREATE TABLE IF NOT EXISTS file_sharing (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    file_id INT NOT NULL COMMENT "id of file_info",
    user_id INT NOT NULL COMMENT "user who now have access to the file",
    create_date DATETIME NOT NULL DEFAULT NOW() COMMENT "time created",
    created_by VARCHAR(50) NOT NULL DEFAULT "" COMMENT "created by",
    update_date DATETIME NOT NULL DEFAULT NOW() COMMENT "time updated",
    updated_by VARCHAR(50) NOT NULL DEFAULT "" COMMENT "updated by",
    is_del TINYINT NOT NULL DEFAULT 0 COMMENT "is deleted, 0: normal, 1: deleted",
    UNIQUE(file_id, user_id)
) COMMENT "file's sharing information";

CREATE TABLE IF NOT EXISTS fs_group (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL COMMENT "group name",
    base_folder VARCHAR(255) NOT NULL COMMENT "base folder",
    mode INT NOT NULL DEFAULT 2 COMMENT "1-read, 2-read/write"
) COMMENT 'FileSystem group, used to differentiate which base folder or mounted folder should be used';

-- script for inserting some default file extension, these are optional
INSERT INTO file_extension (name,is_enabled) VALUES
	 ('png',0),
	 ('jpg',0),
	 ('jpeg',0),
	 ('pdf',0),
	 ('mp4',0),
	 ('txt',0),
	 ('zip',0),
	 ('7z',0),
	 ('gz',0),
	 ('rar',0),
	 ('docx',0),
	 ('xlsx',0);

