use studyDB;

DROP TABLE IF EXISTS `member`;

CREATE TABLE member (
    member_id varchar(10),
    money integer not null default 0,
    primary key (member_id)
);

INSERT INTO member(member_id, money) VALUES ('hi1',10000), ('hi2',20000);