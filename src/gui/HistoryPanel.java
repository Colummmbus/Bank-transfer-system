package gui;

import domain.Account;
import domain.Transaction;
import service.AccountService;
import service.HistoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryPanel extends JPanel {
    private MainFrame mainFrame;

    private AccountService accountService = new AccountService();
    private HistoryService historyService = new HistoryService();

    private JComboBox<String> accountBox;
    private DefaultTableModel tableModel;
    private JTable historyTable;

    private List<Account> accountList = new ArrayList<>();

    public HistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("거래내역 조회", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel();

        topPanel.add(new JLabel("조회 계좌"));
        accountBox = new JComboBox<>();
        topPanel.add(accountBox);

        JButton searchButton = new JButton("조회");
        JButton backButton = new JButton("뒤로가기");

        topPanel.add(searchButton);
        topPanel.add(backButton);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {
                "거래ID", "구분", "상대 계좌ID", "금액", "유형", "상태", "거래일시"
        };

        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);

        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        searchButton.addActionListener(e -> loadHistory());
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

    private void loadHistory() {
        try {
            tableModel.setRowCount(0);

            int selectedIndex = accountBox.getSelectedIndex();

            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(this, "조회할 계좌가 없습니다.");
                return;
            }

            Account selectedAccount = accountList.get(selectedIndex);

            int accountId = selectedAccount.getAccountId();
            int loginUserId = mainFrame.getLoginUser().getUserId();

            List<Transaction> transactions =
                    historyService.getAccountHistory(accountId, loginUserId);

            for (Transaction t : transactions) {
                String direction = historyService.getDirection(accountId, t);

                int otherAccountId;

                if (direction.equals("출금")) {
                    otherAccountId = t.getToAccountId();
                } else {
                    otherAccountId = t.getFromAccountId();
                }

                tableModel.addRow(new Object[]{
                        t.getTransactionId(),
                        direction,
                        otherAccountId,
                        t.getAmount(),
                        t.getType(),
                        t.getStatus(),
                        t.getCreatedAt()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
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