package dao;

import domain.Account;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // 1. 계좌 생성
    public int save(Account account) {
        String sql = "Insert INTO accounts"
                + "(user_id, account_number, balance, account_password, status, one_time_limit, daily_limit)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            pstmt.setInt(1, account.getUserId());
            pstmt.setString(2, account.getAccountNumber());
            pstmt.setLong(3, account.getBalance());
            pstmt.setString(4, account.getAccountPassword());
            pstmt.setString(5, account.getStatus());
            pstmt.setLong(6, account.getOneTimeLimit());
            pstmt.setLong(7, account.getDailyLimit());

            int rowCount = pstmt.executeUpdate();

            if (rowCount == 0) {
                throw new RuntimeException("계좌 생성에 실패했습니다.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new RuntimeException("생성된 계좌번호를 가져오지 못했습니다.");
            }
        }  catch (SQLException e) {
            throw new RuntimeException("계좌 생성 중 오류가 발생했습니다.", e);
        }
    }

    // 2. 계좌번호 중복 확인
    public boolean existsByAccountNumber(String accountNumber) {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("계좌번호 중복 확인 중 오류가 발생했습니다.", e);
        }
    }

    // 3. account_id 기준 조회
    public Account findById(int accountId) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toAccount(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("계좌 ID 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 4. 계좌번호 기준 조회
    public Account findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toAccount(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("계좌번호 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 5. 로그인 사용자의 계좌 목록 조회
    public List<Account> findByUserId(int userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ? ORDER BY created_at DESC";
        List<Account> accountList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accountList.add(toAccount(rs));
                }
            }
            return accountList;

        } catch (SQLException e) {
            throw new RuntimeException("계좌 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 6. 계좌 소유자 확인
    public boolean isOwner(int accountId, int userId) {
        String sql = "SELECT 1 FROM accounts WHERE account_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("계좌 소유자 확인 중 오류가 발생했습니다.", e);
        }
    }

    // 7. 계좌 상태 조회
    public String getStatus(int accountId) {
        String sql = "SELECT status FROM accounts WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("계좌 상태 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 8. 계좌 비밀번호 조회
    public String getAccountPassword(int accountId) {
        String sql = "SELECT account_password FROM accounts WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("account_password");
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("계좌 비밀번호 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 9. 계좌 잔액 조회
    public long getBalance(int accountId) {
        String sql = "SELECT balance FROM accounts WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("balance");
                }
                return -1;
            }

        } catch (SQLException e) {
            throw new RuntimeException("계좌 잔액 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 10. 1회 이체한도 조회
    public long getOneTimeLimit(int accountId) {
        String sql = "SELECT one_time_limit FROM accounts WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("one_time_limit");
                }
                return -1;
            }

        } catch (SQLException e) {
            throw new RuntimeException("1회 이체한도 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 11. 1일 이체한도 조회
    public long getDailyLimit(int accountId) {
        String sql = "SELECT daily_limit FROM accounts WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("daily_limit");
                }
                return -1;
            }

        } catch (SQLException e) {
            throw new RuntimeException("1일 이체한도 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 12. 출금 계좌 조회
    public Account findWithdrawAccount(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toAccount(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("출금 계좌 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 13. 입금 계좌 조회
    public Account findDepositAccount(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toAccount(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("입금 계좌 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 14. 출금 처리
    public int withdraw(Connection conn, int accountId, long amount) {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, amount);
            pstmt.setInt(2, accountId);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("출금 처리 중 오류가 발생했습니다.", e);
        }
    }

    // 15. 입금 처리
    public int deposit(Connection conn, int accountId, long amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, amount);
            pstmt.setInt(2, accountId);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("입금 처리 중 오류가 발생했습니다.", e);
        }
    }

    // 16. 데드락 방지
    public List<Account> lockAccountsInOrder(Connection conn, int fromAccountId, int toAccountId) {
        String sql = "SELECT * FROM accounts "
                + "WHERE account_id IN (?, ?) "
                + "ORDER BY account_id ASC FOR UPDATE";

        List<Account> lockedAccounts = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fromAccountId);
            pstmt.setInt(2, toAccountId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lockedAccounts.add(toAccount(rs));
                }
            }

            return lockedAccounts;

        } catch (SQLException e) {
            throw new RuntimeException("계좌 락 처리 중 오류가 발생했습니다.", e);
        }
    }

    // ResultSet -> Account
    private Account toAccount(ResultSet rs) throws SQLException {
        int accountId = rs.getInt("account_id");
        int userId = rs.getInt("user_id");
        String accountNumber = rs.getString("account_number");
        long balance = rs.getInt("balance");
        String accountPassword = rs.getString("account_password");
        long oneTimeLimit = rs.getLong("one_time_limit");
        long dailyLimit = rs.getLong("daily_limit");
        String status = rs.getString("status");
        Timestamp createdAt = rs.getTimestamp("created_at");


        return new Account(accountId, userId, accountNumber, balance, accountPassword,
                oneTimeLimit, dailyLimit, status, createdAt);
    }
}
