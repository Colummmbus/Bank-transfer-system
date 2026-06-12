package gui;

import domain.User;
import service.LoginService;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private LoginService loginService = new LoginService();

    private JTextField loginIdField;
    private JPasswordField passwordField;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("아이디"));
        loginIdField = new JTextField(15);
        panel.add(loginIdField);

        panel.add(new JLabel("비밀번호"));
        passwordField = new JPasswordField(15);
        panel.add(passwordField);

        JButton loginButton = new JButton("로그인");
        JButton signupButton = new JButton("회원가입");

        panel.add(loginButton);
        panel.add(signupButton);

        add(panel);

        loginButton.addActionListener(e -> login());
        signupButton.addActionListener(e -> mainFrame.showSignupPanel());
    }

    private void login() {
        String loginId = loginIdField.getText();
        String password = new String(passwordField.getPassword());

        try {
            User user = loginService.login(loginId, password);
            mainFrame.setLoginUser(user);
            JOptionPane.showMessageDialog(this, user.getName() + "님 로그인 성공");
            mainFrame.showHomePanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}