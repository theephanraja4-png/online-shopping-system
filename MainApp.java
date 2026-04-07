import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainApp extends JFrame {

    // 🔌 DB CONFIG
    static final String URL = "jdbc:mysql://localhost:3306/online_shopping?useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "nathadaleo";

    JTextField id, name, product, category, price, quantity, date;
    JTable table;
    DefaultTableModel model;

    public MainApp() {
        setTitle("Online Shopping System");
        setSize(800,500);
        setLayout(new BorderLayout());

        // 🧾 INPUT PANEL
        JPanel top = new JPanel(new GridLayout(7,2));

        id = new JTextField();
        name = new JTextField();
        product = new JTextField();
        category = new JTextField();
        price = new JTextField();
        quantity = new JTextField();
        date = new JTextField();

        top.add(new JLabel("Order ID")); top.add(id);
        top.add(new JLabel("Customer Name")); top.add(name);
        top.add(new JLabel("Product")); top.add(product);
        top.add(new JLabel("Category")); top.add(category);
        top.add(new JLabel("Price")); top.add(price);
        top.add(new JLabel("Quantity")); top.add(quantity);
        top.add(new JLabel("Date (YYYY-MM-DD)")); top.add(date);

        add(top, BorderLayout.NORTH);

        // 📊 TABLE
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "ID","Name","Product","Category","Price","Qty","Date"
        });

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 🔘 BUTTONS
        JPanel bottom = new JPanel();

        JButton insert = new JButton("Insert");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");

        bottom.add(insert);
        bottom.add(update);
        bottom.add(delete);
        bottom.add(clear);

        add(bottom, BorderLayout.SOUTH);

        // 🎯 BUTTON ACTIONS
        insert.addActionListener(e -> insertData());
        update.addActionListener(e -> updateData());
        delete.addActionListener(e -> deleteData());
        clear.addActionListener(e -> clearFields());

        // 🖱️ CLICK TABLE → AUTO FILL
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                id.setText(model.getValueAt(row,0).toString());
                name.setText(model.getValueAt(row,1).toString());
                product.setText(model.getValueAt(row,2).toString());
                category.setText(model.getValueAt(row,3).toString());
                price.setText(model.getValueAt(row,4).toString());
                quantity.setText(model.getValueAt(row,5).toString());
                date.setText(model.getValueAt(row,6).toString());
            }
        });

        // 🔥 LOAD EXISTING DATA
        loadData();

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // 🔌 CONNECT DB
    Connection connect() throws Exception {
        // Modern JDBC — no need Class.forName, but safe to keep:
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(URL, USER, PASS);
        System.out.println("✅ Connected to DB");
        return con;
    }

    // 📥 LOAD DATA INTO TABLE
    void loadData() {
        try {
            model.setRowCount(0);

            Connection con = connect();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM online_shopping");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("cust_name"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getDate("order_date")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ➕ INSERT
    void insertData() {
        try {
            Connection con = connect();

            String sql = "INSERT INTO online_shopping (cust_name, product_name, category, price, quantity, order_date) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name.getText());
            ps.setString(2, product.getText());
            ps.setString(3, category.getText());
            ps.setDouble(4, Double.parseDouble(price.getText()));
            ps.setInt(5, Integer.parseInt(quantity.getText()));
            ps.setString(6, date.getText());

            ps.executeUpdate();
            loadData();

            JOptionPane.showMessageDialog(this, "Inserted!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔄 UPDATE
    void updateData() {
        try {
            Connection con = connect();

            String sql = "UPDATE online_shopping SET cust_name=?, product_name=?, category=?, price=?, quantity=?, order_date=? WHERE order_id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name.getText());
            ps.setString(2, product.getText());
            ps.setString(3, category.getText());
            ps.setDouble(4, Double.parseDouble(price.getText()));
            ps.setInt(5, Integer.parseInt(quantity.getText()));
            ps.setString(6, date.getText());
            ps.setInt(7, Integer.parseInt(id.getText()));

            ps.executeUpdate();
            loadData();

            JOptionPane.showMessageDialog(this, "Updated!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ❌ DELETE
    void deleteData() {
        try {
            Connection con = connect();

            String sql = "DELETE FROM online_shopping WHERE order_id=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(id.getText()));
            ps.executeUpdate();

            loadData();

            JOptionPane.showMessageDialog(this, "Deleted!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🧹 CLEAR
    void clearFields() {
        id.setText("");
        name.setText("");
        product.setText("");
        category.setText("");
        price.setText("");
        quantity.setText("");
        date.setText("");
    }

    public static void main(String[] args) {
        new MainApp();
    }
}