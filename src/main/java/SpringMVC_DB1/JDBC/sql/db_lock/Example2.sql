-- 세션1, 2 생성
-- 초기화
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA',10000);

-- 세션1: 조회 시점 락 획득
set autocommit false;
select * from member where member_id='memberA' for update;

-- 세션2: 업데이트 대기
set autocommit false;
update member set money=1000 where member_id = 'memberA';

-- 세션1 업데이트 및 커밋 ==> 세션2의 업데이트 수행
update member set money=500 where member_id = 'memberA';
commit;

-- 세션2 커밋: money가 1000으로
commit;