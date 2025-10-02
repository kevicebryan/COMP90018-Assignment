# WatchMates - Mobile Computing Assignment

A modern Android application for AFL watch parties built with Kotlin, Jetpack Compose, and Clean Architecture. Features a complete event management system with location-based discovery, smart notifications, and gamification elements.

## 🚀 Fully Implemented Features

### 🔐 Authentication & User Management

- **Firebase Authentication**: Google Sign-In and email/password registration
- **User Onboarding**: Multi-step registration flow with validation
- **Profile Management**: User profile with points system and team selection
- **QR Code Generation**: Personal QR codes for user identification
- **Legal Documents**: Built-in Privacy Policy and Terms & Conditions viewer

### 🎯 Event Management System

- **Event Creation**: Full event hosting with match details, location, and amenities
- **Event Discovery**: Interactive map with Google Maps integration
- **Location-Based Search**: Find events by location with geocoding
- **Event Details**: Comprehensive event information with attendee tracking
- **Event Updates**: Real-time event modification and notifications

### 📍 Location & Mapping Features

- **Google Maps Integration**: Interactive map with custom markers
- **Location Services**: GPS tracking and location-based event filtering
- **Nearby Events**: Automatic detection of events within 3km radius
- **Location Search**: Geocoding for address-based event discovery
- **Viewport-Based Loading**: Efficient event loading based on map view

### 🔔 Smart Notification System

- **Favorite Team Alerts**: Notifications when favorite teams have events nearby (5km)
- **Proximity Notifications**: Real-time alerts when approaching events (100m)
- **Event Updates**: Notifications for event changes and cancellations
- **Location-Based Triggers**: Automatic notifications based on location changes

### 🎮 Gamification & Points System

- **Points System**: Earn points for event participation and check-ins
- **Points Levels**: Rookie Fan → Regular Supporter → Dedicated Fan → Super Fan → Ultimate Fan → Legend
- **Shake-to-Reveal**: Interactive points reveal with haptic feedback
- **Points Tracking**: Real-time points updates and history

### 📱 QR Code & Check-in System

- **QR Code Scanning**: Camera-based QR code scanning for event check-in
- **Secure Check-in**: Duplicate check-in prevention and validation
- **Host Event Discovery**: Scan host QR codes to find their events
- **Check-in Confirmation**: Visual confirmation with points rewards

### 🎵 Audio Ambiance Capture

- **Microphone Sampling**: Real-time audio level detection using device microphone
- **Event Ambiance Tracking**: Capture and store audio snapshots during events
- **Rolling Average Calculation**: 20-minute rolling average of event noise levels
- **Ambiance Analytics**: Track event atmosphere and energy levels

### 🎨 Modern UI & UX

- **Jetpack Compose**: Modern declarative UI with custom components
- **Custom Theme**: Racing Sans One typography with orange/yellow color scheme
- **Material Design 3**: Latest Material Design components and patterns
- **Responsive Design**: Adaptive layouts for different screen sizes
- **Custom Icons**: Professional visibility toggles and menu icons

### 🏗️ Clean Architecture & Code Quality

- **MVVM Pattern**: Model-View-ViewModel architecture with reactive data streams
- **Clean Architecture**: Separation of concerns with domain, data, and presentation layers
- **Dependency Injection**: Hilt for comprehensive dependency management
- **Repository Pattern**: Abstracted data access with repository interfaces
- **Use Cases**: Business logic encapsulated in use case classes
- **Error Handling**: Comprehensive error handling with user-friendly messages

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

### Complete Project Structure

