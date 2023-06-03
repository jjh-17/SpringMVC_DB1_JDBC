-- 두 개 H2 데이터배이스 콘솔 띄우기

-- 데이터 초기화
set autocommit true;
delete from member;
insert into member(member_id, money) values ('oldId',10000);

-- 세션1에 아래 sql문 실행
set autocommit false;
insert into member(member_id, money) values ('newId1',10000);
insert into member(member_id, money) values ('newId2',10000);

-- 커밋 전이기에 세션2에서는 넣은 데이터가 보이지 않음
select * from member;

-- 세션1에서 커밋
commit;

-- 세션1, 2 모두에서 데이터 조회 가능
select * from member;

-- 데이터 초기화
set autocommit true;
delete from member;
insert into member(member_id, money) values ('oldId',10000)

-- 세션1에서 실행
set autocommit false;
insert into member(member_id, money) values ('newId1',10000);
insert into member(member_id, money) values ('newId2',10000);

-- 세션1에서 롤백
rollback;

-- 세션1, 2 모두에서 데이터 조회 실패
select * from member;