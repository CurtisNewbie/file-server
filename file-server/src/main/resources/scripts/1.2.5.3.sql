alter table file_info drop index parent_file_idx;
alter table file_info drop index name_idx;
alter table file_info add index parent_file_type_idx (parent_file, file_type);
alter table file_info add index uploader_id_idx (uploader_id);