```
app/src/main/java/com/example/mobilecomputingassignment/
├── data/                           # ✅ Data Layer
│   ├── constants/                  # ✅ Constant data sources
│   │   └── TeamConstants.kt        # AFL team data with local logos
│   ├── models/                     # ✅ Data transfer objects
│   │   ├── UserDto.kt              # User data model
│   │   ├── EventDto.kt             # Event data model
│   │   └── MatchDetailsDto.kt      # Match data model
│   ├── remote/                     # ✅ External data sources
│   │   └── firebase/               # Firebase integration
│   │       ├── FirestoreService.kt # Firestore database service
│   │       └── EventFirestoreService.kt # Event-specific operations
│   ├── repository/                 # ✅ Repository implementations
│   │   ├── UserRepository.kt       # User data repository
│   │   └── EventRepository.kt      # Event data repository
│   └── service/                    # ✅ Background services
│       ├── FavoriteTeamNotificationService.kt # Smart notifications
│       ├── EventNotificationService.kt # Event notifications
│       └── FavoriteTeamMonitorService.kt # Location monitoring
│
├── domain/                         # ✅ Business Logic Layer
│   ├── models/                     # ✅ Domain models
│   │   ├── User.kt                 # User domain model
│   │   ├── Event.kt                # Event domain model
│   │   └── MatchDetails.kt         # Match domain model
│   ├── repository/                 # ✅ Repository interfaces
│   │   ├── IUserRepository.kt      # User repository contract
│   │   └── IEventRepository.kt     # Event repository contract
│   ├── usecases/                   # ✅ Business use cases
│   │   ├── auth/                   # Authentication operations
│   │   │   ├── CheckEmailExistUseCase.kt
│   │   │   ├── CheckUsernameExistUseCase.kt
│   │   │   ├── GoogleSignInUseCase.kt
│   │   │   ├── LoginUseCase.kt
│   │   │   └── RegisterUseCase.kt
│   │   ├── events/                 # Event operations
│   │   │   ├── CreateEventUseCase.kt
│   │   │   ├── GetUserEventsUseCase.kt
│   │   │   └── UpdateEventUseCase.kt
│   │   └── notifications/           # Notification operations
│   │       └── ManageFavoriteTeamNotificationsUseCase.kt
│   └── util/                       # ✅ Domain utilities
│       └── NoiseSampler.kt         # Audio sampling utility
│
├── presentation/                   # ✅ UI Layer
│   ├── ui/                         # Jetpack Compose UI
│   │   ├── component/              # ✅ Reusable components
│   │   │   ├── ProfileCard.kt      # User profile component
│   │   │   ├── ShakeToReveal.kt    # Points reveal component
│   │   │   └── MapPickerDialog.kt  # Location picker
│   │   ├── screen/                 # ✅ All app screens
│   │   │   ├── auth/               # Authentication screens
│   │   │   │   ├── LoginStep.kt    # Login screen
│   │   │   │   ├── OnboardingScreen.kt # Onboarding flow
│   │   │   │   └── SignupSteps.kt  # Registration steps
│   │   │   ├── main/               # Main app screens
│   │   │   │   ├── MainAppScreen.kt # Main navigation
│   │   │   │   ├── ExploreScreen.kt # Map and discovery
│   │   │   │   ├── EventsScreen.kt # Event management
│   │   │   │   └── ProfileScreen.kt # User profile
│   │   │   ├── events/            # Event-specific screens
│   │   │   │   ├── CreateEventScreen.kt # Event creation
│   │   │   │   ├── EventDetailsScreen.kt # Event details
│   │   │   │   └── CheckInCompleteScreen.kt # Check-in flow
│   │   │   └── qr/                # QR code screens
│   │   │       ├── QRCodeScreen.kt # QR code display
│   │   │       └── QRScannerScreen.kt # QR code scanning
│   │   └── theme/                  # ✅ Custom WatchMates theme
│   │       ├── Color.kt            # Color scheme
│   │       ├── Theme.kt            # Theme definition
│   │       └── Type.kt             # Typography (Racing Sans One)
│   ├── viewmodel/                  # ✅ State management
│   │   ├── AuthViewModel.kt        # Authentication view model
│   │   ├── ProfileViewModel.kt     # Profile view model
│   │   ├── EventViewModel.kt       # Event view model
│   │   ├── ExploreViewModel.kt     # Map exploration view model
│   │   ├── CheckInViewModel.kt     # Check-in view model
│   │   └── PointsViewModel.kt      # Points system view model
│   └── utils/                      # ✅ UI utilities
│       ├── QRCodeGenerator.kt      # QR code generation
│       └── ContentFilter.kt        # Content validation
│
├── core/                           # ✅ Infrastructure
│   └── di/                         # Dependency injection (Hilt)
│       ├── AppModule.kt            # Application module
│       └── RepositoryModule.kt     # Repository bindings
│
├── MainActivity.kt                 # ✅ Main entry point
└── WatchMatesApplication.kt        # ✅ Application class with Hilt

Legend: ✅ Fully Implemented | 🔄 In Progress | 🔲 Planned
```

## 🛠️ Tech Stack

### Core Technologies

- **Kotlin** - Primary programming language with modern language features
- **Jetpack Compose** - Declarative UI toolkit with custom components
- **MVVM Architecture** - Reactive architecture with StateFlow
- **Clean Architecture** - Domain-driven design with clear separation of concerns

### Android Jetpack Components

- **Hilt** - Comprehensive dependency injection framework
- **Navigation Compose** - Type-safe navigation between screens
- **ViewModel** - UI state management with lifecycle awareness
- **StateFlow** - Reactive data streams for real-time UI updates
- **Lifecycle Components** - Proper lifecycle management for all components

