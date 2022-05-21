alter table file_extension modify create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created';
alter table file_extension modify update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated';

alter table file_info modify create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created';
alter table file_info modify update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated';

alter table file_tag modify create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created';
alter table file_tag modify update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated';

alter table tag modify create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created';
alter table tag modify update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated';

alter table file_sharing change create_date create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created';
alter table file_sharing change update_date update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated';
alter table file_sharing change created_by create_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who created this record';
alter table file_sharing change updated_by update_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who updated this record';

alter table fs_group modify create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created';
alter table fs_group modify update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated';
