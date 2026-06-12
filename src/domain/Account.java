package domain;

import java.sql.Timestamp;

public class Account {
    private int accountId;
    private int userId;
    private String accountNumber;
    private long balance;
    private String accountPassword;
    private long oneTimeLimit;
    private long dailyLimit;
    private String status;
    private Timestamp createdAt;


    public Account() {
    }
    
    // DB 조회용
    public Account(int accountId, int userId, String accountNumber, long balance, String accountPassword,
                   long oneTimeLimit, long dailyLimit, String status, Timestamp createdAt) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountPassword = accountPassword;
        this.oneTimeLimit = oneTimeLimit;
        this.dailyLimit = dailyLimit;
        this.status = status;
        this.createdAt = createdAt;
    }

    // 생성용
    public Account(int userId, String accountNumber, long balance, String accountPassword,
                   long oneTimeLimit, long dailyLimit, String status) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountPassword = accountPassword;
        this.oneTimeLimit = oneTimeLimit;
        this.dailyLimit = dailyLimit;
        this.status = status;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public long getOneTimeLimit() {
        return oneTimeLimit;
    }

    public void setOneTimeLimit(long oneTimeLimit) {
        this.oneTimeLimit = oneTimeLimit;
    }

    public long getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(long dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    // 사용자 조회용
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", oneTimeLimit=" + oneTimeLimit +
                ", dailyLimit=" + dailyLimit +
                '}';
    }

}
