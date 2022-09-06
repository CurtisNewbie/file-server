alter table file_info add column file_type varchar(6) not null default 'FILE' comment 'file type: FILE, DIR';
alter table file_info add column parent_file VARCHAR(64) NOT NULL default '' COMMENT "parent file uuid";
