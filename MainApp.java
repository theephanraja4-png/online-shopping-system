import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainApp extends JFrame {

    // ================= DATABASE =================
    static final String URL =
            "jdbc:mysql://localhost:3306/online_shopping?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "nathadaleo";

    // ================= PRODUCT FIELDS =================
    JTextField pid, pname, pcategory, pbrand, pmodel, pprice, pdiscount;

    JTable productTable, cartTable;
    DefaultTableModel productModel, cartModel;

    JTextField cartSearch;
    JTextField couponField;

    JLabel totalLabel;
    JLabel savedLabel;

    JButton checkoutBtn;

    double discountAmount = 0;

    public MainApp() {

        setTitle("Online Shopping Product Manager + Cart + Coupon + Payment");
        setSize(1050,650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Products", createProductPanel());
        tabs.add("Cart", createCartPanel());

        add(tabs);

        setVisible(true);
    }

    // ================= DB CONNECT =================
    Connection connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // =========================================================
    // ================= PRODUCT PANEL =========================
    // =========================================================
    JPanel createProductPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(7,2));

        pid = new JTextField();
        pname = new JTextField();
        pcategory = new JTextField();
        pbrand = new JTextField();
        pmodel = new JTextField();
        pprice = new JTextField();
        pdiscount = new JTextField();

        top.add(new JLabel("Product ID"));
        top.add(pid);

        top.add(new JLabel("Product Name"));
        top.add(pname);

        top.add(new JLabel("Category"));
        top.add(pcategory);

        top.add(new JLabel("Brand"));
        top.add(pbrand);

        top.add(new JLabel("Model Name"));
        top.add(pmodel);

        top.add(new JLabel("Price"));
        top.add(pprice);

        top.add(new JLabel("Discount %"));
        top.add(pdiscount);

        panel.add(top, BorderLayout.NORTH);

        productModel = new DefaultTableModel();
        productModel.setColumnIdentifiers(new String[]{
                "ID","Product","Category","Brand","Model","Price","Discount"
        });

        productTable = new JTable(productModel);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel();

        JButton insert = new JButton("Insert");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton remove1 = new JButton("Remove 1");
        JButton clear = new JButton("Clear");
        JButton addToCart = new JButton("Add To Cart");

        bottom.add(insert);
        bottom.add(update);
        bottom.add(delete);
        bottom.add(remove1);
        bottom.add(clear);
        bottom.add(addToCart);

        panel.add(bottom, BorderLayout.SOUTH);

        insert.addActionListener(e -> insertProduct());
        update.addActionListener(e -> updateProduct());
        delete.addActionListener(e -> deleteProduct());
        remove1.addActionListener(e -> removeOneDiscount());
        clear.addActionListener(e -> clearFields());
        addToCart.addActionListener(e -> addToCart());

        productTable.getSelectionModel().addListSelectionListener(e -> fillFields());

        loadProducts();

        return panel;
    }

    // =========================================================
    // ================= CART PANEL ============================
    // =========================================================
    JPanel createCartPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        JPanel top = new JPanel();

        cartSearch = new JTextField(15);
        JButton searchBtn = new JButton("Search");

        couponField = new JTextField(10);
        JButton applyCouponBtn = new JButton("Apply Coupon");

        top.add(new JLabel("Search Cart: "));
        top.add(cartSearch);
        top.add(searchBtn);

        top.add(new JLabel("Coupon: "));
        top.add(couponField);
        top.add(applyCouponBtn);

        panel.add(top, BorderLayout.NORTH);

        cartModel = new DefaultTableModel();
        cartModel.setColumnIdentifiers(new String[]{
                "ID","Product","Price","Qty","Total"
        });

        cartTable = new JTable(cartModel);
        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel();

        JButton removeOne = new JButton("Remove 1");
        JButton removeAll = new JButton("Remove All");
        checkoutBtn = new JButton("Checkout");

        totalLabel = new JLabel("Total: ₹0");
        savedLabel = new JLabel("You Saved: ₹0");

        bottom.add(removeOne);
        bottom.add(removeAll);
        bottom.add(checkoutBtn);
        bottom.add(totalLabel);
        bottom.add(savedLabel);

        panel.add(bottom, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> searchCart());

        applyCouponBtn.addActionListener(e -> applyCoupon());

        removeOne.addActionListener(e -> {
            removeOneFromCart();
            updateTotal();
        });

        removeAll.addActionListener(e -> {
            removeAllFromCart();
            updateTotal();
        });

        checkoutBtn.addActionListener(e -> openPaymentPage());

        return panel;
    }

    // =========================================================
    // ================= LOAD PRODUCTS =========================
    // =========================================================
    void loadProducts() {
        try {
            productModel.setRowCount(0);

            Connection con = connect();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM products");

            while(rs.next()) {
                productModel.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getString("brand"),
                        rs.getString("model_name"),
                        rs.getDouble("price"),
                        rs.getInt("discount_percent")
                });
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // =========================================================
    // ================= INSERT PRODUCT ========================
    // =========================================================
    void insertProduct() {
        try {
            Connection con = connect();

            String sql = "INSERT INTO products(product_name, category, brand, model_name, price, discount_percent) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, pname.getText());
            ps.setString(2, pcategory.getText());
            ps.setString(3, pbrand.getText());
            ps.setString(4, pmodel.getText());
            ps.setDouble(5, Double.parseDouble(pprice.getText()));
            ps.setInt(6, Integer.parseInt(pdiscount.getText()));

            ps.executeUpdate();

            loadProducts();
            clearFields();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // =========================================================
    // ================= UPDATE PRODUCT ========================
    // =========================================================
    void updateProduct() {
        try {
            Connection con = connect();

            String sql = "UPDATE products SET product_name=?, category=?, brand=?, model_name=?, price=?, discount_percent=? WHERE product_id=?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, pname.getText());
            ps.setString(2, pcategory.getText());
            ps.setString(3, pbrand.getText());
            ps.setString(4, pmodel.getText());
            ps.setDouble(5, Double.parseDouble(pprice.getText()));
            ps.setInt(6, Integer.parseInt(pdiscount.getText()));
            ps.setInt(7, Integer.parseInt(pid.getText()));

            ps.executeUpdate();

            loadProducts();
            clearFields();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // =========================================================
    // ================= DELETE PRODUCT ========================
    // =========================================================
    void deleteProduct() {
        try {
            Connection con = connect();

            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM products WHERE product_id=?");

            ps.setInt(1, Integer.parseInt(pid.getText()));
            ps.executeUpdate();

            loadProducts();
            clearFields();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // =========================================================
    // ================= REMOVE 1 DISCOUNT =====================
    // =========================================================
    void removeOneDiscount() {
        try {
            Connection con = connect();

            int id = Integer.parseInt(pid.getText());

            PreparedStatement ps1 = con.prepareStatement(
                    "SELECT discount_percent FROM products WHERE product_id=?");

            ps1.setInt(1, id);
            ResultSet rs = ps1.executeQuery();

            if(rs.next()) {
                int discount = rs.getInt("discount_percent");

                if(discount > 0) {
                    PreparedStatement ps2 = con.prepareStatement(
                            "UPDATE products SET discount_percent = discount_percent - 1 WHERE product_id=?");

                    ps2.setInt(1, id);
                    ps2.executeUpdate();
                }

                loadProducts();
                clearFields();
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // =========================================================
    // ================= ADD TO CART ===========================
    // =========================================================
    void addToCart() {

        int row = productTable.getSelectedRow();

        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Select product first");
            return;
        }

        int id = Integer.parseInt(productModel.getValueAt(row,0).toString());
        String name = productModel.getValueAt(row,1).toString();
        double price = Double.parseDouble(productModel.getValueAt(row,5).toString());

        boolean found = false;

        for(int i=0;i<cartModel.getRowCount();i++) {

            if(Integer.parseInt(cartModel.getValueAt(i,0).toString()) == id) {

                int qty = Integer.parseInt(cartModel.getValueAt(i,3).toString()) + 1;

                cartModel.setValueAt(qty, i, 3);
                cartModel.setValueAt(qty * price, i, 4);

                found = true;
                break;
            }
        }

        if(!found) {
            cartModel.addRow(new Object[]{id,name,price,1,price});
        }

        updateTotal();
    }

    // =========================================================
    // ================= APPLY COUPON ==========================
    // =========================================================
    void applyCoupon() {

        String code = couponField.getText().trim().toUpperCase();

        double total = calculateRawTotal();

        if(code.equals("SAVE10")) {
            discountAmount = total * 0.10;
        }
        else if(code.equals("SAVE20")) {
            discountAmount = total * 0.20;
        }
        else if(code.equals("FLAT500")) {
            discountAmount = 500;
        }
        else {
            discountAmount = 0;
            JOptionPane.showMessageDialog(this, "Invalid Coupon Code!");
        }

        updateTotal();
    }

    // =========================================================
    // ================= TOTAL CALCULATION =====================
    // =========================================================
    double calculateRawTotal() {

        double total = 0;

        for(int i=0;i<cartModel.getRowCount();i++) {
            total += Double.parseDouble(
                    cartModel.getValueAt(i,4).toString()
            );
        }

        return total;
    }

    void updateTotal() {

        double total = calculateRawTotal();
        double finalAmount = total - discountAmount;

        if(finalAmount < 0) finalAmount = 0;

        totalLabel.setText("Total: ₹" + finalAmount);
        savedLabel.setText("You Saved: ₹" + discountAmount);
    }

    // =========================================================
    // ================= REMOVE ONE FROM CART ==================
    // =========================================================
    void removeOneFromCart() {

        int row = cartTable.getSelectedRow();

        if(row == -1) return;

        int qty = Integer.parseInt(cartModel.getValueAt(row,3).toString());
        double price = Double.parseDouble(cartModel.getValueAt(row,2).toString());

        if(qty > 1) {
            qty--;
            cartModel.setValueAt(qty, row, 3);
            cartModel.setValueAt(qty * price, row, 4);
        } else {
            cartModel.removeRow(row);
        }
    }

    // =========================================================
    // ================= REMOVE ALL FROM CART ==================
    // =========================================================
    void removeAllFromCart() {

        int row = cartTable.getSelectedRow();

        if(row != -1) {
            cartModel.removeRow(row);
        }
    }

    // =========================================================
    // ================= SEARCH CART ===========================
    // =========================================================
    void searchCart() {

        String keyword = cartSearch.getText().toLowerCase();

        for(int i=0;i<cartTable.getRowCount();i++) {

            String name = cartModel.getValueAt(i,1).toString().toLowerCase();

            if(name.contains(keyword)) {
                cartTable.setRowSelectionInterval(i, i);
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Not Found");
    }

    // =========================================================
    // ================= PAYMENT PAGE ==========================
    // =========================================================
    void openPaymentPage() {

        if(cartModel.getRowCount()==0) {
            JOptionPane.showMessageDialog(this,"Cart is Empty!");
            return;
        }

        JFrame payFrame = new JFrame("Payment Page");
        payFrame.setSize(400,300);
        payFrame.setLayout(new GridLayout(6,1));

        JLabel title = new JLabel("Choose Payment Method", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JRadioButton cod = new JRadioButton("Cash On Delivery");
        JRadioButton card = new JRadioButton("Card Payment");
        JRadioButton upi = new JRadioButton("UPI Payment");

        ButtonGroup group = new ButtonGroup();
        group.add(cod);
        group.add(card);
        group.add(upi);

        JLabel totalPay = new JLabel(totalLabel.getText(), JLabel.CENTER);

        JButton payNow = new JButton("Pay Now");

        payNow.addActionListener(e -> {

            String method = "";

            if(cod.isSelected()) method = "COD";
            else if(card.isSelected()) method = "Card";
            else if(upi.isSelected()) method = "UPI";
            else {
                JOptionPane.showMessageDialog(payFrame,"Select Payment Method");
                return;
            }

            JOptionPane.showMessageDialog(payFrame,
                    "Payment Successful via " + method);

            cartModel.setRowCount(0);
            discountAmount = 0;
            updateTotal();

            payFrame.dispose();
        });

        payFrame.add(title);
        payFrame.add(cod);
        payFrame.add(card);
        payFrame.add(upi);
        payFrame.add(totalPay);
        payFrame.add(payNow);

        payFrame.setVisible(true);
    }

    // =========================================================
    // ================= CLEAR FIELDS ==========================
    // =========================================================
    void clearFields() {
        pid.setText("");
        pname.setText("");
        pcategory.setText("");
        pbrand.setText("");
        pmodel.setText("");
        pprice.setText("");
        pdiscount.setText("");
    }

    // =========================================================
    // ================= FILL FORM =============================
    // =========================================================
    void fillFields() {

        int row = productTable.getSelectedRow();

        if(row != -1) {
            pid.setText(productModel.getValueAt(row,0).toString());
            pname.setText(productModel.getValueAt(row,1).toString());
            pcategory.setText(productModel.getValueAt(row,2).toString());
            pbrand.setText(productModel.getValueAt(row,3).toString());
            pmodel.setText(productModel.getValueAt(row,4).toString());
            pprice.setText(productModel.getValueAt(row,5).toString());
            pdiscount.setText(productModel.getValueAt(row,6).toString());
        }
    }

    // =========================================================
    // ================= MAIN =================================
    // =========================================================
    public static void main(String[] args) {
        new MainApp();
    }
}