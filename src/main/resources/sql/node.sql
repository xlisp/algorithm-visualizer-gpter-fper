CREATE TABLE `nodes` (
  `id` INT(11) AUTO_INCREMENT,
  `name`  VARCHAR(64)  NOT NULL COMMENT '节点的名称',
  `content`  VARCHAR(64) COMMENT '节点的描述',
  `parid`   INT(11)  NOT NULL COMMENT '父节点的ID',
  `algorithm`   INT(11)  NOT NULL COMMENT '属于哪个算法',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(`id`)
) ENGINE = INNODB CHARSET = UTF8 COMMENT = '用一张表来表示算法对应数据的树还有网络图';
