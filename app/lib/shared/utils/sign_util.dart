import 'package:app/core/providers.dart';
import 'package:app/features/main/screens/main_screen.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

final class SignUtil {
  static void login(BuildContext context, {required WidgetRef ref, required String token}) {
    ref.read(localRepositoryProvider).save(LocalRepositoryKey.accessToken, token);
    Navigator.pushAndRemoveUntil(context, MaterialPageRoute(builder: (_) => MainScreen()), (route) => false);
  }
}
