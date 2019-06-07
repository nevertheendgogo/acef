create database acef;
use acef;

#友情链接
create table friendly_link(
	`id` bigint(10) primary key auto_increment comment'自增id',
	`description` varchar(50) not null comment'加载失败时显示的描述',
	`link` varchar(100) not null comment'链接',
    `imgPath` varchar(55) not null comment'图片映射路径'
)engine=InnoDB default charset=utf8mb4;

#富文本图片
create table rich_text_picture(
	`id` bigint(10) primary key auto_increment comment'自增id',
	`articleId` bigint(20) not null comment'文章id',
	`url` char(55) not null comment'图片映射路径'
)engine=InnoDB default charset=utf8mb4;

#活动文章保存
create table activityArticle(
	`articleId` bigint(20) primary key comment'文章id', 
    `language` char(7) not null comment'语言',
    `title` varchar(80) comment'标题',
    `author` varchar(80) comment'作者',
    `displayTime` date comment'发布时间',
    `activityStartTime` date comment'活动开始时间',
    `activityEndTime` date comment'活动结束时间',
    `entryFormUrl` varchar(100) comment'报名表映射路径',
    `posterUrl` varchar(55) comment'海报映射路径',
    `content` varchar(10000) comment'文章内容'
)engine=InnoDB default charset=utf8mb4;


#(轮播图,协会介绍)
create table picture(
	`id` bigint(10) primary key auto_increment comment'自增id',
    `part` varchar(10) comment'标志图片用途',
    `url` varchar(100) comment'图片相对项目路径',
    `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment'数据创建时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#创建注册用户表
create table user(
	`id` bigint(20) primary key auto_increment comment'自增id',
	`emailAccount` varchar(30) unique not null comment'邮箱账号',
    `password` varchar(34) not null comment'密码',
    `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment'用户注册时间',
    `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment'用户信息更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


#创建数据表,存放成员展示信息
create table show_member(
	`id` bigint(20) primary key auto_increment comment'自增id',
	`chName` varchar(20) comment'成员名',
    `chPos` varchar(30) comment'职位',
    `chDes` varchar(5000) comment'简介',
	`frName` varchar(20) comment'成员名',
    `frPos` varchar(30) comment'职位',
    `frDes` varchar(5000) comment'简介',
    `showPriority` bigint(2) comment'展示优先级',
    `imgPath` varchar(100) comment'图片相对项目路径',
    `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment'数据创建时间',
    `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment'数据更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#意见反馈表
create table feedback(
	`id` bigint(20) primary key auto_increment comment'自增id',
    `emailAccount` varchar(30) comment'邮箱',
    `userName` varchar(20) comment'姓名',
    `phone` varchar(13) comment'手机号',
    `title` varchar(80) comment'标题',
	`description` varchar(1000) comment'反馈内容',
    `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment'反馈提交时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
