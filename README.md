# WatchAlong - AFL Watch Party App

A Kotlin-based Android application that allows users to host and join AFL watch parties with real-time location tracking, QR code check-ins, and social features.

## 📱 Features

- **AFL Integration**: Real-time match data and team information via AFL REST API
- **Location-Based Discovery**: Find nearby watch parties using Google Maps integration
- **Host & Join Events**: Create and participate in watch-along events
- **QR Code Check-in**: Secure event check-in system with barcode scanning
- **Firebase Integration**: User authentication, real-time data storage, and file storage
- **Social Features**: Like events, view participants, and manage your hosted events

## 🏗️ Architecture

This project follows **MVVM (Model-View-ViewModel)** architecture pattern with **Clean Architecture** principles:

### Architecture Layers

```
┌─────────────────┐
│   Presentation  │  ← Jetpack Compose UI + ViewModels
├─────────────────┤
│     Domain      │  ← Business Logic + Use Cases
├─────────────────┤
│      Data       │  ← Repositories + Data Sources
└─────────────────┘
```

### Project Structure

```
app/src/main/java/com/example/mobilecomputingassignment/
├── data/                           # Data Layer
│   ├── local/                      # Room Database
│   │   ├── dao/                    # Database Access Objects
│   │   ├── database/               # Database setup and modules
│   │   └── entities/               # Database entities
│   ├── remote/                     # External data sources
│   │   ├── api/                    # AFL REST API services
│   │   ├── dto/                    # Data transfer objects
│   │   └── firebase/               # Firebase services
│   ├── repository/                 # Repository implementations
│   └── models/                     # Raw data models
│
├── domain/                         # Business Logic Layer
│   ├── models/                     # Business/Domain models
│   ├── usecases/                   # Business logic operations
│   │   ├── auth/                   # Authentication use cases
│   │   ├── watchalong/             # Watch party operations
│   │   ├── afl/                    # AFL data operations
│   │   └── location/               # Location/mapping operations
│   └── repository/                 # Repository interfaces
│
├── presentation/                   # UI Layer
│   ├── viewmodels/                 # ViewModels for screens
│   ├── ui/                         # Jetpack Compose UI
│   │   ├── screens/                # Individual screen composables
│   │   │   ├── auth/               # Login/Register screens
│   │   │   ├── home/               # Home screen
│   │   │   ├── map/                # Map view with events
│   │   │   ├── create/             # Create watch party
│   │   │   ├── watchalong/         # Event details/management
│   │   │   ├── profile/            # User profile screens
│   │   │   └── qr/                 # QR scanner/generator
│   │   ├── components/             # Reusable UI components
│   │   ├── navigation/             # Compose Navigation
│   │   └── theme/                  # App theming
│   ├── state/                      # UI state classes
│   └── utils/                      # UI utilities
│
├── core/                           # Infrastructure
│   ├── di/                         # Dependency injection (Hilt)
│   ├── network/                    # Network utilities
│   ├── location/                   # Location services
│   ├── permissions/                # Permission handling
│   ├── storage/                    # Local storage
│   └── utils/                      # General utilities
│
└── MainActivity.kt                 # Main entry point
```

## 🛠️ Tech Stack

### Core
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **MVVM Architecture** - Architectural pattern
- **Clean Architecture** - Code organization principles

### Android Jetpack
- **Hilt** - Dependency injection
- **Navigation Compose** - Navigation between screens
- **Room** - Local database
- **ViewModel** - UI state management
- **LiveData/StateFlow** - Reactive data streams

### Backend & APIs
- **Firebase Authentication** - User authentication
- **Cloud Firestore** - Real-time database
- **Firebase Storage** - File storage
- **AFL REST API** - Match and team data
- **Retrofit** - HTTP client for API calls

### Location & Maps
- **Google Maps SDK** - Map display and interaction
- **Location Services** - GPS and location tracking
- **Geofencing** (Optional) - Location-based notifications

### Other Libraries
- **CameraX** - QR code scanning
- **ZXing** - QR code generation
- **Coil** - Image loading
- **Gson** - JSON serialization

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Google Play Services
- Firebase project setup

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone [repository-url]
   cd watchalong-app
   ```

2. **Firebase Configuration**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Enable Authentication, Firestore, and Storage
   - Download `google-services.json` and place it in `app/` directory

3. **API Keys Setup**
   - Create `local.properties` file in root directory
   - Add your API keys:
     ```properties
     MAPS_API_KEY="your_google_maps_api_key"
     AFL_API_KEY="your_afl_api_key"
     ```

4. **Build and Run**
   ```bash
   ./gradlew assembles
   ```

## 📱 App Flow

### User Journey
1. **Authentication** → Login/Register with Firebase Auth
2. **Home Screen** → View nearby watch parties and AFL matches
3. **Map View** → Discover events by location
4. **Create Event** → Host a new watch party
5. **Event Details** → View/manage event information
6. **QR Check-in** → Scan QR code to join event
7. **Profile** → Manage hosted events and settings

### Data Flow (MVVM)
```
UI (Compose) ←→ ViewModel ←→ UseCase ←→ Repository ←→ Data Source
```

## 🧪 Testing

### Test Structure
```
src/test/                          # Unit Tests
├── data/repository/               # Repository tests
├── domain/usecases/               # Use case tests
└── presentation/viewmodels/       # ViewModel tests

src/androidTest/                   # Integration Tests
├── data/local/                    # Database tests
├── ui/                           # UI tests
└── di/                           # DI tests
```

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## 🔧 Build Configuration

### Build Types
- **Debug** - Development build with logging
- **Release** - Production build with ProGuard

### Flavors
- **Dev** - Development environment
- **Prod** - Production environment

## 📋 Development Guidelines

### Code Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Write KDoc for public APIs
- Keep functions small and focused

### Git Workflow
- Use feature branches: `feature/map-integration`
- Write descriptive commit messages
- Create pull requests for code review

### Architecture Rules
- **Data layer** should not depend on presentation layer
- **Domain layer** should not depend on Android framework
- **ViewModels** should not hold references to Views
- Use **dependency injection** for all dependencies

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Follow coding standards
4. Write tests for new features
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🐛 Known Issues

- Location permission handling needs improvement
- QR code scanning in low light conditions
- Network connectivity edge cases

## 📞 Support

For questions or issues:
- Create an issue in the repository
- Contact the development team
- Check the documentation wiki

---

**Built with ❤️ using Kotlin and Jetpack Compose**
