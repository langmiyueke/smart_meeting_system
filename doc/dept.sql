create table dept
(
    dept_id     bigint auto_increment comment '部门ID'
        primary key,
    parent_id   bigint                                          null comment '上级部门ID',
    dept_name   varchar(50)                                     not null comment '部门名称',
    order_num   int                   default 0                 null comment '显示排序',
    leader      varchar(20)                                     not null comment '负责人',
    phone       varchar(20)                                     null comment '联系电话',
    email       varchar(50)                                     null comment '邮箱',
    status      enum ('正常', '停用') default '正常'            not null comment '部门状态',
    create_time timestamp             default CURRENT_TIMESTAMP null comment '创建时间',
    constraint fk_parent_id
        foreign key (parent_id) references dept (dept_id)
            on update cascade on delete set null
)
    comment '部门信息表';

create index idx_parent_id
    on dept (parent_id);

create index idx_status
    on dept (status);

INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (13, null, '总公司', 1, '王董', null, null, '正常', '2025-06-30 18:08:02');
INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (16, 13, '深圳分公司', 1, '张三', null, null, '正常', '2025-06-30 18:09:00');
INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (17, 13, '上海分公司', 2, '李四', null, null, '正常', '2025-06-30 18:09:00');
INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (18, 16, '研发部', 1, '王技术总监', null, null, '正常', '2025-06-30 18:10:09');
INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (19, 16, '市场部', 2, '赵市场总监', null, null, '正常', '2025-06-30 18:10:09');
INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (20, 16, '人力资源部', 3, '刘HR总监', null, null, '正常', '2025-06-30 18:10:09');
INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (21, 18, '前端组', 1, '陈组长', null, null, '正常', '2025-06-30 18:10:46');
INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (22, 18, '后端组', 2, '林架构师', null, null, '正常', '2025-06-30 18:10:46');
INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (23, 18, '测试组', 3, '周测试经理', null, null, '正常', '2025-06-30 18:10:46');
INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (24, 17, '财务部', 1, '郑财务总监', null, null, '正常', '2025-06-30 18:10:49');
INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (25, 17, '销售部', 2, '吴销售总监', null, null, '正常', '2025-06-30 18:10:49');
INSERT INTO smart_meeting.dept (dept_id, parent_id, dept_name, order_num, leader, phone, email, status, create_time) VALUES (55, 13, '长沙分公司', 3, '王', '', '', '正常', '2025-07-11 18:49:16');
