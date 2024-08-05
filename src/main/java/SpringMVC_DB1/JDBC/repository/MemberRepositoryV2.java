package SpringMVC_DB1.JDBC.repository;

import SpringMVC_DB1.JDBC.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

//커넥션을 파라미터로 전달하여 동일한 커넥션 사용 유지
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    // 저장
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        try(Connection con = getConnection();
            PreparedStatement psmt = con.prepareStatement(sql);) {
            psmt.setString(1, member.getMemberId());
            psmt.setInt(2, member.getMoney());

            log.info("resultSize={}", psmt.executeUpdate());
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    // 정보 수정 - 다른 커넥션 이용
    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        try(Connection con = getConnection();
            PreparedStatement psmt = con.prepareStatement(sql);) {
            psmt.setInt(1, money);
            psmt.setString(2, memberId);

            log.info("resultSize={}", psmt.executeUpdate());
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    // Id를 이용한 탐색 - 다른 커넥션 이용
    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        try(Connection con = getConnection();
            PreparedStatement psmt = con.prepareStatement(sql);) {
            psmt.setString(1, memberId);

            try(ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member();
                    member.setMemberId(rs.getString("member_id"));
                    member.setMoney(rs.getInt("money"));
                    return member;
                } else {
                    // rs에 데이터가 존재하지 않음
                    throw new NoSuchElementException("memberId = " + memberId + " 탐색 실패");
                }
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    // Id를 이용한 탐색 - Connection 파라미터, 서비스 로직에서 호출
    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        try (PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.setString(1, memberId);

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member();
                    member.setMemberId(rs.getString("member_id"));
                    member.setMoney(rs.getInt("money"));

                    return member;
                } else {
                    throw new NoSuchElementException("memberId = " + memberId + " 탐색 실패");
                }
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    //정보 수정 - Connection 파라미터, 서비스 로직에서 호출
    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        try (PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.setInt(1, money);
            psmt.setString(2, memberId);

            log.info("resultSize={}", psmt.executeUpdate());
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    //정보 삭제
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        try(Connection con = getConnection();
            PreparedStatement psmt = con.prepareStatement(sql);) {
            psmt.setString(1, memberId);

            log.info("resultSize={}", psmt.executeUpdate());
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    private Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        log.info("connection={}, class={}", connection, connection.getClass());
        return connection;
    }

}
