import 'package:flutter/material.dart';

import '../../../shared/constants/app_colors.dart';

class SettingActionButton extends StatelessWidget {
  const SettingActionButton({super.key, required this.title, required this.onTap});

  final String title;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      behavior: HitTestBehavior.opaque,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 20),
        width: double.infinity,
        height: 50,
        decoration: BoxDecoration(
          color: AppColors.backgroundColor,
          border: Border(bottom: BorderSide(color: AppColors.fontGray400Color)),
        ),
        child: Row(
          children: [
            Expanded(
              child: Text(
                title,
                style: TextStyle(
                  fontSize: 16,
                  height: 20 / 16,
                  color: AppColors.fontGray800Color,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            SizedBox(width: 20),
            Icon(Icons.arrow_forward_ios, size: 16, color: AppColors.fontGray800Color),
          ],
        ),
      ),
    );
  }
}
