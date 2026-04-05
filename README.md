# 🏦 Finanzo
### *Your Personal Finance, Simplified.*

**Finanzo** is a modern, high-performance Android application designed to give you absolute clarity over your financial life. While most banking apps only show you what's in your account, **Finanzo** helps you understand **why** it's there and **where** it's going. It bridges the gap between digital banking and physical cash management with a beautiful, gesture-driven interface.

---

## 🌎 Real-World Application: The "Finanzo" Difference

In a fast-paced economy, financial stress often comes from a lack of visibility. Finanzo is designed to tackle several real-world pain points:

* **The "Invisible" Cash Problem:** Many users struggle to track physical cash spent on small daily items (coffee, transit, tips). Finanzo makes logging these 5-second tasks easy, ensuring your "Cash" balance is always accurate.
* **Unified Financial Mapping:** Instead of toggling between multiple banking apps and a physical wallet, Finanzo provides a **single pane of glass** to view your total liquidity.
* **Behavioral Nudging:** By forcing a moment of reflection during data entry, the app acts as a psychological speed bump against impulse spending.
* **Emergency Preparedness:** By clearly separating "Bank" and "Cash" holdings, users can better plan for situations where digital payments might be unavailable.

---

## ✨ Feature Deep-Dive

### 1. Dual-Wallet Ecosystem (Cash vs. Bank)
Finanzo treats **Cash** and **Bank** as two distinct pillars. This allows users to mirror their real-life financial structure, ensuring that a digital transfer doesn't get confused with a physical withdrawal.

### 2. Smart Notification System 🔔
Finanzo keeps you accountable even when the app is closed:
* **Daily Reminders:** Customizable nudges to log your expenses before you forget the details of the day.
* **Transaction Alerts:** Immediate feedback upon saving a transaction, confirming your updated balance.
* **Persistence:** Using WorkManager to ensure reminders are delivered reliably without draining your battery.

### 3. Dynamic Transaction Management
* **Edit/Undo Capability:** Made a mistake? The history screen allows for instantaneous corrections, automatically recalculating your total balance in the background.
* **Categorization Engine:** Assign transactions to specific buckets (Food, Rent, Salary, Shopping) to generate meaningful data for future insights.

### 4. Interactive Insights & History
A scrolling, chronological ledger provides a "story" of your spending habits. With high-contrast typography and intuitive icons, you can spot spending trends at a glance.

### 5. Frictionless Onboarding
New users aren't met with complex forms. A simple, two-step setup establishes your current financial "ground truth," letting you start tracking in under 30 seconds.

---

## 🛠 Tech Stack & Architecture

Finanzo is built using the latest industry standards for modern Android development:

| Layer | Technology | Rationale |
| :--- | :--- | :--- |
| **Language** | **Kotlin** | Modern, safe, and the first-class language for Android. |
| **UI Framework** | **Jetpack Compose** | Declarative UI for a fluid, "snappy" user experience. |
| **Architecture** | **MVVM + Clean** | Ensures business logic is decoupled from the UI design. |
| **Local Storage** | **Room Database** | High-speed, offline-first data persistence via SQLite. |
| **DI** | **Hilt (Dagger)** | Keeps the codebase modular, clean, and highly testable. |
| **Navigation** | **Type-Safe Routes** | Eliminates runtime crashes via compile-time route checking. |
| **Preferences** | **Jetpack DataStore** | Asynchronous storage for settings and onboarding state. |

---

## 🚀 Getting Started

1. **Clone the Repo:** `git clone https://github.com/jeet030106/finanzo.git`
2. **Open:** Use **Android Studio Ladybug** (or newer).
3. **JDK:** Ensure you are using **JDK 17**.
4. **Run:** Deploy to any device running **Android 8.0 (API 26)** or higher.

---

## 🤝 Future Roadmap
* **AI Spending Forecasts:** Predicting next month's balance based on historical trends.
* **CSV/PDF Export:** Export your data for tax season or personal accounting.
* **Biometric Lock:** Adding an extra layer of security via Fingerprint or FaceID.

**Finanzo** — *Master your money, don't let it master you.*
