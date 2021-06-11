-- script for creating the table
CREATE TABLE IF NOT EXISTS file_extension (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(15) NOT NULL COMMENT 'name of file extension, e.g., txt',
    is_enabled INT NOT NULL DEFAULT 0 COMMENT 'indicates whether current file extension is disabled, 0-enabled, 1-disabled'
);

CREATE TABLE IF NOT EXISTS file_info (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(15) NOT NULL COMMENT "name of the file",
    uuid VARCHAR(255) NOT NULL COMMENT "file's uuid",
    is_logic_deleted INT NOT NULL DEFAULT 0 COMMENT "whether the file is logically deleted, 0-normal, 1-deleted",
    is_physic_deleted INT NOT NULL DEFAULT 0 COMMENT "whether the file is physically deleted, 0-normal, 1-deleted",
    size_in_bytes BIGINT NOT NULL COMMENT "size of file in bytes",
    uploader_id INT NOT NULL COMMENT "uploader id, i.e., user.id",
    upload_time DATETIME NOT NULL DEFAULT NOW() COMMENT "upload time",
    logic_delete_time DATETIME DEFAULT NOW() COMMENT "when the file is logically deleted",
    physic_delete_time DATETIME DEFAULT NOW() COMMENT "when the file is physically deleted",
    user_group INT NOT NULL COMMENT "the group that the file belongs to, 0-public, 1-private"
);

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

