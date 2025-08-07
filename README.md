# 2004scape Android Client

An Android APK wrapper for the [2004scape browser-based MMORPG](https://github.com/crucifix86/2004scape-server).

## Overview

This project provides a native Android application that wraps the 2004scape web client, offering improved mobile gameplay experience with full-screen support, optimized touch controls, and native Android integration.

## Current Architecture Analysis
- **Renderer**: Canvas-based (789x532 fixed dimensions)
- **Communication**: WebSocket for client-server
- **Mobile Support**: Existing detection and mobile template (client-mobile.ejs)
- **Client**: JavaScript with touch event handling already implemented

## APK Wrapper Benefits
1. **Full Screen Control**: No browser UI, complete immersion
2. **Native Features**: Device orientation, haptic feedback, hardware back button
3. **Persistent Storage**: Local credential/settings storage
4. **Performance**: Hardware acceleration, reduced browser overhead
5. **Distribution**: Google Play Store availability

## Technical Implementation Options

### 1. Android WebView (Simplest)
- Native Android app with WebView component
- Direct control over WebView settings
- Minimal overhead

### 2. Capacitor/Cordova (Recommended)
- Cross-platform (iOS possible later)
- Rich plugin ecosystem
- Native API access
- Easy WebSocket handling

### 3. React Native WebView
- More UI control
- Heavier framework
- Better for custom native UI elements

## Key Implementation Tasks
1. **Dynamic Canvas Scaling**: Handle different screen sizes/ratios
2. **Enhanced Touch Controls**: Optimize for mobile gameplay
3. **Network Resilience**: Handle connection drops/reconnects
4. **Android Lifecycle**: Proper pause/resume handling
5. **Local Storage**: Settings, credentials, cache

## Existing Mobile Code Assets
- `/view/client-mobile.ejs` - Mobile-optimized template
- `/view/client.ejs` - Main game client
- `/public/client/client.js` - Game client logic
- Mobile detection already in `/src/web.ts`

## Project Structure

```
2004scape-android/
├── app/                    # Android application source
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # WebView wrapper & Android code
│   │   │   ├── res/        # Android resources
│   │   │   └── assets/     # Bundled web assets (optional)
│   │   └── test/
│   └── build.gradle
├── gradle/                 # Gradle wrapper files
├── build.gradle           # Project-level build config
├── settings.gradle
└── README.md
```

## Development Roadmap

### Phase 1: Basic WebView Wrapper (Current)
- [ ] Set up Android Studio project
- [ ] Create basic WebView implementation
- [ ] Point to production server URL
- [ ] Handle orientation changes
- [ ] Basic fullscreen support

### Phase 2: Enhanced Mobile Experience
- [ ] Implement dynamic canvas scaling
- [ ] Add loading splash screen
- [ ] Handle network state changes
- [ ] Optimize touch event handling
- [ ] Add haptic feedback for actions

### Phase 3: Native Integration
- [ ] Local credential storage
- [ ] Push notifications support
- [ ] Offline mode detection
- [ ] Hardware back button handling
- [ ] App lifecycle management

### Phase 4: Distribution
- [ ] App signing configuration
- [ ] Google Play Store listing
- [ ] Auto-update mechanism
- [ ] Crash reporting integration

## Related Repositories

- [2004scape Server](https://github.com/crucifix86/2004scape-server) - Main game server and web client
- [2004scape Client Assets](https://github.com/crucifix86/2004scape-server/tree/main/view) - Web client templates and assets