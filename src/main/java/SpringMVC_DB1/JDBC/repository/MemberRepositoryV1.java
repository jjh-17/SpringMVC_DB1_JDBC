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

// JDBC - DataSource, JdbcUtils 사용
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV1 {

    private final DataSource dataSource;

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


    //Id를 이용한 탐색
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

    //정보 수정
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
