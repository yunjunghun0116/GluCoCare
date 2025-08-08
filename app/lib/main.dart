import 'dart:async';

import 'package:app/features/main/screens/splash_screen.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'core/exceptions/exception_handler.dart';
import 'core/notification/notification_service.dart';

final rootStateKey = GlobalKey<NavigatorState>();

Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  NotificationService().showNotification(message);
}

void main() {
  FlutterError.onError = (FlutterErrorDetails details) {
    ExceptionHandler().handleException(details.exception, details.stack, rootStateKey.currentContext);
  };
  runZonedGuarded(() async {
    WidgetsFlutterBinding.ensureInitialized();
    await Firebase.initializeApp();
    FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);
    await NotificationService().initialize();
    await NotificationService().settingHandler();
    runApp(ProviderScope(child: const MainApp()));
  }, (error, stackTrace) => ExceptionHandler().handleException(error, stackTrace, rootStateKey.currentContext));
}

class MainApp extends StatelessWidget {
  const MainApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: "글루코케어",
      debugShowCheckedModeBanner: false,
      navigatorKey: rootStateKey,
      builder: (context, child) {
        if (child == null) return Container();
        return MediaQuery(
          data: MediaQuery.of(context).copyWith(textScaler: const TextScaler.linear(1)),
          child: child,
        );
      },
      home: SplashScreen(),
    );
  }
}
