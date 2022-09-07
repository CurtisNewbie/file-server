alter table file_info add column file_type varchar(6) not null default 'FILE' comment 'file type: FILE, DIR';
alter table file_info add column parent_file VARCHAR(64) NOT NULL default '' COMMENT "parent file uuid";
alter table file_info modify column fs_group_id INT NOT NULL DEFAULT 0 COMMENT 'id of fs_group';