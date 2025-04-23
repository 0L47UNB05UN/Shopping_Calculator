# Shopping Calculator App 🛒💰

[![GitHub License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Compose Version](https://img.shields.io/badge/Jetpack%20Compose-1.5.0-brightgreen)](https://developer.android.com/jetpack/compose)

A modern Android shopping list app with calculation features, built with Jetpack Compose.

<img src="screenshot/shopping_calculator.gif" width="300" alt="App Demo">

## ✨ Features

- **Create shopping lists** with items, prices, and quantities
- **Real-time total calculation** as you add items
- **PDF Export** - Share lists via any Android app
- **List Management**:
  - Save lists for later
  - Rename existing lists
  - Delete unwanted lists
- **Material 3 Design** with dynamic color theming
- **Dark/Light mode** support

## 📦 Technical Stack

- 100% **Kotlin** with modern coroutines
- **Jetpack Compose** UI toolkit
- **AndroidX Navigation** Component
- File system storage for saved lists
- PDF generation using Android's built-in PDF APIs

## 🚀 Getting Started

### Installation
1. Clone this repository
2. Open in Android Studio
3. Build and run on emulator or physical device

## 📄 PDF Generation Details

The app uses Android's native `PdfDocument` API to:
- Create properly formatted shopping lists
- Preserve item details (name, price, quantity)
- Generate shareable PDF files stored in:
 /data/data/com.example.shoppingcalculator/files/saved_lists/


## 🤝 Contributing

I am terrible at coding, so all critics are welcomed.

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Made with ❤️ by Ikupolati Joshua Olatunbosun | ikupolatiolatunbosun@gmail.com
