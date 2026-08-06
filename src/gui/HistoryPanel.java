package gui;

import domain.Account;
import domain.Transaction;
import service.AccountService;
import service.HistoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class HistoryPanel extends JPanel {

    private MainFrame mainFrame;

    private AccountService accountService = new AccountService();
    private HistoryService historyService = new HistoryService();

    private JComboBox<String> accountBox;
    private JTextField startDateField;
    private JTextField endDateField;

    private DefaultTableModel tableModel;
    private JTable historyTable;

    private List<Account> accountList = new ArrayList<>();

    public HistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(10, 10));

        // 상단 영역
        JPanel northPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("거래내역 조회", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        northPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel();

        searchPanel.add(new JLabel("조회 계좌"));

        accountBox = new JComboBox<>();
        searchPanel.add(accountBox);

        JButton allSearchButton = new JButton("전체 조회");
        searchPanel.add(allSearchButton);

        searchPanel.add(new JLabel("시작일"));

        startDateField = new JTextField(10);
        startDateField.setToolTipText("yyyy-MM-dd");
        searchPanel.add(startDateField);

        searchPanel.add(new JLabel("종료일"));

        endDateField = new JTextField(10);
        endDateField.setToolTipText("yyyy-MM-dd");
        searchPanel.add(endDateField);

        JButton periodSearchButton = new JButton("기간 조회");
        JButton detailButton = new JButton("상세 조회");
        JButton backButton = new JButton("뒤로가기");

        searchPanel.add(periodSearchButton);
        searchPanel.add(detailButton);
        searchPanel.add(backButton);

        northPanel.add(searchPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);

        // 거래내역 테이블
        String[] columns = {
                "거래ID", "구분", "상대 계좌ID", "금액", "유형", "상태", "거래일시"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        // 버튼 이벤트
        allSearchButton.addActionListener(e -> loadHistory());
        periodSearchButton.addActionListener(e -> loadHistoryByPeriod());
        detailButton.addActionListener(e -> showTransactionDetail());
        backButton.addActionListener(e -> mainFrame.showHomePanel());
    }

    private void loadAccounts() {
        try {
            accountBox.removeAllItems();
            accountList.clear();

            int userId = mainFrame.getLoginUser().getUserId();

            accountList = accountService.getMyAccounts(userId);

            for (Account account : accountList) {
                accountBox.addItem(account.getAccountNumber());
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // 전체 거래내역 조회
    private void loadHistory() {
        try {
            Account selectedAccount = getSelectedAccount();

            if (selectedAccount == null) {
                return;
            }

            int accountId = selectedAccount.getAccountId();
            int loginUserId = mainFrame.getLoginUser().getUserId();

            List<Transaction> transactions =
                    historyService.getAccountHistory(accountId, loginUserId);

            displayTransactions(accountId, transactions);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // 기간별 거래내역 조회
    private void loadHistoryByPeriod() {
        try {
            Account selectedAccount = getSelectedAccount();

            if (selectedAccount == null) {
                return;
            }

            if (startDateField.getText().isBlank()
                    || endDateField.getText().isBlank()) {

                JOptionPane.showMessageDialog(
                        this,
                        "시작일과 종료일을 입력하세요. (yyyy-MM-dd)"
                );
                return;
            }

            LocalDate startLocalDate =
                    LocalDate.parse(startDateField.getText().trim());

            LocalDate endLocalDate =
                    LocalDate.parse(endDateField.getText().trim());

            Timestamp startDate =
                    Timestamp.valueOf(startLocalDate.atStartOfDay());

            Timestamp endDate =
                    Timestamp.valueOf(
                            endLocalDate.plusDays(1)
                                    .atStartOfDay()
                                    .minusNanos(1)
                    );

            int accountId = selectedAccount.getAccountId();
            int loginUserId = mainFrame.getLoginUser().getUserId();

            List<Transaction> transactions =
                    historyService.getAccountHistoryByPeriod(
                            accountId,
                            loginUserId,
                            startDate,
                            endDate
                    );

            displayTransactions(accountId, transactions);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "날짜 형식은 yyyy-MM-dd로 입력하세요."
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // 거래 상세 조회
    private void showTransactionDetail() {
        try {
            int selectedRow = historyTable.getSelectedRow();

            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "상세 조회할 거래를 선택하세요."
                );
                return;
            }

            Account selectedAccount = getSelectedAccount();

            if (selectedAccount == null) {
                return;
            }

            int transactionId =
                    (int) tableModel.getValueAt(selectedRow, 0);

            int accountId = selectedAccount.getAccountId();
            int loginUserId = mainFrame.getLoginUser().getUserId();

            Transaction transaction =
                    historyService.getTransactionDetail(
                            transactionId,
                            accountId,
                            loginUserId
                    );

            String direction =
                    historyService.getDirection(accountId, transaction);

            int otherAccountId;

            if (direction.equals("출금")) {
                otherAccountId = transaction.getToAccountId();
            } else {
                otherAccountId = transaction.getFromAccountId();
            }

            String message =
                    "거래 ID: " + transaction.getTransactionId()
                            + "\n구분: " + direction
                            + "\n상대 계좌 ID: " + otherAccountId
                            + "\n금액: " + transaction.getAmount()
                            + "\n유형: " + transaction.getType()
                            + "\n상태: " + transaction.getStatus()
                            + "\n거래일시: " + transaction.getCreatedAt();

            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "거래 상세",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // 거래내역 테이블 출력
    private void displayTransactions(
            int accountId,
            List<Transaction> transactions) {

        tableModel.setRowCount(0);

        for (Transaction transaction : transactions) {

            String direction =
                    historyService.getDirection(accountId, transaction);

            int otherAccountId;

            if (direction.equals("출금")) {
                otherAccountId = transaction.getToAccountId();
            } else {
                otherAccountId = transaction.getFromAccountId();
            }

            tableModel.addRow(new Object[]{
                    transaction.getTransactionId(),
                    direction,
                    otherAccountId,
                    transaction.getAmount(),
                    transaction.getType(),
                    transaction.getStatus(),
                    transaction.getCreatedAt()
            });
        }
    }

    private Account getSelectedAccount() {
        int selectedIndex = accountBox.getSelectedIndex();

        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "조회할 계좌가 없습니다."
            );
            return null;
        }

        return accountList.get(selectedIndex);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible && mainFrame.getLoginUser() != null) {
            loadAccounts();
            loadHistory();
        }
    }
}