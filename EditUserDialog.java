import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditUserDialog extends JDialog {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField departmentField;
    private JButton saveButton;
    private JButton cancelButton;

    private UserListView userListViewInstance;
    private int userId;
    private int rowIndex;

    public EditUserDialog(JFrame parent, UserListView userListView, int userId, String currentName, String currentEmail, String currentDepartment, int rowIndex) {
        super(parent, "Edit User", true); // true for modal
        this.userListViewInstance = userListView;
        this.userId = userId;
        this.rowIndex = rowIndex;

        setLayout(new BorderLayout());
        setSize(400, 200);
        setLocationRelativeTo(parent);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5)); // rows, cols, hgap, vgap
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField(currentName);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(currentEmail);
        formPanel.add(emailField);

        formPanel.add(new JLabel("Department:"));
        departmentField = new JTextField(currentDepartment);
        formPanel.add(departmentField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveChanges() {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newDepartment = departmentField.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty() || newDepartment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call method in UserListView to update data
        userListViewInstance.updateUser(userId, newName, newEmail, newDepartment, rowIndex);
        dispose(); // Close the dialog
    }
}
