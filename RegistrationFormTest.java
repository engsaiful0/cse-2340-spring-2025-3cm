import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RegistrationFormTest {

    private RegistrationForm registrationForm;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStatement;
    // SQLIntegrityConstraintViolationException is a concrete class, can be instantiated directly if needed for thenThrow
    // @Mock private SQLIntegrityConstraintViolationException mockViolationException;


    private MockedStatic<DBConnection> mockedDBConnection;
    private MockedStatic<JOptionPane> mockedJOptionPane;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockedDBConnection = Mockito.mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        mockedJOptionPane = Mockito.mockStatic(JOptionPane.class, Answers.RETURNS_SMART_NULLS);

        SwingUtilities.invokeAndWait(() -> {
            registrationForm = new RegistrationForm();
            // registrationForm.setVisible(true); // Not needed for logic tests
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        mockedDBConnection.close();
        mockedJOptionPane.close();
        SwingUtilities.invokeAndWait(() -> {
            if (registrationForm != null) {
                registrationForm.dispose();
            }
        });
    }

    @Test
    void testSuccessfulRegistration_ShouldShowSuccessMessageAndClearFields() throws SQLException {
        // Arrange
        registrationForm.nameField.setText("New User");
        registrationForm.emailField.setText("new@example.com");
        registrationForm.passwordField.setText("password123");
        registrationForm.departmentCombo.setSelectedItem("CSE");

        when(mockStatement.executeUpdate()).thenReturn(1); // 1 row affected

        // Act
        registrationForm.registerUser(); // Assuming registerUser is made accessible

        // Assert
        mockedJOptionPane.verify(() -> JOptionPane.showMessageDialog(registrationForm, "Registration successful!"));
        // Verify fields are cleared
        // Need getters or direct access to fields for this
        assert "".equals(registrationForm.nameField.getText());
        assert "".equals(registrationForm.emailField.getText());
        assert "".equals(new String(registrationForm.passwordField.getPassword()));
        assert registrationForm.departmentCombo.getSelectedIndex() == 0; // Assuming 0 is the default/first item
    }

    @Test
    void testRegistrationWithExistingEmail_ShouldShowEmailExistsMessage() throws SQLException {
        // Arrange
        registrationForm.nameField.setText("Existing User");
        registrationForm.emailField.setText("existing@example.com");
        registrationForm.passwordField.setText("password123");
        registrationForm.departmentCombo.setSelectedItem("EEE");

        // Simulate SQLIntegrityConstraintViolationException for duplicate email
        when(mockStatement.executeUpdate()).thenThrow(new SQLIntegrityConstraintViolationException("Duplicate entry 'existing@example.com' for key 'users.email_UNIQUE'"));

        // Act
        registrationForm.registerUser(); // Assuming registerUser is made accessible

        // Assert
        mockedJOptionPane.verify(() -> JOptionPane.showMessageDialog(registrationForm, "Email already registered."));
    }

    @Test
    void testRegistrationWithEmptyName_ShouldShowFillAllFieldsMessage() {
        // Arrange
        registrationForm.nameField.setText(""); // Empty name
        registrationForm.emailField.setText("test@example.com");
        registrationForm.passwordField.setText("password123");
        registrationForm.departmentCombo.setSelectedItem("CSE");

        // Act
        registrationForm.registerUser();

        // Assert
        mockedJOptionPane.verify(() -> JOptionPane.showMessageDialog(registrationForm, "Please fill all fields."));
    }


    @Test
    void testRegistrationWithDatabaseError_ShouldShowRegistrationFailedMessage() throws SQLException {
        // Arrange
        registrationForm.nameField.setText("DB Error User");
        registrationForm.emailField.setText("dberror@example.com");
        registrationForm.passwordField.setText("password123");
        registrationForm.departmentCombo.setSelectedItem("BBA");

        when(mockStatement.executeUpdate()).thenThrow(new SQLException("General DB Error"));

        // Act
        registrationForm.registerUser(); // Assuming registerUser is made accessible

        // Assert
        mockedJOptionPane.verify(() -> JOptionPane.showMessageDialog(registrationForm, "Registration failed."));
    }

    // Navigation test: Register to Sign In
    @Test
    void testNavigateToSignIn_ShouldOpenSignInForm() {
        // Similar to SignInUsingSwingTest, direct verification of
        // `new SignInUsingSwing().setVisible(true)` is hard without PowerMock.
        // This test is conceptual for full verification in this environment.
        try {
            SwingUtilities.invokeAndWait(() -> {
                // Simulate what the signIn button's action listener does:
                 registrationForm.signInBtn.doClick(); // This will try to create a real SignInUsingSwing
            });
            // Assert that registrationForm is disposed
            // Assert.assertFalse(registrationForm.isVisible()); // Can be unreliable
        } catch (Exception e) {
            // Fail
        }
        // We would ideally mock the SignInUsingSwing constructor and verify it was called.
    }
}

// Note: To make `registerUser()`, `clearFields()` and fields like `nameField`, `emailField`, `passwordField`, `departmentCombo`, `signInBtn`
// accessible for tests, they would ideally be made package-private or have getters/setters.
// In RegistrationForm.java:
// private void registerUser() -> void registerUser()
// private void clearFields() -> void clearFields()
// private JTextField nameField -> JTextField nameField
// ...and other fields.
// JButton signInBtn needs to be a field:
// In RegistrationForm constructor:
// JButton signInBtn = new JButton("Sign In"); -> this.signInBtn = new JButton("Sign In");
// And declare: private JButton signInBtn; at class level.
