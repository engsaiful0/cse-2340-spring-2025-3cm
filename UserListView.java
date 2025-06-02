import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserListView extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserListView() {
        setTitle("Registered Users");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        userTable = new JTable(tableModel);

        // Define table columns
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Email");
        tableModel.addColumn("Department");

        fetchUserData();

        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void fetchUserData() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, name, email, department FROM users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String department = rs.getString("department");

                tableModel.addRow(new Object[]{id, name, email, department});
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading user data.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserListView::new);
    }
}
