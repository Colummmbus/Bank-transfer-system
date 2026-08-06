package service;

import dao.AccountDAO;
import dao.TransactionDAO;
import domain.Account;
import domain.Transaction;
import exception.AccountNotFoundException;
import exception.InsufficientBalanceException;
import exception.TransferFailedException;
import exception.TransferLimitExceededException;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransferService {

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final AccountService accountService = new AccountService();

    public void transfer(int loginUserId, String fromAccountNumber, String toAccountNumber,
                         long amount, String accountPassword) {

        validateTransferRequest(fromAccountNumber, toAccountNumber, amount, accountPassword);

        Account fromAccount = accountDAO.findWithdrawAccount(fromAccountNumber);
        if (fromAccount == null) {
            throw new AccountNotFoundException("출금 계좌가 존재하지 않습니다.");
        }

        Account toAccount = accountDAO.findDepositAccount(toAccountNumber);
        if (toAccount == null) {
            throw new AccountNotFoundException("입금 계좌가 존재하지 않습니다.");
        }

        accountService.validateAccountOwner(fromAccount.getAccountId(), loginUserId);
        accountService.validateAccountStatus(fromAccount.getAccountId());
        accountService.validateAccountStatus(toAccount.getAccountId());
        accountService.validateAccountPassword(fromAccount.getAccountId(), accountPassword);
        accountService.validateOneTimeLimit(fromAccount.getAccountId(), amount);

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            List<Account> lockedAccounts = accountDAO.lockAccountsInOrder(
                    conn,
                    fromAccount.getAccountId(),
                    toAccount.getAccountId()
            );

            if (lockedAccounts.size() < 2) {
                throw new TransferFailedException("계좌 락 처리에 실패했습니다.");
            }

            Account lockedFromAccount = lockedAccounts.stream()
                    .filter(account -> account.getAccountId() == fromAccount.getAccountId())
                    .findFirst()
                    .orElseThrow(() ->
                            new TransferFailedException("출금 계좌 락 처리에 실패했습니다."));

            // Lock 획득 후 최신 데이터 기준으로 검증
            validateSufficientBalance(lockedFromAccount, amount);
            validateDailyLimit(conn, lockedFromAccount, amount);

            int withdrawResult = accountDAO.withdraw(
                    conn,
                    fromAccount.getAccountId(),
                    amount
            );

            if (withdrawResult == 0) {
                throw new TransferFailedException("출금 처리에 실패했습니다.");
            }

            int depositResult = accountDAO.deposit(
                    conn,
                    toAccount.getAccountId(),
                    amount
            );

            if (depositResult == 0) {
                throw new TransferFailedException("입금 처리에 실패했습니다.");
            }

            Transaction transaction = new Transaction(
                    fromAccount.getAccountId(),
                    toAccount.getAccountId(),
                    amount,
                    "TRANSFER",
                    "SUCCESS"
            );

            int transactionId = transactionDAO.save(conn, transaction);

            if (transactionId <= 0) {
                throw new TransferFailedException("거래 기록 저장에 실패했습니다.");
            }

            conn.commit();

        } catch (Exception e) {

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    throw new RuntimeException("롤백 중 오류가 발생했습니다.", rollbackException);
                }
            }

            if (e instanceof AccountNotFoundException)
                throw (AccountNotFoundException) e;

            if (e instanceof TransferLimitExceededException)
                throw (TransferLimitExceededException) e;

            if (e instanceof RuntimeException)
                throw (RuntimeException) e;

            throw new TransferFailedException("이체 처리 중 오류가 발생했습니다.");

        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException("DB 연결 종료 중 오류가 발생했습니다.", e);
                }
            }
        }
    }

    private void validateTransferRequest(String fromAccountNumber, String toAccountNumber,
                                         long amount, String accountPassword) {

        if (fromAccountNumber == null || fromAccountNumber.isBlank()) {
            throw new IllegalArgumentException("출금 계좌번호를 입력하세요.");
        }

        if (toAccountNumber == null || toAccountNumber.isBlank()) {
            throw new IllegalArgumentException("입금 계좌번호를 입력하세요.");
        }

        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("동일한 계좌로 이체할 수 없습니다.");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("이체 금액은 0보다 커야 합니다.");
        }

        if (accountPassword == null || accountPassword.isBlank()) {
            throw new IllegalArgumentException("계좌 비밀번호를 입력하세요.");
        }
    }

    private void validateSufficientBalance(Account fromAccount, long amount) {

        if (fromAccount.getBalance() < amount) {
            throw new InsufficientBalanceException("잔액이 부족합니다.");
        }
    }

    private void validateDailyLimit(Connection conn, Account fromAccount, long amount) {

        long todayAmount = transactionDAO.getTodayTransferAmount(
                conn,
                fromAccount.getAccountId()
        );

        if (todayAmount + amount > fromAccount.getDailyLimit()) {
            throw new TransferLimitExceededException("1일 이체 한도를 초과했습니다.");
        }
    }
}