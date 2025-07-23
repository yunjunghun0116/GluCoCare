# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is **GlucoseCoCare**, a Flutter application for glucose care management. The project is currently in its initial setup phase with the standard Flutter counter app template.

## Development Commands

### Essential Flutter Commands
- `flutter run` - Run the app on a connected device/emulator
- `flutter run --hot-reload` - Run with hot reload enabled
- `flutter build apk` - Build Android APK
- `flutter build ios` - Build iOS app
- `flutter clean` - Clean build artifacts
- `flutter pub get` - Install/update dependencies
- `flutter pub upgrade` - Upgrade dependencies to latest versions

### Testing and Quality
- `flutter test` - Run all unit and widget tests
- `flutter analyze` - Run static analysis and linting
- `flutter doctor` - Check Flutter installation and dependencies

## Project Structure

This is a standard Flutter project with the following key directories:

- `lib/` - Main Dart source code
  - `main.dart` - Application entry point with MaterialApp setup
- `test/` - Widget and unit tests
- `android/` - Android-specific configuration and native code
- `ios/` - iOS-specific configuration and native code
- `pubspec.yaml` - Project configuration and dependencies

## Architecture Notes

- Uses Material Design as the primary UI framework
- Currently implements a simple StatefulWidget pattern in `main.dart`
- The app uses `flutter_lints` for code quality enforcement
- Test setup uses `flutter_test` framework with widget testing

## Dependencies

### Core Dependencies
- `flutter` (SDK)
- `cupertino_icons` (^1.0.8) - iOS-style icons

### Development Dependencies
- `flutter_test` (SDK)
- `flutter_lints` (^5.0.0) - Linting rules

## Configuration Files

- `analysis_options.yaml` - Dart analyzer configuration with Flutter lints
- `pubspec.yaml` - Project metadata and dependencies
- Platform-specific configurations in `android/` and `ios/` directories