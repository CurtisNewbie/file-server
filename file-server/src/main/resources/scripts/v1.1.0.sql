alter table file_info add column upload_type TINYINT NOT NULL DEFAULT 0 COMMENT 'upload type: 0-user uploaded, 1-application uploaded' after uploader_id;
alter table file_info add column upload_app VARCHAR(50) NOT NULL DEFAULT '' COMMENT 'app that uploaded this file, only used when the file is uploaded by an app' after upload_type;
alter table file_info modify column uploader_id INT NOT NULL DEFAULT 0 COMMENT "uploader id, i.e., user.id";
