import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignInUsingSwing extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signInButton;

    public SignInUsingSwing() {
        setTitle("Sign In");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Sign In button
        signInButton = new JButton("Sign In");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(signInButton, gbc);

        // Action listener
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.equals("admin") && password.equals("1234")) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new Dashboard().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Dashboard class with two buttons
    class Dashboard extends JFrame {
        public Dashboard() {
            setTitle("Dashboard");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            JLabel welcomeLabel = new JLabel("Welcome to the Dashboard!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
            add(welcomeLabel, BorderLayout.NORTH);

            // Panel for buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());

            JButton signInBtn = new JButton("Sign In");
            JButton signUpBtn = new JButton("Sign Up");

            // Add buttons to panel
            buttonPanel.add(signInBtn);
            buttonPanel.add(signUpBtn);

            // Add panel to frame
            add(buttonPanel, BorderLayout.CENTER);

            // Dummy actions
            signInBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Sign In clicked"));
            signUpBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Sign Up clicked"));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SignInUsingSwing().setVisible(true);
        });
    }
}
