package gui;

import service.UserService;

import javax.swing.*;
import java.awt.*;

public class SignupPanel extends JPanel {
    private MainFrame mainFrame;
    private UserService userService = new UserService();

    private JTextField nameField;
    private JTextField loginIdField;
    private JPasswordField passwordField;

    public SignupPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("이름"));
        nameField = new JTextField(15);
        panel.add(nameField);

        panel.add(new JLabel("아이디"));
        loginIdField = new JTextField(15);
        panel.add(loginIdField);

        panel.add(new JLabel("비밀번호"));
        passwordField = new JPasswordField(15);
        panel.add(passwordField);

        JButton signupButton = new JButton("회원가입");
        JButton backButton = new JButton("뒤로가기");

        panel.add(signupButton);
        panel.add(backButton);

        add(panel);

        signupButton.addActionListener(e -> signup());
        backButton.addActionListener(e -> mainFrame.showLoginPanel());
    }

    private void signup() {
        String name = nameField.getText();
        String loginId = loginIdField.getText();
        String password = new String(passwordField.getPassword());

        try {
            userService.signup(loginId, password, name);

            JOptionPane.showMessageDialog(this, "회원가입 성공");
            clearFields();
            mainFrame.showLoginPanel();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText("");
        loginIdField.setText("");
        passwordField.setText("");
    }
}