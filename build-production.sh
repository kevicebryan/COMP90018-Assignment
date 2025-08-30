#!/bin/bash

# Production Build Script
# This script sets up environment variables for secure API key management

echo "🚀 Starting Production Build..."

# Check if API key is provided
if [ -z "$MAPS_API_KEY" ]; then
    echo "❌ Error: MAPS_API_KEY environment variable is not set"
    echo "Please set it using: export MAPS_API_KEY=your_api_key_here"
    exit 1
fi

echo "✅ MAPS_API_KEY is set (length: ${#MAPS_API_KEY})"

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build release APK
echo "🔨 Building release APK..."
./gradlew assembleRelease

# Build release AAB (for Google Play Store)
echo "📦 Building release AAB..."
./gradlew bundleRelease

echo "✅ Production build completed!"
echo "📱 APK location: app/build/outputs/apk/release/"
echo "📦 AAB location: app/build/outputs/bundle/release/"
