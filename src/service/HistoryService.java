package service;

import dao.TransactionDAO;
import domain.Transaction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class HistoryService {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final AccountService accountService = new AccountService();

    // 1. 특정 계좌 전체 거래내역 조회
    public List<Transaction> getAccountHistory(int accountId, int loginUserId) {
        accountService.getAccountDetail(accountId, loginUserId);

        return transactionDAO.findByAccountId(accountId);
    }

    // 2. 특정 계좌 기간별 거래내역 조회
    public List<Transaction> getAccountHistoryByPeriod(int accountId, int loginUserId,
                                                       Timestamp startDate, Timestamp endDate) {
        accountService.getAccountDetail(accountId, loginUserId);

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("조회 기간을 입력하세요.");
        }

        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다.");
        }

        return transactionDAO.findByAccountIdAndPeriod(accountId, startDate, endDate);
    }

    // 3. 거래 상세 조회
    public Transaction getTransactionDetail(int transactionId, int accountId, int loginUserId) {
        accountService.getAccountDetail(accountId, loginUserId);

        Transaction transaction = transactionDAO.findById(transactionId);

        if (transaction == null) {
            throw new IllegalArgumentException("존재하지 않는 거래입니다.");
        }

        boolean related =
                transaction.getFromAccountId() == accountId ||
                        transaction.getToAccountId() == accountId;

        if (!related) {
            throw new IllegalArgumentException("해당 계좌의 거래내역이 아닙니다.");
        }

        return transaction;
    }

    // 4. 입금 / 출금 구분 표시용 문자열 목록
    public List<String> getHistoryView(int accountId, int loginUserId) {
        accountService.getAccountDetail(accountId, loginUserId);

        List<Transaction> transactionList = transactionDAO.findByAccountId(accountId);
        List<String> historyViewList = new ArrayList<>();

        for (Transaction transaction : transactionList) {
            String direction = getDirection(accountId, transaction);

            String historyText =
                    "[" + direction + "] "
                            + "금액: " + transaction.getAmount()
                            + ", 거래일시: " + transaction.getCreatedAt()
                            + ", 상태: " + transaction.getStatus();

            historyViewList.add(historyText);
        }

        return historyViewList;
    }

    // 5. 입금 / 출금 구분
    public String getDirection(int accountId, Transaction transaction) {
        if (transaction.getFromAccountId() == accountId) {
            return "출금";
        }

        if (transaction.getToAccountId() == accountId) {
            return "입금";
        }

        throw new IllegalArgumentException("해당 계좌와 관련 없는 거래입니다.");
    }
}