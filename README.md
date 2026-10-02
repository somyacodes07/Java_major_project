# Wholesale Order Processing System

> **Case Study No. 119**  
> **Course:** Java Programming | **Semester:** III | **Program:** B.Tech CSE (2025–29)  
> **Institution:** ITM Business School & School of Future Tech, ITM Skills University  

---

## 📌 Problem Statement

A wholesale distributor receives bulk product orders from retailers. The system must manage **retailers**, **products**, **bulk quantities**, **tiered bulk pricing**, **tax invoices**, and **delivery status tracking**, backed by proper architectural justification, robust validation, and an intuitive graphical user interface (GUI).

---

## 🚀 Key Features

- 🔐 **Secure Authentication & Role Display:** Login screen for warehouse administrators and sales managers.
- 👥 **Retailer Record & Credit Management:** Register, update, and search retailer accounts with real-time tracking of credit limits, outstanding balances, and available credit.
- 📦 **Product Catalog & Inventory Engine:** Track wholesale inventory stock, categories, base wholesale unit prices, and Minimum Order Quantity (MOQ).
- 🏷️ **Tiered Bulk Volume Pricing:** Automatic tier-based discount calculation applied based on order volume:
  - **1 – 49 units:** Base Wholesale Price (0% discount)
  - **50 – 199 units:** 10% Bulk Discount
  - **200 – 499 units:** 18% Bulk Discount
  - **500+ units:** 25% Bulk Discount
- 🛒 **Interactive Order Cart & Live Price Preview:** Live calculation of unit rates, tiered savings, subtotal, 5% wholesale GST, and net payable before checkout.
- 🚚 **Order & Delivery Lifecycle Tracking:** Manage order progress through fulfillment milestones:
  `Placed` ➔ `In Processing` ➔ `Dispatched` ➔ `Delivered` (or `Cancelled` with automatic inventory & credit rollback).
- 🧾 **Commercial Tax Invoices:** Auto-generates itemized, printable wholesale tax invoices with full audit breakdowns.
- 📊 **Executive Analytics & Business Reports:** KPI cards (Revenue, Volume Savings Granted, Active Retailers), order status distribution, credit exposure, and low-stock warehouse warnings.

---

## 🧩 Java Concepts & Justification Matrix

| Java Concept | Project Implementation | Purpose & Justification |
| :--- | :--- | :--- |
| **Object-Oriented Programming (OOP)** | `Retailer`, `Product`, `Order`, `OrderItem`, `Invoice`, `User` | Encapsulates business data using `private` fields and controlled getter/setter access methods. |
| **`ArrayList`** | `List<OrderItem>`, `List<Retailer>`, `List<Product>`, `List<Order>` | Stores dynamic lists of line items, catalog records, and orders where the size changes dynamically at runtime. |
| **`HashMap`** | `Map<String, Retailer>`, `Map<String, Product>`, `Map<String, Order>` | Enables instant $O(1)$ constant-time lookup by unique keys (`retailerId`, `productId`, `orderId`) without looping. |
| **Exception Handling** | `BusinessRuleException`, `InsufficientStockException` | Safely catches invalid inputs, MOQ violations, out-of-stock conditions, and credit limit breaches. |
| **Java Swing GUI** | `LoginFrame`, `MainDashboardFrame`, `CreateOrderPanel`, etc. | Clean, modern desktop GUI with event listeners, tabular views, and interactive dialogues. |

---

## 🏛️ System Architecture

```mermaid
graph TD
    UI[Swing GUI Layer: LoginFrame & MainDashboardFrame]
    UI --> Service[OrderService & Business Logic]
    Service --> Repo[DataRepository Singleton]
    Repo --> HashMapCollections[(HashMaps: O(1) Key Lookup)]
    Repo --> ArrayListCollections[(ArrayLists: Dynamic Lists)]
    Service --> ExceptionLayer[Custom Exceptions: BusinessRuleException]
    Service --> Models[Domain Models: Retailer, Product, Order, Invoice]
```

---

## 📂 Project Directory Structure

