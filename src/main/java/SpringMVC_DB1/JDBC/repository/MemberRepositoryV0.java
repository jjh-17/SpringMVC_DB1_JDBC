package SpringMVC_DB1.JDBC.repository;

import SpringMVC_DB1.JDBC.connection.DBConnectionUtil;
import SpringMVC_DB1.JDBC.domain.Member;
import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

// JDBC DriverManager 사용
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "INSERT INTO member(member_id, money) VALUES (?, ?);";

        // Statement의 자식, '?'을 통한 파아미터 바인딩 가능, SQL Injection 공격 예방
        try (Connection con = getConnection();
             PreparedStatement psmt = con.prepareStatement(sql)) {
            // sql의 첫번째, 두번째 ?에 값을 넣는다
            psmt.setString(1, member.getMemberId());
            psmt.setInt(2, member.getMoney());

            // sql을 실제 DB로 전달. 영향을 받은 DB row의 수 반환
            psmt.executeUpdate();

            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    // Id를 이용한 탐색
    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        try (Connection con = getConnection();
             PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.setString(1, memberId);

            // 쿼리문 결과
            try(ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member();
                    member.setMemberId(rs.getString("member_id"));
                    member.setMoney(rs.getInt("money"));

                    return member;
                } else{
                    // rs에 데이터가 존재하지 않음
                    throw new NoSuchElementException("memberId = " + memberId + " 탐색 실패");
                }
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    // 정보 수정
    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        try (Connection con = getConnection();
             PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.setInt(1, money);
            psmt.setString(2, memberId);

            // sql을 실제 DB로 전달. 영향을 받은 DB row의 수 반환
            log.info("resultSize={}", psmt.executeUpdate());
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    // 정보 삭제
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        try (Connection con = getConnection();
             PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.setString(1, memberId);
            log.info("resultSize={}", psmt.executeUpdate());
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
    
}
