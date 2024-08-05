package SpringMVC_DB1.JDBC.service;

import SpringMVC_DB1.JDBC.domain.Member;
import SpringMVC_DB1.JDBC.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

//트랜잭션 템플릿
@Slf4j
public class MemberServiceV3_2 {

    // 트랜잭션 매니저 주입
    private final TransactionTemplate transactionTemplate;
    private final MemberRepositoryV3 memberRepositoryV3;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepositoryV3) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.memberRepositoryV3 = memberRepositoryV3;
    }

    // 롤백 적용
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 비즈니스 로직이 정상 수행되면 커밋, 언체크 예외 발생 시 롤백
        transactionTemplate.executeWithoutResult((status) -> {
            try {
                businessLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
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
    private void businessLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepositoryV3.findById(fromId);
        Member toMember = memberRepositoryV3.findById(toId);

        memberRepositoryV3.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV3.update(toId, toMember.getMoney() + money);
    }

    //예외 상황 가정 메서드
    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
