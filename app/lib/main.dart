import 'package:app/features/main/screens/splash_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

void main() {
  runApp(ProviderScope(child: const MainApp()));
}

class MainApp extends StatelessWidget {
  const MainApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: "글루코케어",
      debugShowCheckedModeBanner: false,
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
