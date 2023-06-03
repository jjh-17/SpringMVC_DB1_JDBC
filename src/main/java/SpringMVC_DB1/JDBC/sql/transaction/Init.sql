-- 기존 테이블 제거
drop table member if exists;

--새 테이블 생성
create table member (
    member_id varchar(10),
    money integer not null default 0,
    primary key (member_id)
);