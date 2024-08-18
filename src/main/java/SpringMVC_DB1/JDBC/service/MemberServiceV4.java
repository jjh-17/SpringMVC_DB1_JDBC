package SpringMVC_DB1.JDBC.service;

import SpringMVC_DB1.JDBC.domain.Member;
import SpringMVC_DB1.JDBC.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import java.sql.SQLException;

/**
 * 예외 누수 문제 해결
 *  SQLException 제거
 *  MemberRepository 인터페이스 의존
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV4 {

    private final MemberRepository memberRepository;

    // 아래 메서드가 호출되면 트랜잭션을 호출한다. 성공 시 커밋, 실패 시 롤백
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException{
        businessLogic(fromId, toId, money);
    }

    private void businessLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    //예외 상황 가정 메서드
    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
