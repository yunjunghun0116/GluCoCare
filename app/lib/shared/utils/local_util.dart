import 'package:another_flushbar/flushbar.dart';
import 'package:flutter/material.dart';

import '../constants/app_colors.dart';

final class LocalUtil {
  static void showMessage(BuildContext context, {required String message}) {
    var flushBar = Flushbar(
      backgroundColor: AppColors.flushBarBackgroundColor,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 5),
      flushbarPosition: FlushbarPosition.TOP,
      messageText: Text(
        message,
        textAlign: TextAlign.center,
        style: TextStyle(color: AppColors.whiteColor, fontSize: 14),
      ),
      duration: const Duration(seconds: 2),
    );
    Future.delayed(Duration.zero, () {
      try {
        if (!context.mounted) return;
        var navigator = Navigator.maybeOf(context);
        if (navigator == null) return;

        flushBar.show(context);
      } catch (_) {
        // 에러메시지 무시 ignore
      }
    });
  }
}