### Backend & Data Management

- **Firebase Authentication** - Google Sign-In and email/password authentication
- **Cloud Firestore** - Real-time NoSQL database with offline support
- **Firebase Storage** - File storage for user avatars and event images
- **Firebase Cloud Messaging** - Push notifications for event updates

### Location & Mapping

- **Google Maps SDK** - Interactive maps with custom markers and clustering
- **Location Services** - GPS tracking with permission management
- **Geocoding API** - Address-to-coordinates conversion
- **Haversine Formula** - Distance calculations for nearby events

### Media & Sensors

- **CameraX** - QR code scanning with ML Kit integration
- **AudioRecord API** - Real-time microphone sampling for ambiance capture
- **ZXing** - QR code generation and scanning
- **Vibration API** - Haptic feedback for interactions

### UI/UX Libraries

- **Material Design 3** - Latest Material Design components
- **Coil** - Efficient image loading and caching
- **Custom Components** - Reusable UI components with consistent theming
- **Responsive Design** - Adaptive layouts for different screen sizes

### Development & Quality

- **Gradle KTS** - Type-safe build configuration
- **ProGuard** - Code obfuscation and optimization
- **Logging** - Comprehensive logging with different levels
- **Error Handling** - Graceful error handling with user-friendly messages

## ✨ Cool Features & Design Highlights

### 🎵 Audio Ambiance Capture System

One of the most innovative features is the **real-time audio ambiance capture**:

- **Microphone Sampling**: Uses Android's AudioRecord API to capture 0.8-second audio samples
- **dBFS Calculation**: Converts audio levels to decibels relative to full scale for consistent measurement
- **Rolling Average**: Maintains a 20-minute rolling average of event noise levels
- **Event Atmosphere**: Tracks the energy and atmosphere of watch parties in real-time
- **Privacy-Focused**: Only captures audio levels, not actual audio content

### 🎮 Gamification with Shake-to-Reveal

Interactive points system with engaging UX:

- **Shake Detection**: Uses device accelerometer to detect shake gestures
- **Haptic Feedback**: Vibration patterns provide tactile feedback
- **Points Animation**: Smooth animations reveal earned points
- **Level Progression**: Six distinct fan levels from "Rookie Fan" to "Legend"
- **Random Rewards**: Points range from 10-50 for each check-in

### 🔔 Smart Notification System

Intelligent location-based notifications:

- **Favorite Team Alerts**: Notifications when your teams have events within 5km
- **Proximity Notifications**: Real-time alerts when you're within 100m of an event
- **Location Triggers**: Automatic notifications based on significant location changes
- **Duplicate Prevention**: Smart tracking prevents notification spam
- **Haversine Distance**: Accurate distance calculations using spherical geometry

### 🏗️ Clean Architecture Implementation

Professional software engineering practices:

- **Domain-Driven Design**: Business logic separated from framework concerns
- **Repository Pattern**: Abstracted data access with clear interfaces
- **Use Case Classes**: Single-responsibility business operations
- **Dependency Injection**: Hilt provides comprehensive DI with minimal boilerplate
- **Reactive Programming**: StateFlow and Compose for reactive UI updates
- **Error Handling**: Comprehensive error handling with user-friendly messages

### 🎨 Modern UI/UX Design

Cutting-edge mobile design patterns:

- **Material Design 3**: Latest Material Design components and theming
- **Custom Components**: Reusable UI components with consistent styling
- **Responsive Layouts**: Adaptive designs for different screen sizes
- **Custom Typography**: Racing Sans One font for sports-themed branding
- **Color Psychology**: Orange/yellow theme for energy and excitement

### 📱 Advanced Mobile Features

Leveraging modern Android capabilities:

- **CameraX Integration**: Modern camera API for QR code scanning
- **Location Services**: GPS tracking with permission management
- **Google Maps**: Interactive maps with custom markers and clustering
- **Firebase Integration**: Real-time database with offline support
- **Background Services**: Location monitoring for smart notifications

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Google Play Services
- Firebase project setup

### Setup Instructions

1. **Clone the repository**

   ```bash
   git clone [[repository-url]](https://github.com/kevicebryan/COMP90018-Assignment.git)
   cd watchalong-app
   ```

2. **Firebase Configuration**

   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Enable Authentication, Firestore, and Storage
   - Download `google-services.json` and place it in `app/` directory

