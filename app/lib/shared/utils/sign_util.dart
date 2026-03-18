import 'package:app/features/auth/presentation/screens/sign_in_screen.dart';
import 'package:app/features/main/screens/main_screen.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/data/repositories/local_repository.dart';
import '../../core/data/repositories/secure_repository.dart';

final class SignUtil {
  static Future<void> login(BuildContext context, {required WidgetRef ref, required String token}) async {
    await LocalRepository().save(LocalRepositoryKey.accessToken, token);
    if (!context.mounted) return;
    Navigator.pushAndRemoveUntil(context, MaterialPageRoute(builder: (_) => MainScreen()), (route) => false);
  }

  static Future<void> logout(BuildContext context) async {
    LocalRepository().delete(LocalRepositoryKey.accessToken);
    LocalRepository().delete(LocalRepositoryKey.lateCareRelationId);
    LocalRepository().delete(LocalRepositoryKey.lastSyncDateTime);
    LocalRepository().delete(LocalRepositoryKey.consentAgreed);
    await SecureRepository().deleteRefreshToken();

    if (!context.mounted) return;
    Navigator.pushAndRemoveUntil(context, MaterialPageRoute(builder: (_) => SignInScreen()), (route) => false);
  }
}
