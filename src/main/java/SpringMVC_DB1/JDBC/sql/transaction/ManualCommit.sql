-- 수동 커밋 모드 설정
set autocommit false;

-- 데이터 삽입
insert into member(member_id, money) values ('data3',10000);
insert into member(member_id, money) values ('data4',10000);

-- 수동 커밋
commit;