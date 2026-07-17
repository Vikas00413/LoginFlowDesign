# LoginFlowDesign

A modular Android login flow sample built with **Jetpack Compose**, **MVI**, **Hilt**, **Room + SQLCipher**, **Retrofit**, and **EncryptedSharedPreferences**. Demonstrates email/social login, biometric unlock, token refresh, encrypted local storage, and profile management.

**Demo API:** [DummyJSON](https://dummyjson.com/) (`emilys` / `emilyspass`)

---

## Screenshots

| Login | Biometric prompt |
|-------|------------------|
| ![Login screen](docs/screenshots/01_login.png) | ![Biometric prompt](docs/screenshots/02_biometric_prompt.png) |

| Profile | Settings (debug) |
|---------|------------------|
| ![Profile screen](docs/screenshots/03_profile.png) | ![Settings screen](docs/screenshots/04_settings_debug.png) |

---

## Features

- Email/password login with token + refresh token persistence
- Google & Facebook sign-in (maps to demo API user after SDK auth)
- Biometric unlock on app launch (optional)
- Automatic token refresh via `AuthInterceptor` + `TokenAuthenticator`
- SQLCipher-encrypted Room database for user & session data
- EncryptedSharedPreferences for auth tokens
- MVI presentation layer in feature modules
- Debug data viewer (debug builds) for Room + prefs inspection

---

## Module Architecture

```
:app                 → NavHost, Splash, AppAuthNavigator, SocialSignInHelper
:feature-auth        → LoginScreen, BiometricScreen, LoginViewModel (MVI)
:feature-profile     → ProfileScreen, SettingsScreen, ProfileViewModel (MVI)
:core-auth           → AuthManager, AuthRepository, TokenManager, AuthApi
:core-network        → Retrofit, AuthInterceptor, TokenAuthenticator
:core-database       → SQLCipher Room, UserDao, SessionDao
```

### Dependency Graph

```mermaid
graph TD
    app[:app]
    fa[:feature-auth]
    fp[:feature-profile]
    ca[:core-auth]
    cn[:core-network]
    cd[:core-database]

    app --> fa
    app --> fp
    app --> ca
    app --> cn
    app --> cd
    fa --> ca
    fp --> ca
    ca --> cd
    cn --> ca
```

---

## High-Level Design (HLD)

### 1. System Context

```mermaid
flowchart LR
    User([User])
    App[LoginFlow Android App]
    Google[Google Sign-In SDK]
    Facebook[Facebook Login SDK]
    API[DummyJSON API]

    User --> App
    App --> Google
    App --> Facebook
    App --> API
```

### 2. Layered Architecture

```mermaid
flowchart TB
    subgraph Presentation
        UI[Compose UI]
        VM[MVI ViewModel<br/>Intent / State / Effect]
        NAV[AuthNavigator]
    end

    subgraph Domain
        AM[AuthManager]
        REPO[AuthRepository]
    end

    subgraph Data
        API[AuthApi - Retrofit]
        TM[TokenManager<br/>EncryptedSharedPreferences]
        ROOM[(Room + SQLCipher<br/>UserDao / SessionDao)]
    end

    subgraph Network
        RETRO[Retrofit + OkHttp]
        INT[AuthInterceptor]
        AUTH[TokenAuthenticator]
    end

    UI --> VM
    VM --> AM
    VM --> NAV
    AM --> REPO
    REPO --> API
    REPO --> TM
    REPO --> ROOM
    API --> RETRO
    RETRO --> INT
    RETRO --> AUTH
    INT --> TM
    AUTH --> TM
```

### 3. App Launch & Routing

```mermaid
flowchart TD
    Start([App Launch]) --> Splash[SplashViewModel]
    Splash --> Init[AuthManager.initialize]
    Init --> Check{Session valid?}

    Check -->|No| Login[Login Screen]
    Check -->|Yes + Biometric ON| Bio[Biometric Screen]
    Check -->|Yes + Biometric OFF| Profile[Profile Screen]

    Login -->|Success| BioPrompt{Enable Biometric?}
    BioPrompt --> Profile
    Bio -->|Success| Profile
    Bio -->|Fail / Skip| Login
```

### 4. Login Sequence

```mermaid
sequenceDiagram
    participant UI as LoginScreen
    participant VM as LoginViewModel
    participant AM as AuthManager
    participant Repo as AuthRepository
    participant API as DummyJSON API
    participant TM as TokenManager
    participant DB as Room DB

    UI->>VM: LoginIntent.LoginClicked
    VM->>AM: login(username, password)
    AM->>Repo: login()
    Repo->>API: POST /auth/login
    API-->>Repo: access + refresh tokens, user
    Repo->>TM: saveTokens()
    Repo->>DB: save user + session
    Repo-->>VM: Success
    VM-->>UI: ShowBiometricDialog Effect
    UI->>VM: BiometricChoice / Skip
    VM->>NAV: NavigateToProfile
```

### 5. Authenticated API Call & Token Refresh

```mermaid
sequenceDiagram
    participant App as App / ViewModel
    participant OkHttp as OkHttp Client
    participant INT as AuthInterceptor
    participant API as DummyJSON API
    participant AUTH as TokenAuthenticator
    participant TM as TokenManager

    App->>OkHttp: GET /auth/me
    OkHttp->>INT: attach Bearer token
    INT->>API: request + Authorization

    alt 401 Unauthorized
        API-->>AUTH: 401
        AUTH->>API: POST /auth/refresh
        API-->>AUTH: new access token
        AUTH->>TM: saveAccessToken()
        AUTH->>API: retry original request
    end

    API-->>App: user profile
```

### 6. Local Storage Design

| Store | Technology | Contents |
|-------|------------|----------|
| `auth_tokens` | EncryptedSharedPreferences | access token, refresh token, expiry, biometric flag |
| `db_passphrase_prefs` | EncryptedSharedPreferences | SQLCipher passphrase |
| `login_flow_encrypted.db` | Room + SQLCipher | `users`, `sessions` tables |

### 7. MVI Flow (Feature Modules)

```mermaid
flowchart LR
    View[Compose View] -->|Intent| VM[ViewModel]
    VM -->|State| View
    VM -->|Effect| View
    VM --> AM[AuthManager]
    VM --> NAV[AuthNavigator]
```

---

## Tech Stack

| Category | Libraries |
|----------|-----------|
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Architecture | MVI, Hilt, ViewModel |
| Network | Retrofit, OkHttp, Gson |
| Database | Room, SQLCipher |
| Security | EncryptedSharedPreferences, Biometric API |
| Social | Google Play Services Auth, Facebook Login |

---

## Getting Started

### Prerequisites

- Android Studio Ladybug or newer
- JDK 17+
- Android SDK 35

### Run

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or device (debug build)

### Demo credentials

```
Username: emilys
Password: emilyspass
```

### Facebook / Google

Replace placeholder values in `app/src/main/res/values/strings.xml`:

- `facebook_app_id`
- `facebook_client_token`

Configure Google Sign-In in [Google Cloud Console](https://console.cloud.google.com/) for your app package `com.example.loginflow`.

---

## Debug Data Viewer

In **debug builds**, open **Profile → Settings → View DB + SharedPrefs (Debug)** to inspect:

- Room `users` and `sessions` tables
- EncryptedSharedPreferences token snapshot (masked)

> Android Studio App Inspection cannot read SQLCipher or EncryptedSharedPreferences directly.

---

## Project Structure

```
LoginFlowSystemDesign/
├── app/                    # Application shell, NavHost, social sign-in
├── feature-auth/           # Login + Biometric (MVI)
├── feature-profile/        # Profile + Settings (MVI)
├── core-auth/              # Auth domain, repository, tokens
├── core-network/           # Retrofit, interceptors
├── core-database/          # Encrypted Room database
└── docs/screenshots/       # README screenshots
```

---

## License

This project is provided as a system design reference sample.
