import 'package:flutter/material.dart';

import '../constants/app_colors.dart';

class CommonLoadingIndicator extends StatelessWidget {
  final double size;
  final Color? color;
  final double? strokeWidth;

  const CommonLoadingIndicator({super.key, this.size = 24, this.color, this.strokeWidth});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: size,
      height: size,
      child: CircularProgressIndicator(strokeWidth: strokeWidth ?? (size / 12), color: color ?? AppColors.mainColor),
    );
  }
}
