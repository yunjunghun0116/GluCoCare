import 'package:app/core/providers.dart';
import 'package:app/features/auth/presentation/providers.dart';
import 'package:app/features/auth/presentation/screens/sign_in_screen.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:app/shared/utils/sign_util.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/exceptions/custom_exception.dart';
import '../../../core/exceptions/exception_message.dart';
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
      var token = await _validateSavedAccessToken();
      if (!mounted) return;
      if (token != null) {
        SignUtil.login(context, ref: ref, token: token);
        return;
      }
      Navigator.push(context, MaterialPageRoute(builder: (_) => SignInScreen()));
    });
  }

  Future<void> _initializeProvider() async {
    await ref.read(localRepositoryProvider).initialize();
  }

  Future<String?> _validateSavedAccessToken() async {
    try {
      var savedToken = ref.read(localRepositoryProvider).read<String>(LocalRepositoryKey.accessToken);
      var token = await ref.read(authControllerProvider.notifier).autoLogin(savedToken);
      if (token == null) throw CustomException(ExceptionMessage.badRequest);
      return token;
    } on CustomException catch (e) {
      return null;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.backgroundColor,
      body: Center(child: SizedBox(width: 200, height: 200, child: Image.asset("assets/logo/app_logo.png"))),
    );
  }
}
