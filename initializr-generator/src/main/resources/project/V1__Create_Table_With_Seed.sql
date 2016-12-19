create table IF NOT EXISTS product (
      id bigint primary key auto_increment comment 'id',
      name varchar(100) comment '名前',
      price int comment '価格',
      lock_version int  NOT NULL comment 'ロックバージョン',
      deleted boolean  NOT NULL comment '削除フラグ',
      created_at datetime(6) NOT NULL comment '作成日時',
      created_by varchar(100) comment '作成者',
      updated_at datetime(6) NOT NULL comment '更新日時',
      updated_by varchar(100) comment '更新者'
);

insert into product (id, name,price,lock_version,deleted,created_at,created_by,updated_at,updated_by) values (1, 'ワゴンＲ　ＦＸ',599000, 0, 0,'2016-12-12 00:00:01','system','2016-12-12 00:00:01','system');
insert into product (id, name,price,lock_version,deleted,created_at,created_by,updated_at,updated_by) values (2, 'Ｎ　ＢＯＸ',850000, 0, 0,'2016-12-12 00:00:01','system','2016-12-12 00:00:01','system');
insert into product (id, name,price,lock_version,deleted,created_at,created_by,updated_at,updated_by) values (3, 'エスティマ',3450000, 0, 0,'2016-12-12 00:00:01','system','2016-12-12 00:00:01','system');
insert into product (id, name,price,lock_version,deleted,created_at,created_by,updated_at,updated_by) values (4, 'car',10000, 0, 0,'2016-12-12 00:00:01','system','2016-12-12 00:00:01','system');
insert into product (id, name,price,lock_version,deleted,created_at,created_by,updated_at,updated_by) values (5, 'car1',10000, 0, 0,'2016-12-12 00:00:01','system','2016-12-12 00:00:01','system');
insert into product (id, name,price,lock_version,deleted,created_at,created_by,updated_at,updated_by) values (6, 'car1',10000, 0, 0,'2016-12-12 00:00:01','system','2016-12-12 00:00:01','system');