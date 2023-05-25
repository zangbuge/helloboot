-- mysql 5.7+ 设置默认更新时间
create table test(
id integer not null auto_increment primary key,
name varchar(20) not null ,
create_time timestamp not null default CURRENT_TIMESTAMP COMMENT '创建时间',
update_time timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP COMMENT '更新时间');
