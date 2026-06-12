package gui;

import domain.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private User loginUser;

    public MainFrame() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(new LoginPanel(this), "LOGIN");
        cardPanel.add(new SignupPanel(this), "SIGNUP");
        cardPanel.add(new HomePanel(this), "HOME");
        cardPanel.add(new MyAccountPanel(this), "MY ACCOUNT");
        cardPanel.add(new TransferPanel(this), "TRANSFER");
        cardPanel.add(new HistoryPanel(this), "HISTORY");

        add(cardPanel);
        showLoginPanel();
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

    public User getLoginUser() {
        return loginUser;
    }

    public void showLoginPanel() {
        cardLayout.show(cardPanel, "LOGIN");
    }

    public void showSignupPanel() {
        cardLayout.show(cardPanel, "SIGNUP");
    }

    public void showHomePanel() {
        cardLayout.show(cardPanel, "HOME");
    }

    public void showMyAccountPanel() {
        cardLayout.show(cardPanel, "MY ACCOUNT");
    }

    public void showTransferPanel() {
        cardLayout.show(cardPanel, "TRANSFER");
    }

    public void showHistoryPanel() {
        cardLayout.show(cardPanel, "HISTORY");
    }
}