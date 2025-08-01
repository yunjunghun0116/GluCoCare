import 'package:flutter/material.dart';

import '../../../../shared/constants/app_colors.dart';

class SigningScreen extends StatefulWidget {
  final Function registerFunction;

  const SigningScreen({super.key, required this.registerFunction});

  @override
  State<SigningScreen> createState() => _SigningScreenState();
}

class _SigningScreenState extends State<SigningScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      widget.registerFunction();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.backgroundColor,
      body: Column(
        children: [
          SizedBox(height: 100),
          SizedBox(width: 200, height: 200, child: Image.asset("assets/logo/app_logo_no_background.png")),
          Center(
            child: Center(
              child: Text(
                "안좋은 감정을 해결해주는\n나만의 부정감정 완화 에이전트",
                textAlign: TextAlign.center,
                style: TextStyle(color: AppColors.subColor3, fontSize: 16, height: 20 / 16),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
