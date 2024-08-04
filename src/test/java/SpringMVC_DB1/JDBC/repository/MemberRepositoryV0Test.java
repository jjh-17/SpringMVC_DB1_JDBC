package SpringMVC_DB1.JDBC.repository;

import SpringMVC_DB1.JDBC.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;

@Slf4j
class MemberRepositoryV0Test {

    private final MemberRepositoryV0 repositoryV0 = new MemberRepositoryV0();

    @Test
    public void crud() throws Exception {

        // save
        Member member = new Member("memberV0", 10000);
        repositoryV0.save(member);

        // findById
        Member findMember = repositoryV0.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        Assertions.assertThat(member).isEqualTo(findMember);

        // update
        repositoryV0.update(member.getMemberId(), 20000);
        Member updateMember = repositoryV0.findById(member.getMemberId());
        Assertions.assertThat(updateMember.getMoney()).isEqualTo(20000);
        Assertions.assertThat(updateMember.getMemberId()).isEqualTo(member.getMemberId());

        // delete
        repositoryV0.delete(member.getMemberId());
        Assertions.assertThatThrownBy(() -> repositoryV0.findById(member.getMemberId()))
                        .isInstanceOf(NoSuchElementException.class);
    }

}