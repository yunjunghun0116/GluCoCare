import 'package:flutter/material.dart';

import '../../../shared/constants/app_colors.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  var _currentIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("글루코케어")),
      backgroundColor: AppColors.backgroundColor,
      body: Container(child: Text("hello")),
    );
  }
}
