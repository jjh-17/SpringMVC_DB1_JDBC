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