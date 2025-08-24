# WatchMates - Mobile Computing Assignment

A modern Android application for AFL watch parties built with Kotlin, Jetpack Compose, and Clean Architecture. Currently implements user authentication and onboarding with plans for location-based watch party features.

## 🚧 Current Implementation Status

### ✅ Completed Features

- **Firebase Authentication**: Google Sign-In and email/password registration
- **User Onboarding**: Multi-step registration flow with validation
- **Modern UI**: Jetpack Compose with custom WatchMates theme
- **Clean Architecture**: MVVM pattern with dependency injection (Hilt)
- **Custom Theme**: Racing Sans One typography with orange/yellow color scheme
- **Profile Management**: User profile with points system and team selection
- **QR Code Generation**: Personal QR codes for user identification
- **Legal Documents**: Built-in Privacy Policy and Terms & Conditions viewer
- **Custom Icons**: Professional visibility toggles and menu icons

### 🔄 In Development / Planned Features

- **AFL Integration**: Real-time match data and team information via AFL REST API
- **Location-Based Discovery**: Find nearby watch parties using Google Maps integration
- **Host & Join Events**: Create and participate in watch-along events
- **QR Code Check-in**: Secure event check-in system with barcode scanning
- **Local Database**: Room database for offline data storage
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

### Current Project Structure

