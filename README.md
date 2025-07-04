<div align="center">
<br />
<img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp" alt="Logo" />
</div>

# 📱 Vitesse – Candidate Management App

**Vitesse** is an Android application designed to streamline candidate management for Human Resources (HR) teams. With Vitesse, HR professionals can easily create, edit, view, and manage candidates in their pipeline, with the ability to mark specific candidates as favorites for easier tracking.

---

<div align="center">
   <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
   <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
   <img src="https://img.shields.io/badge/Android%20Studio-143?style=for-the-badge&logo=android-studio&logoColor=white" alt="Android Studio" />
   <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle" />
   <img src="https://img.shields.io/badge/Hilt-3F51B5?style=for-the-badge&logo=dagger&logoColor=white" alt="Hilt" />
   <img src="https://img.shields.io/badge/Moshi-6200EE?style=for-the-badge&logoColor=white" alt="Moshi" />
   <img src="https://img.shields.io/badge/Retrofit-00599C?style=for-the-badge&logoColor=white" alt="Retrofit" />
   <img src="https://img.shields.io/badge/Room-6D4C41?style=for-the-badge&logoColor=white" alt="Room" />    
</div>

## 🚀 Features

- ✅ Add new candidates
- 📝 Edit existing candidate profiles
- 📋 View detailed candidate information
- ⭐ Mark candidates as favorites
- 🔍 Browse all candidates from a centralized home screen

---

## 🧱 Architecture & Technologies

The project follows the **MVVM (Model-View-ViewModel)** architecture and clean code principles to ensure a robust and scalable codebase.

### ✨ Tech Stack

| Layer                | Technology Used                |
|----------------------|--------------------------------|
| Language             | Kotlin                         |
| UI                   | XML Layouts + View Binding     |
| Data Storage         | Room Database                  |
| Networking           | Retrofit + Moshi               |
| Dependency Injection | Hilt                           |
| Code Generation      | KSP (Kotlin Symbol Processing) |
| Testing              | JUnit, MockK, Turbine          |

### 📦 Main Components

- **Model:** `Candidate` class (domain layer)
- **Persistence:** `CandidateDto` (data layer), `CandidateDao`
- **Repository Pattern:** Bridges data and domain layers

### 🧩 Screens (Fragments)

Each screen has its own dedicated `ViewModel`:

- `HomeFragment` – Displays list of all candidates
- `AddFragment` – Form to create a new candidate
- `DetailFragment` – Shows full profile of a selected candidate
- `EditFragment` – Allows modification of an existing candidate

---

## 🔍 Project Structure

```
com.vitesse
├── data
│   ├── dao
│   ├── dto
│   ├── repository
├── domain
│   └── model
├── ui
│   ├── home
│   ├── add
│   ├── detail
│   └── edit
├── viewmodel
├── di (Hilt modules)
└── utils
```

---

## 🧪 Testing Strategy

Unit tests ensure reliability across the app:

- `JUnit` for core test framework
- `MockK` for mocking dependencies
- `Turbine` for testing Kotlin `Flow` emissions

---

## 📲 Installation

To run this app locally:

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/vitesse.git
   ```
2. Open the project in **Android Studio**
3. Sync Gradle and run the app on your emulator or physical device

---

## 🛠️ Future Improvements

- Add filtering and search capabilities
- Implement pagination for large candidate lists
- Add cloud sync or API support
- UI improvements with Jetpack Compose (optional evolution)

---

## 🤝 License

This project is for educational and portfolio purposes. License can be added here if necessary.

---

## 🙌 Author

Made with passion for Android development by Arno
