import 'package:app/core/providers.dart';
import 'package:app/features/auth/presentation/screens/sign_in_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../shared/constants/app_colors.dart';

class SplashScreen extends ConsumerStatefulWidget {
  const SplashScreen({super.key});

  @override
  ConsumerState<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends ConsumerState<SplashScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      await Future.delayed(Duration(seconds: 1), () => _initializeProvider());
      if (!mounted) return;
      Navigator.push(context, MaterialPageRoute(builder: (_) => SignInScreen()));
    });
  }

  Future<void> _initializeProvider() async {
    await ref.read(localRepositoryProvider).initialize();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.backgroundColor,
      body: Center(child: SizedBox(width: 200, height: 200, child: Image.asset("assets/logo/app_logo.png"))),
    );
  }
}
