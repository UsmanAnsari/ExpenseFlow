<div align="center">

# 💸 ExpenseFlow

<p align="center">
  <img src="app/src/main/ic_launcher-playstore.png" alt="ExpenseFlow Logo" width="25%" height="25%"/>
</p>

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-29+-green.svg?style=flat&logo=android)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025.12.01-blue.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)

![Architecture](https://img.shields.io/badge/Architecture-Clean_Architecture-blue?style=for-the-badge)
![Pattern](https://img.shields.io/badge/Pattern-MVVM%2FMVI-purple?style=for-the-badge)
![Database](https://img.shields.io/badge/Database-Room-orange?style=for-the-badge)

### ExpenseFlow - Personal Expense Tracker

**A personal finance tracking application built with Clean Architecture, MVVM/MVI pattern, and a fully offline-first local database. Demonstrates reactive UI, multi-entity Room schema, and modern Jetpack Compose development.**

*Built as a portfolio project to demonstrate local-first Android architecture and financial domain modelling.*

</div>

---

## 📱 Features

### Core Functionality
- **📊 Dashboard** — Real-time overview of income, expenses, and current balance with recent transaction feed
- **💳 Transaction Management** — Full CRUD for income and expense transactions with category and account assignment
- **🏦 Multiple Accounts** — Track Cash, Bank, and custom account types with independent balances
- **🏷️ Custom Categories** — Create categories with custom icons and colors for income or expenses
- **🔄 Reactive Updates** — All screens update instantly via Room Flow — no manual refresh needed

### Technical Highlights
- **🏗️ Clean Architecture** — Strict domain/data/presentation separation with dependency inversion
- **🔄 MVVM/MVI Pattern** — Unidirectional data flow with immutable `StateFlow`-backed UI state
- **🗄️ Relational Room Schema** — 3-entity schema with foreign key constraints and cascade rules
- **⚡ Kotlin Flow** — Reactive data streams from database through to Compose UI
- **💉 Hilt DI** — Compile-time dependency injection throughout all layers
- **🧱 Modular Use Cases** — Every business operation encapsulated in a single-responsibility use case

---

## 📸 Screenshots

<p align="center">
  <img src="screenshots/Screenshot_Dashboard.png" width="24%" />
  <img src="screenshots/Screenshot_Transactions.png" width="24%" />
  <img src="screenshots/Screenshot_Accounts_ADD.png" width="24%" />
  <img src="screenshots/Screenshot_Accounts_Managment.png" width="24%" />
</p>

---

## 🎬 [ExpenseFlow DEMO - YouTube](https://youtube.com/shorts/dnTMPW8SoZ0)

---

## 📲 [Download & Install the App](https://github.com/UsmanAnsari/ExpenseFlow/releases/download/v1.0.0/app-debug.apk)

---

## 🛠️ Tech Stack

| Category | Technology | Why This Choice |
|----------|------------|-----------------|
| **Language** | Kotlin 2.2.21 | Coroutines + Flow for reactive streams, null safety |
| **UI Framework** | Jetpack Compose + Material 3 | Declarative UI driven directly by StateFlow |
| **Architecture** | Clean Architecture + MVVM/MVI | Separation of concerns, independently testable layers |
| **Dependency Injection** | Dagger Hilt | Compile-time safety, integrates natively with ViewModel |
| **Database** | Room (SQLite) | Type-safe DAOs with first-class Flow support |
| **Async** | Kotlin Coroutines + Flow | Native async — no RxJava overhead |
| **Navigation** | Compose Navigation | Consistent back-stack with type-safe arguments |
| **Build System** | Gradle KTS + Version Catalog | Reproducible builds and centralised dependency versions |

---

## 🏗️ Architecture

ExpenseFlow follows **Clean Architecture** with a clear separation between the presentation, domain, and data layers. The domain layer contains zero Android dependencies — all business logic is pure Kotlin.

### Three-Layer Architecture

```
┌────────────────────────────────────────────────────────────────────────────┐
│                              PRESENTATION LAYER                            │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Screens (Compose)    │    ViewModels    │    UI State/Events       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
├────────────────────────────────────────────────────────────────────────────┤
│                                DOMAIN LAYER                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Use Cases    │    Domain Models    │    Repository Interfaces      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
├────────────────────────────────────────────────────────────────────────────┤
│                                 DATA LAYER                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Repository Impl    │    Room DAOs    │    Entities & Mappers       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────────┘
```

### Data Flow: Database → UI

One of the key architectural benefits in ExpenseFlow is that the UI reacts to data changes automatically. When a transaction is added, the Dashboard balance updates without any manual refresh:

```
Room Database
     │  (Flow<List<TransactionEntity>>)
     ▼
Repository Implementation
     │  (maps Entity → Domain Model)
     ▼
Use Case
     │  (applies business logic)
     ▼
ViewModel (StateFlow<UiState>)
     │  (collectAsState())
     ▼
Compose Screen  ← recomposes automatically
```

---

## 🗃️ Database Schema

ExpenseFlow uses **Room** with **3 entities** and foreign key constraints that enforce referential integrity — transactions cannot exist without a valid account and category.

```
┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
│     ACCOUNTS        │     │    TRANSACTIONS     │     │     CATEGORIES      │
├─────────────────────┤     ├─────────────────────┤     ├─────────────────────┤
│ id (PK)             │────<│ accountId (FK)      │>────│ id (PK)             │
│ name                │     │ id (PK)             │     │ name                │
│ type                │     │ amount              │     │ icon                │
│ balance             │     │ type (INCOME/EXP)   │     │ color               │
│ icon                │     │ categoryId (FK)     │     │ type (INCOME/EXP)   │
│ color               │     │ date                │     │ isDefault           │
│ isDefault           │     │ note                │     │ createdAt           │
│ createdAt           │     │ createdAt           │     └─────────────────────┘
└─────────────────────┘     └─────────────────────┘
```

### Schema Design Decisions

| Decision | Rationale |
|----------|-----------|
| **Foreign keys on Transactions** | Prevents orphaned transactions if an account or category is deleted |
| **`type` field on Categories** | Income and expense categories are kept separate to avoid invalid pairings at the domain level |
| **`isDefault` on Accounts & Categories** | Default entries ship with the app and are protected from deletion |
| **`balance` on Accounts** | Maintained as a derived value updated atomically with each transaction insert/update/delete |

---

## 🎯 Key Implementation Highlights

### 1. Reactive ViewModel with StateFlow

```kotlin
@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            getTransactionsUseCase()
                .collect { transactions ->
                    _uiState.update { it.copy(transactions = transactions, isLoading = false) }
                }
        }
    }

    fun onEvent(event: TransactionsEvent) {
        when (event) {
            is TransactionsEvent.DeleteTransaction -> deleteTransaction(event.id)
            is TransactionsEvent.AddTransaction -> addTransaction(event.transaction)
        }
    }
}
```

### 2. Clean Use Case Pattern

```kotlin
// Single responsibility — one use case, one operation
class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(transaction: TransactionDom): Result<Unit> {
        // Validate amount
        if (transaction.amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be positive"))
        }
        
        // Insert transaction and update account balance atomically
        return transactionRepository.insertWithBalanceUpdate(transaction)
    }
}
```

### 3. Entity ↔ Domain Mapping

```kotlin
// Keeps the domain layer free of Room annotations
fun TransactionEntity.toDomain(): TransactionDom = TransactionDom(
    id = id,
    amount = amount,
    type = TransactionType.valueOf(type),
    categoryId = categoryId,
    accountId = accountId,
    date = date,
    note = note
)

fun TransactionDom.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    amount = amount,
    type = type.name,
    categoryId = categoryId,
    accountId = accountId,
    date = date,
    note = note
)
```

---

## 🚧 Scope: Portfolio vs Production

### What's Implemented

| Feature | Status | Notes |
|---------|--------|-------|
| **Transaction CRUD** | ✅ Complete | With account balance updates |
| **Multiple Accounts** | ✅ Complete | Cash, Bank, and custom types |
| **Custom Categories** | ✅ Complete | Icons, colors, income/expense types |
| **Reactive Dashboard** | ✅ Complete | Room Flow → Compose auto-updates |
| **Foreign Key Constraints** | ✅ Complete | Referential integrity enforced |
| **Automated Tests** | 🔜 Planned | Next phase |
| **CI/CD Pipeline** | 🔜 Planned | GitHub Actions |

### Production Enhancements

| Enhancement | Why | Complexity |
|-------------|-----|------------|
| **Automated Tests** | Validate use cases and state transitions | Medium |
| **Reports & Charts** | Pie/bar charts for spending by category | Medium (MPAndroidChart) |
| **Budget Tracking** | Set monthly limits per category with alerts | Medium |
| **Data Export (CSV/PDF)** | Share records for accounting | Medium |
| **Cloud Sync** | Access data across multiple devices | High (backend + auth) |
| **Recurring Transactions** | Automate fixed monthly expenses | Medium |

---

## 🎓 What I Learned

<details>
<summary><b>Reactive Architecture with Room + Flow</b></summary>

**Room Flow is the right way to observe data** — Rather than fetching data once and manually refreshing, Room DAOs that return `Flow<T>` automatically emit new values whenever the underlying table changes. The entire UI stays in sync with the database with zero manual coordination.

**StateFlow is the right bridge between Flow and Compose** — Using `collectAsState()` on a `StateFlow` in a Compose screen means the UI re-renders only when state actually changes. This is more efficient than collecting a raw `Flow` directly in the UI layer.

**Single source of truth eliminates sync bugs** — Account balances are maintained in the database and observed reactively. There's no in-memory cache that can get out of sync with what's stored.

</details>

<details>
<summary><b>Clean Architecture in Practice</b></summary>

**Mappers are worth the boilerplate** — Converting between `TransactionEntity` (Room) and `TransactionDom` (domain model) feels redundant at first. The payoff is that the domain layer has zero `@Entity` or `@ColumnInfo` annotations — it's pure Kotlin that could run on any platform.

**Use cases prevent ViewModel bloat** — Without use cases, ViewModels become repositories of business logic that's hard to test and reuse. Each use case is a named, testable operation: `AddTransactionUseCase`, `DeleteTransactionUseCase`. The ViewModel just routes events.

**Repository interfaces let you swap implementations** — The domain layer depends on `TransactionRepository` (an interface), not `TransactionRepositoryImpl`. This makes it straightforward to write a fake in-memory implementation for tests.

</details>

<details>
<summary><b>Jetpack Compose Patterns</b></summary>

**`collectAsState()` is just the last step in a chain** — The real work happens upstream: Room emits → repository maps → use case processes → ViewModel updates StateFlow → Compose collects. Understanding the whole chain makes debugging much easier.

**Hilt + ViewModel removes factory boilerplate** — `@HiltViewModel` and `@Inject constructor` replace the need for `ViewModelProvider.Factory` entirely. Dependencies are injected at construction time, fully managed by Hilt.

</details>

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 29+
- No API keys required — fully offline app

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/UsmanAnsari/ExpenseFlow.git
cd ExpenseFlow
```

2. **Build and Run**
```bash
./gradlew installDebug
# Or click Run ▶️ in Android Studio
```

---

## 📁 Project Structure

```
app/src/main/java/com/uansari/expenseflow/
├── core/
│   ├── di/                        # Hilt modules
│   ├── navigation/                # Navigation graph
│   └── ui/                        # Theme, shared components
│
├── data/
│   ├── local/
│   │   ├── dao/                   # Room DAOs
│   │   ├── entity/                # Room entities
│   │   └── ExpenseDatabase.kt     # Room database class
│   ├── mapper/                    # Entity ↔ Domain mappers
│   └── repository/                # Repository implementations
│
├── domain/
│   ├── model/                     # Domain models (no Android deps)
│   ├── repository/                # Repository interfaces
│   └── usecase/
│       ├── account/
│       ├── category/
│       └── transaction/
│
└── feature/
    ├── accounts/
    ├── categories/
    ├── dashboard/
    ├── settings/
    └── transactions/
```

---

## 🗺️ Roadmap

- [x] Core transaction management (CRUD)
- [x] Account management
- [x] Category management
- [x] Dashboard with reactive balance summary
- [ ] Automated tests (use cases + ViewModels)
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Reports & Charts (Pie/Bar)
- [ ] Budget tracking with alerts
- [ ] Data export (CSV/PDF)
- [ ] Cloud sync (Firebase)

---

## 👤 Author

**Usman Ansari**

- 💼 LinkedIn: [Usman Ansari](https://www.linkedin.com/in/usman1ansari/)
- 🐙 GitHub: [@UsmanAnsari](https://github.com/UsmanAnsari)
- 📧 Email: usman10ansari@gmail.com

---

<div align="center">

**Built with ❤️ Kotlin and Jetpack Compose**

</div>
