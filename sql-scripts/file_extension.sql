-- script for creating the table
CREATE TABLE file_extension (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(15) NOT NULL COMMENT 'name of file extension, e.g., txt',
    is_enabled INT NOT NULL DEFAULT 0 COMMENT 'indicates whether current file extension is disabled, 0-enabled, 1-disabled'
);

-- script for inserting some default file extension, these are optional
INSERT INTO fileServer.file_extension (name,is_enabled) VALUES
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

