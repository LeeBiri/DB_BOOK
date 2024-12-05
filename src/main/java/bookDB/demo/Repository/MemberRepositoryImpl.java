package bookDB.demo.Repository;

import bookDB.demo.Domain.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public MemberRepositoryImpl(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Member addMember(Member member) {
        String sql = "INSERT INTO MEMBERS (MEMBER_ID, MEMBER_NAME, EMAIL, GENDER, ROLE, MEMBERSHIP_LEVEL, OVERDUE_FEE, PASSWORD) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setLong(1, member.getId());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getEmail());
            pstmt.setString(4, member.getGender());
            pstmt.setString(5, member.getRole());
            pstmt.setString(6, member.getMembershipLevel());
            pstmt.setInt(7, member.getOverdueFee());
            pstmt.setString(8, member.getPassword());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 1) {
                return member; // 삽입 성공
            } else {
                throw new SQLException("Member insertion failed");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to add book", e);
        } finally {
            close(conn, pstmt, null); // 리소스 정리
        }
    }

    @Override
    public void deleteById(Long id) {
        System.out.println(id);
        String sql = "DELETE FROM MEMBERS WHERE MEMBER_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, id); // SQL 실행
        if (rowsAffected > 0) {
            System.out.println("Member with ID " + id + " was deleted successfully.");
        } else {
            System.out.println("No member found with ID " + id);
        }
    }

    @Override
    public Member findById(Long id) {
        String sql = "SELECT * FROM MEMBERS WHERE MEMBER_ID = ?";
        List<Member> result = jdbcTemplate.query(sql, new Object[]{id}, memberRowMapper());
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Member> findAll() {
        return jdbcTemplate.query("SELECT * FROM members", memberRowMapper());
    }

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setId(rs.getLong("MEMBER_ID"));
            member.setName(rs.getString("MEMBER_NAME"));
            member.setEmail(rs.getString("EMAIL"));
            member.setGender(rs.getString("GENDER"));
            member.setRole(rs.getString("ROLE"));
            member.setMembershipLevel(rs.getString("membership_level"));
            member.setOverdueFee(rs.getInt("OVERDUE_FEE"));
            member.setPassword(rs.getString("PASSWORD"));

            return member;
        };
    }
}
