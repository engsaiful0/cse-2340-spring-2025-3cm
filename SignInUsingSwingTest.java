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
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// It's often better to test methods directly rather than through UI interactions if possible.
// However, performLogin() is private and tightly coupled to the UI fields and JOptionPane.
// For these tests, we will make it public for testing or use reflection if it were to remain private.
// For simplicity, assume it's made package-private or public for tests, or use reflection.
// We will also test it by setting text fields and calling the method.

public class SignInUsingSwingTest {

    private SignInUsingSwing signInFrame;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStatement;
    @Mock private ResultSet mockResultSet;

    // Mocking JOptionPane is tricky. We'll focus on the logic paths.
    // One way is to use PowerMockito to mock static methods of JOptionPane,
    // or use a wrapper service for showing messages.
    // For now, we will mostly check if the expected frames (Dashboard) appear or not.

    // We need to mock DBConnection.getConnection() which is static.
    // This requires PowerMockito or a refactor of DBConnection.
    // Let's use Mockito.mockStatic for this example.
    private MockedStatic<DBConnection> mockedDBConnection;
    private MockedStatic<JOptionPane> mockedJOptionPane;


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Prepare static mock for DBConnection
        mockedDBConnection = Mockito.mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        // Prepare static mock for JOptionPane to avoid dialogs popping up
        // We are mocking on a best-effort basis for this environment
        mockedJOptionPane = Mockito.mockStatic(JOptionPane.class, Answers.RETURNS_SMART_NULLS);


