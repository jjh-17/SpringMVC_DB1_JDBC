-- 세션1, 2 생성
-- 초기화
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA',10000);

-- 세션1
set autocommit false;
update member set money=500 where member_id = 'memberA';

-- 세션2: '락 타임아웃' 설정, 업데이트 대기
SET LOCK_TIMEOUT 60000;
set autocommit false;
update member set money=1000 where member_id = 'memberA';

-- 세션1 커밋 ==> 세션2의 업데이트 수행
commit;

-- 세션2 커밋
commit;