-- 세션1, 2 실행
-- 초기화
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA',10000);
insert into member(member_id, money) values ('memberB',10000);

-- 세션1 - 계좌이체
set autocommit false;
update member set money=10000 - 2000 where member_id = 'memberA'; -- 성공
update member set money=10000 + 2000 where member_iddd = 'memberB'; -- 쿼리 예외

-- 쿼리 예외가 발생한 부분은 빼고 변경점이 저장됨 ==> A 계좌에서만 2000원이 빠짐
commit;
select * from member;


-- 초기화
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA',10000);
insert into member(member_id, money) values ('memberB',10000);

-- 초기화 상태로 롤백
set autocommit false;
update member set money=10000 - 2000 where member_id = 'memberA'; -- 성공
update member set money=10000 + 2000 where member_iddd = 'memberB'; -- 쿼리 예외
rollback;
select * from member;

