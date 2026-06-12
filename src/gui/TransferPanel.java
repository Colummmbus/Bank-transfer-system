package gui;

import domain.Account;
import service.AccountService;
import service.TransferService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TransferPanel extends JPanel {
    private MainFrame mainFrame;

    private AccountService accountService = new AccountService();
    private TransferService transferService = new TransferService();

    private JComboBox<String> fromAccountBox;
    private JTextField toAccountField;
    private JTextField amountField;
    private JPasswordField accountPasswordField;

    public TransferPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.add(new JLabel("출금 계좌"));
        fromAccountBox = new JComboBox<>();
        panel.add(fromAccountBox);

        panel.add(new JLabel("입금 계좌번호"));
        toAccountField = new JTextField(15);
        panel.add(toAccountField);

        panel.add(new JLabel("이체 금액"));
        amountField = new JTextField(15);
        panel.add(amountField);

        panel.add(new JLabel("계좌 비밀번호"));
        accountPasswordField = new JPasswordField(15);
        panel.add(accountPasswordField);

        JButton transferButton = new JButton("이체");
        JButton backButton = new JButton("뒤로가기");

        panel.add(transferButton);
        panel.add(backButton);

        add(panel);

        transferButton.addActionListener(e -> transfer());
        backButton.addActionListener(e -> mainFrame.showHomePanel());
    }

    private void loadMyAccounts() {
        try {
            fromAccountBox.removeAllItems();

            int userId = mainFrame.getLoginUser().getUserId();
            List<Account> accounts = accountService.getMyAccounts(userId);

            for (Account account : accounts) {
                fromAccountBox.addItem(account.getAccountNumber());
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void transfer() {
        String fromAccountNumber = (String) fromAccountBox.getSelectedItem();
        String toAccountNumber = toAccountField.getText();
        String amountText = amountField.getText();
        String accountPassword = new String(accountPasswordField.getPassword());

        try {
            long amount = Long.parseLong(amountText);

            int loginUserId = mainFrame.getLoginUser().getUserId();

            transferService.transfer(
                    loginUserId,
                    fromAccountNumber,
                    toAccountNumber,
                    amount,
                    accountPassword
            );

            JOptionPane.showMessageDialog(this, "이체 성공");

            clearFields();
            loadMyAccounts();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "금액은 숫자로 입력하세요.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void clearFields() {
        toAccountField.setText("");
        amountField.setText("");
        accountPasswordField.setText("");
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible && mainFrame.getLoginUser() != null) {
            loadMyAccounts();
        }
    }
}