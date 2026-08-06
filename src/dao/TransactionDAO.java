package dao;

import domain.Transaction;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // 1. 거래 기록 저장
    public int save(Connection conn, Transaction transaction) {
        String sql = "INSERT INTO transactions "
                + "(from_account_id, to_account_id, amount, type, t_status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, transaction.getFromAccountId());
            pstmt.setInt(2, transaction.getToAccountId());
            pstmt.setLong(3, transaction.getAmount());
            pstmt.setString(4, transaction.getType());
            pstmt.setString(5, transaction.getStatus());

            int rowCount = pstmt.executeUpdate();

            if (rowCount == 0) {
                throw new RuntimeException("거래 기록 저장에 실패했습니다.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new RuntimeException("생성된 transaction_id를 가져오지 못했습니다.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("거래 기록 저장 중 오류가 발생했습니다.", e);
        }
    }

    // 2. 오늘 출금 성공 거래 누적 금액 합계 조회
    public long getTodayTransferAmount(Connection conn, int accountId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total_amount "
                + "FROM transactions "
                + "WHERE from_account_id = ? "
                + "AND type = 'TRANSFER' "
                + "AND t_status = 'SUCCESS' "
                + "AND DATE(t_created_at) = CURDATE()";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total_amount");
                }
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("오늘 이체 누적 금액 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 3. 특정 계좌 전체 거래내역 조회
    public List<Transaction> findByAccountId(int accountId) {
        String sql = "SELECT * FROM transactions "
                + "WHERE from_account_id = ? OR to_account_id = ? "
                + "ORDER BY t_created_at DESC";

        List<Transaction> transactionList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactionList.add(toTransaction(rs));
                }
            }

            return transactionList;

        } catch (SQLException e) {
            throw new RuntimeException("거래내역 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 4. 특정 기간 거래내역 조회
    public List<Transaction> findByAccountIdAndPeriod(int accountId, Timestamp startDate, Timestamp endDate) {
        String sql = "SELECT * FROM transactions "
                + "WHERE (from_account_id = ? OR to_account_id = ?) "
                + "AND t_created_at BETWEEN ? AND ? "
                + "ORDER BY t_created_at DESC";

        List<Transaction> transactionList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);
            pstmt.setTimestamp(3, startDate);
            pstmt.setTimestamp(4, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactionList.add(toTransaction(rs));
                }
            }

            return transactionList;

        } catch (SQLException e) {
            throw new RuntimeException("기간별 거래내역 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 5. 거래 ID 기준 상세 조회
    public Transaction findById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toTransaction(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("거래 상세 조회 중 오류가 발생했습니다.", e);
        }
    }

    // ResultSet -> Transaction
    private Transaction toTransaction(ResultSet rs) throws SQLException {
        int transactionId = rs.getInt("transaction_id");
        int fromAccountId = rs.getInt("from_account_id");
        int toAccountId = rs.getInt("to_account_id");
        long amount = rs.getLong("amount");
        String type = rs.getString("type");
        String status = rs.getString("t_status");
        Timestamp createdAt = rs.getTimestamp("t_created_at");

        return new Transaction(transactionId, fromAccountId, toAccountId, amount,
                type, status, createdAt);
    }
}