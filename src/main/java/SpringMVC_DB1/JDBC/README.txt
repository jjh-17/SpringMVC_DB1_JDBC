[JDBC 등장 배경]
데이터베이스는 여러가지가 있으며, 각 데이터베이스 마다 연결 방법, SQL 전달 방법, 결과 응답 방법이 상이함
    ==> 데이터베이스 접근을 위한 자바 표준 'JDBC 표준 인터페이스' 등장
        ==> JDBC 드라이버: 각 DB에 맞도록 JDBC 인터페이스를 구현한 라이브러리
            EX) Oracle DB 접근을 위한 Oracle JDBC 드라이버

[DB 커넥션 획득]
1. 어플리케이션 로직이 DB 드라이버를 통해 커넥션 조회
2. DB 드라이버가 DB와 TCP/IP 커넥션을 연결.
3. 연결 이후, DB 드라이버가 ID, PW, 기타 정보를 DB로 전달
4. DB는 ID, PW로 내부 인증 완료. 내부에 DB 세션 생성
5. DB가 커넥션 생성 완료를 알림
6. DB 드라이버가 커넥션객체를 생성하여 클라이언트에 반환

과정이 애무 복잡함

[커넥션 풀]
어플리케이션 시작 시점에 필요한 커넥션을 미리 확보하여 커넥션 풀에 보관
    ==> 이미 TCP/IP로 DB와 연결된 상태 ==> 즉시 SQL을 DB에 전달 가능

[DriverManager VS DataSource]
-DriverManager: 커넥션 획득할 때 마다 URL, USERNAME, PASSWORD 파라미터 필요
-DataSource: 최초 객체 생성 시에만 URL, USERNAME, PASSWORD 필요


[MemberServiceV1 ~ V2 문제점]
<V1>
1. SQLException(JDBC 기술)에 의존
    ==> MemberRepository에서 올라오는 오류이므로 MemberRepository에서 해결이 되어야함
2. MemberRepositry라는 구체 클래스에 직접 의존

<V2>
1. DataSource, Connection, SQLException 등 JDBC 기술에 의존
2. 트랜잭션을 사용하기 위헤 JDBC 기술에 의존
    ==> 비즈니스 로직보다 JDBC를 사용하여 트랜잭션을 처리하는 코드가 더 많음
3. 향후 JPA와 같은 다른 기술로 바꾸어 사용하게 되면 서비스 코드도 모두 수정 필요
4. 핵심 비즈니스 로직과 JDBC 기술이 섞여 있어 유지보수 어려움

<총평>
1. 트랜잭션 문제
    1.1. JDBC 구현 기술이 서비스 계층에 누수됨
        -서비스 계층은 구현 기술을 변경해도 그 원형을 최대한 유지할 수 있어야하나,
         JDBC 기술을 사용하여 유지 불가
    1.2. 트랜잭션 동기화 문제
        -동일한 트랜잭션 유지를 위해 커넥션을 파라미터로 넘겨야함
            => 동일한 기능이라도 트랜잭션용, 트랜잭션 유지 불필요 기능으로 분리 필요
    1.3. 트랜잭션 반복 문제
        -try, catch, finally와 같이 동일한 구절이 반복됨
2. 예외 누수
    2.1. 데이터 접근 계층의 'JDBC 구현 기술 예외'가 서비스 계층으로 전파됨
        ==> SQLException은 체크 예외이므로 데이터 접근 계층을 호출한 서비스 계층에서 해당 예외를 잡아서
            처리, 혹은 throws 필요
    2.3. SQLException은 JDBC 전용 기술. 향후 JPA와 같은 다른 데이터 접근 기술을 사용하면 코드 수정 불가피
3. JDBC 반복 문제
    3.1. MemberRepository내 유사 코드 반복
        -try, catch, finally
        -커넥션 열기, PreparedStatement 사용, 결과 매핑, 실행, 커넥션 및 리소스 정리

[트랜잭션 추상화]
-JdbcTxManager: JDBC 트랜잭션 기능 제공 구현체
-JpaTxManager: JPA 트랜잭션 기능 제공 구현체











































