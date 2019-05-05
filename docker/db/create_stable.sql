create table article_comment
(
    id          bigint   not null auto_increment,
    content     TEXT     not null,
    posted_time datetime not null,
    article_id  bigint   not null,
    author_id   bigint   not null,
    primary key (id)
) engine = MyISAM;
create table articles
(
    id               bigint   not null auto_increment,
    header           varchar(40),
    publication_date datetime not null,
    text             TEXT     not null,
    author_id        bigint   not null,
    group_id         bigint   not null,
    primary key (id)
) engine = MyISAM;
create table group_membership_permissions
(
    group_membership_group_id bigint      not null,
    group_membership_user_id  bigint      not null,
    permission                varchar(10) not null,
    primary key (group_membership_group_id, group_membership_user_id, permission)
) engine = MyISAM;
create table group_membership
(
    invited  bit,
    group_id bigint not null,
    user_id  bigint not null,
    primary key (group_id, user_id)
) engine = MyISAM;
create table groups
(
    id          bigint      not null auto_increment,
    description varchar(50),
    image_path  varchar(20),
    name        varchar(25) not null,
    type        integer     not null,
    primary key (id)
) engine = MyISAM;
create table keywords
(
    group_id bigint not null,
    keyword  varchar(20)
) engine = MyISAM;
create table users
(
    id        bigint      not null auto_increment,
    email     varchar(50) not null,
    enabled   bit,
    firstname varchar(15) not null,
    lastname  varchar(15) not null,
    roles     varchar(20) not null,
    primary key (id)
) engine = MyISAM;
alter table groups
    add constraint UK_qknyspte24la8b18kbn7e3je3 unique (name);
alter table users
    add constraint UK_sx468g52bpetvlad2j9y0lptc unique (email);
alter table article_comment
    add constraint FKqj9hpugypxtqtsicbx8h968lq foreign key (article_id) references articles (id);
alter table article_comment
    add constraint FKr6n3iwt28icgmsn2bnargxqd1 foreign key (author_id) references users (id);
alter table articles
    add constraint FKe02fs2ut6qqoabfhj325wcjul foreign key (author_id) references users (id);
alter table articles
    add constraint FKbwjc7en0ekyxxe3da4vi49ufc foreign key (group_id) references groups (id);
alter table group_membership_permissions
    add constraint FKpq3p2dvlpmw2b4ovhwp98ky2 foreign key (group_membership_group_id, group_membership_user_id) references group_membership (group_id, user_id);
alter table group_membership
    add constraint FKsc4ykyti548dy9kso0bd3hxf foreign key (group_id) references groups (id);
alter table group_membership
    add constraint FK650cewn9xqbxdo98s53set6ht foreign key (user_id) references users (id);
alter table keywords
    add constraint FKmkltgx4dg833d1c28sib3ku1l foreign key (group_id) references groups (id);
