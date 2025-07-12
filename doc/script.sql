create table if not exists course
(
    id          bigint unsigned auto_increment comment '课程ID（主键）'
        primary key,
    course_name varchar(200)                         not null comment '课程名称',
    cover_url   varchar(500)                         null comment '课程封面图URL（关联文件存储）',
    description text                                 null comment '课程简介',
    video_url   varchar(500)                         null comment '课程视频URL（关联文件存储）',
    author      varchar(100)                         null comment '课程作者',
    create_time datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted  tinyint(1) default 0                 not null comment '逻辑删除（0-未删 1-已删）'
)
    comment '课程管理主表' collate = utf8mb4_unicode_ci;

create table if not exists dept
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
    comment '部门信息表' collate = utf8mb4_unicode_ci;

create index idx_parent_id
    on dept (parent_id);

create index idx_status
    on dept (status);

create table if not exists employee
(
    id              int auto_increment
        primary key,
    username        varchar(255)                        not null,
    password        varchar(255)                        not null,
    role            varchar(255)                        null,
    enterprise_name varchar(255)                        null,
    nickname        varchar(255)                        not null,
    phone           varchar(255)                        null,
    email           varchar(255)                        null,
    sex             varchar(20)                         null,
    state           varchar(20)                         null,
    department      varchar(255)                        null,
    job             varchar(255)                        null,
    comment         varchar(255)                        null,
    create_at       timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists enterprise
(
    enterprise_mark  varchar(20)  not null comment '租户标识'
        primary key,
    name             varchar(20)  not null,
    contact_person   varchar(20)  null,
    phone            varchar(20)  not null,
    manager_username varchar(20)  not null,
    enterprise_icon  text         null comment '图标',
    comment          varchar(255) null
);

create table if not exists meeting
(
    id           int unsigned auto_increment comment '会议主键ID'
        primary key,
    name         varchar(255)         null comment '标题',
    creator      varchar(255)         null comment '会议创建者',
    content      longtext             null comment '内容',
    cover        varchar(255)         null comment '图片封面',
    is_effective tinyint(1)           null comment '是否有效',
    start_time   datetime             null comment '会议开始时间',
    end_time     datetime             null comment '会议结束时间',
    is_deleted   tinyint(1) default 0 not null comment '软删除标记（0表示未删除，1表示已删除）'
)
    comment '健康资讯表' collate = utf8mb4_bin
                         row_format = DYNAMIC;

create table if not exists news
(
    id        bigint auto_increment comment '新闻ID'
        primary key,
    title     varchar(255) not null comment '新闻标题',
    image_url varchar(512) null comment '新闻图片路径',
    author    varchar(100) null comment '作者',
    summary   text         null comment '新闻简介',
    content   longtext     null comment '新闻内容（富文本）',
    tenant_id varchar(20)  not null comment '租户标识',
    constraint fk_news_enterprise
        foreign key (tenant_id) references enterprise (enterprise_mark)
)
    comment '新闻信息表';

create table if not exists user
(
    id               bigint auto_increment
        primary key,
    username         varchar(255) not null comment '用户名',
    password         varchar(255) not null comment '密码',
    role             varchar(20)  not null comment '角色',
    enterprise_name  varchar(255) null comment '企业名称（仅企业用户需要）',
    enterprise_phone varchar(255) null comment '企业联系方式（仅企业用户需要）',
    admin_code       varchar(255) null comment '管理员特有字段',
    constraint username
        unique (username)
)
    charset = latin1;


