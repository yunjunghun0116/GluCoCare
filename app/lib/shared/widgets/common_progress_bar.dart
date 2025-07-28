import 'package:app/shared/constants/app_colors.dart';
import 'package:flutter/material.dart';

class CommonProgressBar extends StatelessWidget {
  final double progress;

  const CommonProgressBar({super.key, required this.progress});

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        var width = constraints.maxWidth;
        var multiples = progress.clamp(0, 1);
        var activeWidth = multiples * width;
        return Stack(
          children: [
            Container(
              width: width,
              height: 20,
              decoration: BoxDecoration(color: AppColors.subColor1, borderRadius: BorderRadius.circular(20)),
            ),
            Container(
              width: activeWidth,
              height: 20,
              decoration: BoxDecoration(color: AppColors.mainColor, borderRadius: BorderRadius.circular(20)),
            ),
            Container(
              alignment: Alignment.center,
              width: width,
              height: 20,
              child: Text(
                "${(multiples * 100).toInt()}%",
                style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray800Color),
              ),
            ),
          ],
        );
      },
    );
  }
}