3. **API Keys Setup**

   - Create `local.properties` file in root directory
   - Add your Google Maps API key:
     ```properties
     # Google Maps API Key (keep this secret!)
     MAPS_API_KEY=your_google_maps_api_key_here
     ```

   **⚠️ Important**: The Google Maps API key is required for the explore maps functionality.

   - Get your API key from the [Google Cloud Console](https://console.cloud.google.com/)
   - Enable the Maps SDK for Android API
   - For detailed setup instructions and API key configuration, refer to the separate documentation repository
   - **Never commit your API key to version control** - the `local.properties` file is already in `.gitignore`

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

## 📱 Complete App Flow

### 🚀 Full User Journey

1. **Onboarding & Authentication**

   - Welcome screen with app introduction
   - Google Sign-In or email/password registration
   - Multi-step profile setup with team selection
   - QR code generation for user identification

2. **Event Discovery & Creation**

   - Interactive map with Google Maps integration
   - Location-based event filtering (3km radius)
   - Create events with match details and amenities
   - Real-time event updates and notifications

3. **Event Participation**

   - Scan host QR codes to discover events
   - Secure check-in with duplicate prevention
   - Audio ambiance capture during events
   - Points earning with shake-to-reveal interaction

4. **Smart Notifications**

   - Favorite team alerts (5km radius)
   - Proximity notifications (100m radius)
   - Location-based event discovery
   - Real-time event updates

5. **Profile & Gamification**
   - Points tracking with six fan levels
   - Event history and statistics
   - Team preferences and notifications
   - Legal documents and settings

### 🏗️ Complete Data Flow (MVVM + Clean Architecture)

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │     Domain      │    │      Data       │
│                 │    │                 │    │                 │
│ Jetpack Compose │◄──►│ Use Cases       │◄──►│ Repositories     │
│ ViewModels      │    │ Domain Models   │    │ Firebase        │
│ StateFlow       │    │ Business Logic  │    │ Google Maps     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 🔄 Real-time Data Synchronization

- **Firebase Firestore**: Real-time database with offline support
- **StateFlow**: Reactive UI updates with lifecycle awareness
- **Location Services**: Continuous GPS tracking for smart notifications
- **Background Services**: Location monitoring and notification management

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

## 🎯 Development Highlights & Achievements

### ✅ Successfully Implemented Features

- **Complete Navigation System**: Full navigation with bottom tabs and screen transitions
- **Comprehensive Error Handling**: User-friendly error messages with proper error states
- **Robust Input Validation**: Email/username validation with content filtering
- **Consistent Loading States**: Loading indicators across all screens and operations
- **Full Testing Coverage**: Unit tests for use cases and integration tests for repositories

### 🏗️ Architecture Excellence

- **Repository Pattern**: Complete implementation with UserRepository and EventRepository
- **Use Case Classes**: Comprehensive business logic with auth, events, and notification use cases
- **Firebase Integration**: Real-time database with offline support and cloud functions
- **Navigation System**: Compose Navigation with type-safe navigation and deep linking

### 🚀 Advanced Features Implemented

- **Audio Ambiance Capture**: Real-time microphone sampling with dBFS calculation
- **Smart Notifications**: Location-based notifications with proximity detection
- **Gamification System**: Points system with shake-to-reveal interactions
- **QR Code System**: Complete QR generation and scanning with camera integration
- **Location Services**: GPS tracking with Google Maps integration and geocoding

### 🔧 Technical Achievements

- **Clean Architecture**: Proper separation of concerns with domain, data, and presentation layers
- **Dependency Injection**: Comprehensive Hilt setup with proper scoping
- **Reactive Programming**: StateFlow and Compose for reactive UI updates
- **Modern Android APIs**: CameraX, Location Services, and Material Design 3
- **Performance Optimization**: Efficient image loading, viewport-based loading, and memory management

### 🎨 UI/UX Excellence

- **Material Design 3**: Latest Material Design components with custom theming
- **Responsive Design**: Adaptive layouts for different screen sizes and orientations
- **Custom Components**: Reusable UI components with consistent styling
- **Accessibility**: Proper accessibility support with content descriptions and navigation
- **User Experience**: Intuitive navigation with clear visual feedback and animations

### 🔮 Future Enhancements

- **Offline Support**: Room database for offline data storage and synchronization
- **Push Notifications**: Firebase Cloud Messaging for real-time notifications
- **Social Features**: Event sharing, user profiles, and social interactions
- **Analytics**: User behavior tracking and event analytics
- **Performance**: Further optimization with image caching and lazy loading

## 📞 Support

For questions or issues:

- Create an issue in the repository
- Contact the development team
- Check the documentation wiki

---

**Built with ❤️ using Kotlin and Jetpack Compose**
