CREATE TABLE IF NOT EXISTS file_event (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(25) NOT NULL COMMENT 'event type',
  `file_key` varchar(64) NOT NULL COMMENT 'file key',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
  `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
  `update_by` varchar(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
  `is_del` tinyint NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='File Events';