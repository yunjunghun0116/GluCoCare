import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../../shared/constants/app_colors.dart';

class CommonSmallButton extends StatelessWidget {
  const CommonSmallButton({
    super.key,
    required this.title,
    required this.onTap,
    this.width = 80,
    this.height = 52,
    this.enabled = true,
  });

  final String title;
  final VoidCallback onTap;
  final double width;
  final double height;
  final bool enabled;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      borderRadius: BorderRadius.circular(15),
      child: Ink(
        width: width,
        height: height,
        decoration: BoxDecoration(
          color: enabled ? AppColors.mainColor : AppColors.fontGray200Color,
          borderRadius: BorderRadius.circular(15),
        ),
        child: InkWell(
          borderRadius: BorderRadius.circular(15),
          onTap: enabled
              ? () {
                  HapticFeedback.lightImpact();
                  onTap();
                }
              : null,
          child: Center(
            child: Text(
              title,
              style: TextStyle(fontSize: 16, height: 20 / 16, color: AppColors.whiteColor, fontWeight: FontWeight.bold),
            ),
          ),
        ),
      ),
    );
  }
}
