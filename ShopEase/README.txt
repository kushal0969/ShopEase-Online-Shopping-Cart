
# 🛒 ShopEase — Online Shopping Cart System

A **console-based Java application** built using **Object-Oriented Programming (OOP)** principles that simulates a real-world online shopping experience.

> 🚀 Designed as an academic project with clean architecture, modular design, and strong OOP implementation.

---

## ✨ Features

* 🔐 User authentication (Login & Register)
* 🛍️ Product catalog (Electronics, Clothing, Food)
* 🔎 Search and filter products
* 🛒 Shopping cart (add, remove, update items)
* 📦 Checkout system with order summary
* 🎟️ Coupon/discount system (percentage & flat)
* 💳 Multiple payment methods
* 📜 Order history per user
* ⚠️ Robust exception handling

---

## ⚡ Quick Start

### 📌 Requirements

* Java JDK 17 or later
  👉 [https://adoptium.net](https://adoptium.net)

---

### ▶️ Run the Project

#### 🪟 Windows

```bash
compile_and_run_windows.bat
```

---

### 🛠️ Manual Compilation

```bash
mkdir out
javac -d out src/cart/exceptions/*.java src/cart/users/*.java \
      src/cart/products/*.java src/cart/cart/*.java \
      src/cart/checkout/*.java src/cart/utils/*.java src/Main.java
cd out
java Main
```

---

### 💻 IDE Setup (IntelliJ / VS Code)

1. Open `ShopEase/` as a project
2. Mark `src/` as **Sources Root**
3. Run `Main.java`

---

## 🔑 Demo Credentials

```
Username: alice   Password: pass123
Username: bob     Password: bob456
Or create your own account using register page.
```

👉 Or create your own account from the menu

---

## 🎟️ Demo Coupon Codes

| Code    | Discount                  |
| ------- | ------------------------- |
| SAVE10  | 10% off orders above $50  |
| SAVE20  | 20% off orders above $200 |
| FLAT30  | $30 off orders above $100 |
| WELCOME | $5 off (no minimum)       |

---

## 🏗️ Project Structure

```
ShopEase/
├── src/
│   ├── Main.java
│   └── cart/
│       ├── products/     # Product hierarchy
│       ├── cart/         # Cart logic
│       ├── checkout/     # Order & payment system
│       ├── users/        # User management
│       ├── exceptions/   # Custom exceptions
│       └── utils/        # Helper utilities
├── scripts/
└── README.md
```

---

## 🧠 OOP Concepts Implemented

### 🔹 Encapsulation

* Private fields with controlled access (getters/setters)
* Immutable structures where needed (e.g., Order)

### 🔹 Inheritance

* `Product` → Electronics, Clothing, Food
* Custom exception hierarchy

### 🔹 Polymorphism

* `List<Product>` holding multiple subclasses
* Runtime method dispatch (`getDetails()`, `getCategory()`)

### 🔹 Abstraction

* Abstract class `Product` defines common behavior

### 🔹 Method Overloading

* Multiple constructors and methods with different parameters

### 🔹 Method Overriding

* Subclasses override `toString()`, `getDetails()`, etc.

### 🔹 Exception Handling

* Custom exceptions:

  * OutOfStockException
  * ProductNotFoundException
  * InvalidQuantityException
  * EmptyCartException
  * InvalidCouponException

---

## 🛠️ Technologies Used

* ☕ Java (JDK 17+)
* 🧱 OOP Design Principles
* 🖥️ Console-based UI

---

## 📸 (Optional — Add Screenshots Here)

> You can improve this repo by adding screenshots of your application output or UI.

---

## 🚀 Future Improvements

* 🌐 Convert to web application (Spring Boot / React)
* 🗄️ Database integration (MySQL / MongoDB)
* 🔐 Advanced authentication (JWT)
* 🎨 GUI enhancement (JavaFX / Swing improvements)

---

## 👨‍💻 Author

**Kushal Khadka**
📧 [kushalkhadka2025@gmail.com](mailto:kushalkhadka2025@gmail.com)

---

## ⭐ If you like this project

Give it a star on GitHub ⭐ and feel free to fork it!

---