```
Java_major_project/
├── .gitignore                     # Git ignore rules
├── run.sh                         # 1-click run script for macOS / Linux
├── run.bat                        # 1-click run script for Windows
├── README.md                      # Complete project documentation
├── CONCEPTS_AND_LOGIC.md          # In-depth explanation of all concepts & code logic
└── src/
    └── com/
        └── wholesale/
            ├── Main.java          # Application entry point
            ├── model/
            │   ├── User.java              # User login & role model
            │   ├── Retailer.java          # Retailer profile & credit model
            │   ├── Product.java           # Inventory & tiered pricing rules
            │   ├── TieredPricingRule.java # Bulk volume tiers definition
            │   ├── OrderItem.java         # Order line item with savings
            │   ├── Order.java             # Bulk order entity with totals & tax
            │   ├── OrderStatus.java       # Fulfillment status enum
            │   └── Invoice.java           # Commercial tax invoice generator
            ├── service/
            │   ├── DataRepository.java    # In-memory storage (HashMaps + ArrayLists)
            │   └── OrderService.java      # Order validation & business logic
            ├── exception/
            │   ├── BusinessRuleException.java       # Business validation errors
            │   └── InsufficientStockException.java  # Warehouse stock violation
            ├── test/
            │   └── WholesaleSystemTest.java         # Automated verification tests
            └── ui/
                ├── UIUtils.java             # Shared colors, fonts & card styling
                ├── LoginFrame.java          # Login window
                ├── MainDashboardFrame.java  # Main tabbed dashboard
                ├── RetailerPanel.java       # Retailer directory tab
                ├── ProductPanel.java        # Product catalog tab
                ├── CreateOrderPanel.java    # Place bulk order tab
                ├── OrderTrackingPanel.java  # Delivery tracking & invoices tab
                ├── ReportsPanel.java        # Summary metrics & audit tab
                └── InvoiceDialog.java       # Printable tax invoice modal
```

---

## ⚡ How to Run the Project

### Prerequisites
- Java Development Kit (JDK 8 or higher, JDK 17 / 21 / 25 tested and verified).

---

### Option 1: Using the 1-Click Scripts

#### On macOS / Linux:
```bash
./run.sh
```

#### On Windows:
Double-click `run.bat` or run in Command Prompt:
```cmd
run.bat
```

---

### Option 2: Running via Terminal (Manual Commands)

1. **Compile all Java source files:**
   ```bash
   find src -name "*.java" > sources.txt
   javac -d bin @sources.txt
   ```
   *(On Windows Command Prompt: `dir /s /B src\*.java > sources.txt` followed by `javac -d bin @sources.txt`)*

2. **Launch the application:**
   ```bash
   java -cp bin com.wholesale.Main
   ```

3. **Run the automated unit tests:**
   ```bash
   java -cp bin com.wholesale.test.WholesaleSystemTest
   ```

---

### Option 3: In an IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Open the folder `Java_major_project` in your IDE.
2. Ensure the `src` folder is marked as the **Sources Root**.
3. Run `com.wholesale.Main.java`.

---

## 🔑 Default Login Credentials

| Username | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | Warehouse Administrator | Full access to order processing, inventory, and reports |
| `sales` | `sales123` | Senior Sales Executive | Order placement and retailer management |

---

## 🧪 Automated Testing Verification

The project includes an automated test suite ([WholesaleSystemTest.java](file:///Users/somyajeet/Git/college%20projects/Java_major_project/src/com/wholesale/test/WholesaleSystemTest.java)) testing:
- **Test 1:** Tiered discount formulas at 10, 60, 250, and 600 units.
- **Test 2:** $O(1)$ HashMap lookups for retailers and products.
- **Test 3:** Safe prevention of orders exceeding warehouse inventory (`InsufficientStockException`).
- **Test 4:** Enforcement of retailer credit limits (`BusinessRuleException`).
- **Test 5:** Stock deduction, order state update, and invoice linkage.

Run tests anytime with:
```bash
java -cp bin com.wholesale.test.WholesaleSystemTest
```

---

## 📖 In-Depth Concept & Logic Explanation
For a complete, beginner-friendly walkthrough explaining **OOP**, **`ArrayList`**, **`HashMap`**, **Tiered Pricing Algorithms**, and **Exception Handling** with diagrams and examples, see:
👉 **[CONCEPTS_AND_LOGIC.md](file:///Users/somyajeet/Git/college%20projects/Java_major_project/CONCEPTS_AND_LOGIC.md)**

---

## 📜 License
Developed as an academic major project prototype for **ITM Skills University (B.Tech CSE Semester III)**. Open for academic learning and demonstration.
