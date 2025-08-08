import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/features/auth/presentation/providers.dart';
import 'package:app/features/auth/presentation/screens/sign_in_screen.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:app/shared/utils/sign_util.dart';
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
    initialize();
  }

  void initialize() async {
    try {
      await Future.delayed(Duration(seconds: 1), () async => await LocalRepository().initialize());
      var success = await _validateSavedAccessToken();
      if (!mounted) return;
      if (!success) throw CustomException(ExceptionMessage.invalidAuthentication);
      var accessToken = LocalRepository().read<String>(LocalRepositoryKey.accessToken);
      SignUtil.login(context, ref: ref, token: accessToken);
      return;
    } on CustomException catch (e) {
      Navigator.push(context, MaterialPageRoute(builder: (_) => SignInScreen()));
    }
  }

  Future<bool> _validateSavedAccessToken() async {
    try {
      var accessToken = LocalRepository().read<String>(LocalRepositoryKey.accessToken);
      var success = await ref.read(authControllerProvider.notifier).autoLogin(accessToken);
      return success;
    } catch (e) {
      return false;
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