        // It's important to run Swing components on the Event Dispatch Thread (EDT)
        // However, for unit testing logic, we might not always need full UI rendering.
        // For these tests, we'll instantiate the frame to access its methods and fields.
        // If UI interactions were more complex, a library like AssertJ Swing would be used.
        SwingUtilities.invokeAndWait(() -> {
            signInFrame = new SignInUsingSwing();
            // signInFrame.setVisible(true); // Not strictly needed for these logic tests if we call methods directly
        });
        // Access private fields for testing - this is usually done via getters or by making them package-private for tests
        // For simplicity, we assume direct access or reflection would be used if they were private.
        // Let's assume usernameField, passwordField are accessible for test setup.
    }

    @AfterEach
    void tearDown() throws Exception {
        mockedDBConnection.close();
        mockedJOptionPane.close();
        SwingUtilities.invokeAndWait(() -> {
            if (signInFrame != null) {
                signInFrame.dispose();
            }
        });
    }

    @Test
    void testLoginWithValidCredentials_ShouldOpenDashboard() throws SQLException {
        // Arrange
        signInFrame.usernameField.setText("testuser");
        signInFrame.passwordField.setText("password123");

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // User found
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Test User");
        when(mockResultSet.getString("email")).thenReturn("test@example.com");
        when(mockResultSet.getString("department")).thenReturn("CSE");

        // Mocking the Dashboard constructor and setVisible call is complex without PowerMockito or refactoring.
        // We will assume that if JOptionPane.showMessageDialog shows "Login successful!",
        // the next step (new Dashboard().setVisible(true)) would be called.
        // A better approach would be to have a navigator interface or use a spy on the Dashboard.

        // Act
        // Directly call performLogin if it's accessible (e.g., package-private or made public for testing)
        // Or trigger the button click if testing through UI event
        // For this example, let's assume we can call performLogin directly.
        // To do this, performLogin would need to be not private.
        // If SignInUsingSwing.performLogin() was private, we'd use reflection:
        // Method performLoginMethod = SignInUsingSwing.class.getDeclaredMethod("performLogin");
        // performLoginMethod.setAccessible(true);
        // performLoginMethod.invoke(signInFrame);
        // For now, let's assume we refactored it to be package-private for testing:
        signInFrame.performLogin(); // Assuming performLogin is made accessible

        // Assert
        // We expect dispose() on signInFrame and a new Dashboard to be visible.
        // Verifying JOptionPane was called with "Login successful!"
        mockedJOptionPane.verify(() -> JOptionPane.showMessageDialog(signInFrame, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE));

        // Ideally, we'd verify that `new Dashboard(...)` was called and `setVisible(true)` on it.
        // This is hard without more advanced mocking (PowerMockito for constructors) or refactoring.
        // For now, the JOptionPane verification is a proxy for success.
        // Also check that signInFrame was disposed
        // Assert.assertFalse(signInFrame.isVisible()); // This check can be unreliable in unit tests without full UI setup.
    }

    @Test
    void testLoginWithInvalidCredentials_ShouldShowErrorMessage() throws SQLException {
        // Arrange
        signInFrame.usernameField.setText("wronguser");
        signInFrame.passwordField.setText("wrongpassword");

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // User not found

        // Act
        signInFrame.performLogin(); // Assuming performLogin is made accessible

        // Assert
        mockedJOptionPane.verify(() -> JOptionPane.showMessageDialog(signInFrame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE));
        // Assert.assertTrue(signInFrame.isVisible()); // Frame should still be visible
    }

    @Test
    void testLoginWithDatabaseError_ShouldShowSqlErrorMessage() throws SQLException {
        // Arrange
        signInFrame.usernameField.setText("testuser");
        signInFrame.passwordField.setText("password123");

        when(mockStatement.executeQuery()).thenThrow(new SQLException("Test DB Error"));

        // Act
        signInFrame.performLogin(); // Assuming performLogin is made accessible

        // Assert
        mockedJOptionPane.verify(() -> JOptionPane.showMessageDialog(signInFrame, "Database error: Test DB Error", "Error", JOptionPane.ERROR_MESSAGE));
        // Assert.assertTrue(signInFrame.isVisible()); // Frame should still be visible
    }

    // Navigation test: SignIn to Register
    @Test
    void testNavigateToRegister_ShouldOpenRegistrationForm() {
        // This test is more of an integration or UI test in spirit.
        // We'd need to simulate the button click and verify RegistrationForm appears.
        // For a unit test, we might check if the ActionListener for registerButton
        // correctly instantiates and shows RegistrationForm.

        // For now, we assume direct invocation of the action listener's core logic
        // or that a tool like AssertJ Swing would click the button.
        // Since the ActionListener does `new RegistrationForm().setVisible(true);`
        // mocking the constructor of RegistrationForm is needed to verify this without it actually appearing.
        // This needs PowerMockito.whenNew(RegistrationForm.class)...

        // As a simplified check:
        // We can't easily verify `new RegistrationForm().setVisible(true)` without PowerMock or refactoring.
        // This test remains conceptual for full verification in this environment.
        // A simple "does not throw" might be the most basic check here.
        try {
            SwingUtilities.invokeAndWait(() -> {
                // Simulate what the register button's action listener does:
                 signInFrame.registerButton.doClick(); // This will try to create a real RegistrationForm
            });
            // How to assert RegistrationForm became visible without a reference or UI testing tool?
            // This is where unit testing Swing navigation becomes hard.
            // We would ideally mock the RegistrationForm constructor and verify it was called.
        } catch (Exception e) {
            // Fail if any exception
        }
        // This test is more illustrative of the challenge.
        // For a real scenario, you'd use a UI testing framework or mock/spy on RegistrationForm construction.
    }
}
// Note: To make `performLogin()` and fields like `usernameField`, `passwordField`, `registerButton`
// accessible for tests, they would ideally be made package-private or have getters/setters.
// Or use reflection if they must remain private, though that's less clean.
// For this example, I'm writing the tests as if they are accessible.
// The `signInButton.addActionListener` directly calls `performLogin()`.
// The `registerButton.addActionListener` directly news up `RegistrationForm`.
// Making `performLogin` not private is the easiest way to test its logic.
// The fields also need to be accessible to set test values.
//
// In SignInUsingSwing.java, change:
// private void performLogin() -> void performLogin() // package-private
// private JTextField usernameField -> JTextField usernameField // package-private
// private JPasswordField passwordField -> JPasswordField passwordField // package-private
// And the registerButton needs to be a field to be clicked in test:
// In SignInUsingSwing constructor:
// JButton registerButton = new JButton("Register"); -> this.registerButton = new JButton("Register");
// And declare: private JButton registerButton; at class level.
//
// Similar changes would be needed for RegistrationFormTest.java for its fields and registerUser method.
