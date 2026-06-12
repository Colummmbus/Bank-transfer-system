package gui;

import domain.User;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    private MainFrame mainFrame;

    private JLabel welcomeLabel;

    public HomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));

        welcomeLabel = new JLabel("로그인 후 이용하세요.", SwingConstants.CENTER);

        JButton myAccountButton = new JButton("내 계좌 조회");
        JButton transferButton = new JButton("이체");
        JButton historyButton = new JButton("거래내역 조회");
        JButton logoutButton = new JButton("로그아웃");

        panel.add(welcomeLabel);
        panel.add(myAccountButton);
        panel.add(transferButton);
        panel.add(historyButton);
        panel.add(logoutButton);

        add(panel);

        myAccountButton.addActionListener(e -> mainFrame.showMyAccountPanel());
        transferButton.addActionListener(e -> mainFrame.showTransferPanel());
        historyButton.addActionListener(e -> mainFrame.showHistoryPanel());

        logoutButton.addActionListener(e -> {
            mainFrame.setLoginUser(null);
            mainFrame.showLoginPanel();
        });
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible) {
            User user = mainFrame.getLoginUser();

            if (user != null) {
                welcomeLabel.setText(user.getName() + "님 환영합니다.");
            }
        }
    }
}