```
app/src/main/java/com/example/mobilecomputingassignment/
├── data/                           # Data Layer
│   ├── local/                      # 🔲 Room Database (planned)
│   ├── models/                     # ✅ Data transfer objects
│   │   └── UserDto.kt              # User data model
│   ├── remote/                     # ✅ External data sources
│   │   └── firebase/               # Firebase integration
│   │       └── FirestoreService.kt # Firestore database service
│   └── repository/                 # ✅ Repository implementations
│       └── UserRepository.kt       # User data repository
│
├── domain/                         # ✅ Business Logic Layer
│   ├── models/                     # Business/Domain models
│   │   └── User.kt                 # Domain user model
│   ├── repository/                 # Repository interfaces
│   │   └── IUserRepository.kt      # User repository contract
│   └── usecases/                   # Authentication use cases
│       └── auth/                   # ✅ Authentication operations
│           ├── CheckEmailExistUseCase.kt
│           ├── CheckUsernameExistUseCase.kt
│           ├── GoogleSignInUseCase.kt
│           ├── LoginUseCase.kt
│           └── RegisterUseCase.kt
│
├── presentation/                   # ✅ UI Layer
│   ├── ui/                         # Jetpack Compose UI
│   │   ├── component/              # 🔲 Reusable components (planned)
│   │   ├── screen/                 # ✅ Authentication screens
│   │   │   ├── LoginStep.kt        # Login screen
│   │   │   ├── OnboardingScreen.kt # Onboarding flow
│   │   │   └── SignupSteps.kt      # Registration steps
│   │   └── theme/                  # ✅ Custom WatchMates theme
│   │       ├── Color.kt            # Color scheme
│   │       ├── Theme.kt            # Theme definition
│   │       ├── Type.kt             # Typography (Racing Sans One)
│   │       └── ThemeUsageGuide.txt # Theme usage examples
│   └── viewmodel/                  # ✅ State management
│       └── AuthViewModel.kt        # Authentication view model
│
├── core/                           # ✅ Infrastructure
│   └── di/                         # Dependency injection (Hilt)
│       ├── AppModule.kt            # Application module
│       └── RepositoryModule.kt     # Repository bindings
│
├── ui/                            # 🔲 Legacy UI (empty)
├── MainActivity.kt                 # ✅ Main entry point
└── WatchMatesApplication.kt        # ✅ Application class with Hilt

Legend: ✅ Implemented | 🔲 Planned | 🔄 In Progress
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

### Backend & APIs (Current)

- **Firebase Authentication** - User authentication with Google Sign-In
- **Cloud Firestore** - Real-time database for user data
- **Firebase Storage** - File storage (configured)

### Backend & APIs (Planned)

- **AFL REST API** - Match and team data
- **Retrofit** - HTTP client for API calls

### Location & Maps (Planned)

- **Google Maps SDK** - Map display and interaction
- **Location Services** - GPS and location tracking
- **Room Database** - Local data storage

### Other Libraries

- **ZXing** - QR code generation and scanning
- **Coil** - Image loading for user avatars and team logos
- **CameraX** - QR code scanning (planned)

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

## 📋 Legal Documents & Compliance

### Built-in Legal Document System

WatchMates includes a comprehensive legal document management system:

#### **📄 Privacy Policy**

- Comprehensive privacy protection guidelines
- Age restriction compliance (18+/21+ legal drinking age)
- Location data usage transparency
- Anonymous data collection policies
- User rights and data retention information

#### **📋 Terms & Conditions**

- Age verification requirements for alcohol-serving venues
- Community guidelines for sports watch-along events
- User responsibilities and acceptable use policies
- Venue liability disclaimers
- Legal jurisdiction and governing law

#### **🔧 Template System**

- **Easy Content Management**: Update legal documents in `LegalDocuments.kt`
- **Professional Presentation**: Consistent styling with app theme
- **Accessible Navigation**: Direct access from user profile
- **Version Control**: Track document changes with Git
- **Compliance Ready**: Structured for legal review and approval

#### **📱 User Experience**

```
Profile Menu
├── 📱 Show QR
├── ⚽ Teams
├── ℹ️  Privacy Policy      ← Legal Documents
├── 📋 Terms & Conditions   ← Legal Documents
└── 🚪 Log Out
```

### Age Verification & Compliance

- **Legal Drinking Age Requirement**: 18+ or 21+ depending on jurisdiction
- **Venue Alcohol Service**: Clear disclaimers about alcohol-serving venues
- **User Responsibility**: Explicit acknowledgment of personal responsibility
- **Community Safety**: Guidelines for respectful participation

## 📱 Current App Flow

### Current User Journey

1. **Onboarding** → Welcome screen with app introduction
2. **Authentication Choice** → Login or Register options
3. **Registration Flow** → Multi-step signup with validation
   - Personal Information (Name, Username)
   - Email and Password setup
   - Profile completion
4. **Google Sign-In** → Quick authentication option
5. **Login** → Email/password authentication

### Planned User Journey

1. **Home Screen** → View nearby watch parties and AFL matches
2. **Map View** → Discover events by location
3. **Create Event** → Host a new watch party
4. **Event Details** → View/manage event information
5. **QR Check-in** → Scan QR code to join event
6. **Profile** → Manage hosted events and settings

### Current Data Flow (MVVM)

```
UI (Compose) ←→ AuthViewModel ←→ Auth UseCases ←→ UserRepository ←→ FirestoreService
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

## 🚧 Development Notes & Areas for Attention

### Current Issues & TODOs

- **Navigation**: No navigation component implemented yet - currently only auth screens
- **Error Handling**: Basic error handling in place, could be enhanced with proper error states
- **Input Validation**: Email/username validation implemented, could add more robust password requirements
- **Loading States**: Basic loading states in AuthViewModel, consistent loading UI needed
- **Testing**: Test files exist but no actual tests implemented yet

### Architecture Improvements Needed

- **Repository Pattern**: Currently only UserRepository exists, need repositories for other domains
- **Use Cases**: Only auth use cases implemented, need use cases for main app features
- **Database**: Room database structure planned but not implemented
- **Navigation**: Compose Navigation setup needed for multi-screen flow

### Future Considerations

- **Offline Support**: Room database for offline data storage
- **State Management**: Consider using more sophisticated state management if app grows
- **Security**: Implement proper API key management and security rules
- **Performance**: Image loading optimization for user profiles and AFL content
- **Accessibility**: Ensure proper accessibility support for all UI components

## 📞 Support

For questions or issues:

- Create an issue in the repository
- Contact the development team
- Check the documentation wiki

---

**Built with ❤️ using Kotlin and Jetpack Compose**
