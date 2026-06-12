package gui;

import domain.Account;
import service.AccountService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyAccountPanel extends JPanel {
    private MainFrame mainFrame;
    private AccountService accountService = new AccountService();

    private DefaultTableModel tableModel;
    private JTable accountTable;

    public MyAccountPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("내 계좌 조회", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"계좌ID", "계좌번호", "잔액", "상태", "생성일"};
        tableModel = new DefaultTableModel(columns, 0);
        accountTable = new JTable(tableModel);

        add(new JScrollPane(accountTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton refreshButton = new JButton("새로고침");
        JButton createButton = new JButton("계좌 생성");
        JButton backButton = new JButton("뒤로가기");

        buttonPanel.add(refreshButton);
        buttonPanel.add(createButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadAccounts());
        createButton.addActionListener(e -> createAccount());
        backButton.addActionListener(e -> mainFrame.showHomePanel());
    }

    private void loadAccounts() {
        try {
            tableModel.setRowCount(0);

            int userId = mainFrame.getLoginUser().getUserId();
            List<Account> accounts = accountService.getMyAccounts(userId);

            for (Account account : accounts) {
                tableModel.addRow(new Object[]{
                        account.getAccountId(),
                        account.getAccountNumber(),
                        account.getBalance(),
                        account.getStatus(),
                        account.getCreatedAt()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void createAccount() {
        String password = JOptionPane.showInputDialog(this, "계좌 비밀번호를 입력하세요.");

        if (password == null) {
            return;
        }

        try {
            int userId = mainFrame.getLoginUser().getUserId();

            accountService.createAccount(userId, password);

            JOptionPane.showMessageDialog(this, "계좌 생성 성공");

            loadAccounts();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible && mainFrame.getLoginUser() != null) {
            loadAccounts();
        }
    }
}