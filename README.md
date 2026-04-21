# 🛒 Online Shopping Management System (Java + MySQL)

## 📌 Overview

This project is a desktop-based **Online Shopping Management System** developed using **Java Swing (frontend)** and **MySQL (backend)**.
It simulates real-world e-commerce features like product management, cart operations, coupon discounts, and payment processing.

---

## 🎯 Features

### 🧾 Product Management (CRUD)

* Add new products
* Update product details
* Delete products
* View all products

### 🛍 Cart System

* Add products to cart
* Increase / decrease quantity
* Remove items from cart
* Search items in cart

### 🎟 Coupon System

* Apply discount codes
* Supported coupons:

  * `SAVE10` → 10% off
  * `SAVE20` → 20% off
  * `FLAT500` → ₹500 off

### 💳 Payment System

* Cash on Delivery (COD)
* Card Payment
* UPI Payment
* Auto total calculation
* Shows "You Saved" amount

---

## 🛠 Technologies Used

* **Frontend:** Java Swing
* **Backend:** MySQL
* **Connectivity:** JDBC (mysql-connector)
* **IDE:** VS Code / IntelliJ

---

## 🗄 Database Structure

### Database: `online_shopping`

#### Table: `products`

| Column           | Type                     |
| ---------------- | ------------------------ |
| product_id       | INT (PK, AUTO_INCREMENT) |
| product_name     | VARCHAR                  |
| category         | VARCHAR                  |
| brand            | VARCHAR                  |
| model_name       | VARCHAR                  |
| price            | DOUBLE                   |
| discount_percent | INT                      |

---

## ▶ How to Run

### 1. Compile

```
javac -cp ".;mysql-connector-j-9.6.0.jar" MainApp.java
```

### 2. Run

```
java -cp ".;mysql-connector-j-9.6.0.jar" MainApp
```

---

## ⚠️ Important Notes

* Make sure MySQL server is running
* Update DB credentials inside code:

```
USER = "root"
PASS = "your_password"
```

---

## 📷 Output

* Product Management UI
* Cart with quantity control
* Coupon applied screen
* Payment page

---

## 📈 TRL Level

**TRL 4 – Technology validated in lab**

---

## 🌍 SDG Goal

**Goal 9 – Industry, Innovation and Infrastructure**

---

## 👨‍💻 Author

**Theepu**

---

## 🚀 Future Improvements

* Login / Signup system
* Online payment gateway integration
* Order history tracking
* Image support for products

---

## 💡 Project Summary

This project demonstrates real-time **database operations, GUI design, and business logic implementation** similar to platforms like Flipkart.

---
