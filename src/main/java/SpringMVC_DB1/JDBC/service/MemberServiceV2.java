package SpringMVC_DB1.JDBC.service;

import SpringMVC_DB1.JDBC.domain.Member;
import SpringMVC_DB1.JDBC.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//트랜잭션 적용 계좌이체 서비스 - 파라미터 연동, 풀을 고려한 종료
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepositoryV2;

    //롤백 적용
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false);   //자동 커밋 종료 - 트랜잭션 시작
            businessLogic(con, fromId, toId, money);
            con.commit();               //성공 시 커밋
        } catch (Exception e) {
            con.rollback();             //실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }

    //커넥션을 안전하게 종료
    private void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); //커넥션 풀을 고려하여 자동 커밋(디폴트) 활성화
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    //비즈니스 로직
    private void businessLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepositoryV2.findById(con, fromId);
        Member toMember = memberRepositoryV2.findById(con, toId);

        memberRepositoryV2.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV2.update(con, toId, toMember.getMoney() + money);
    }

    //예외 상황 가정 메서드
    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
