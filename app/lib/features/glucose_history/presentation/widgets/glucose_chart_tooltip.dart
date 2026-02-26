import 'package:flutter/material.dart';

import '../../../../shared/constants/app_colors.dart';
import '../../data/models/glucose_history_response.dart';

class GlucoseChartTooltip extends StatelessWidget {
  final double xPosition;
  final GlucoseHistoryResponse record;

  const GlucoseChartTooltip({super.key, required this.xPosition, required this.record});

  @override
  Widget build(BuildContext context) {
    var labelWidth = 100.0;
    var left = xPosition + 10 - labelWidth / 2;
    return Positioned(
      top: 12,
      left: left,
      width: labelWidth,
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 10),
        child: Column(
          children: [
            Text(
              "${record.sgv}",
              textAlign: TextAlign.center,
              style: const TextStyle(color: AppColors.mainColor, fontWeight: FontWeight.bold, fontSize: 20, height: 1),
            ),
            Text(
              "${record.dateTime.hour < 12 ? "오전" : "오후"} ${record.dateTime.hour > 12 ? record.dateTime.hour - 12 : record.dateTime.hour}:${record.dateTime.minute.toString().padLeft(2, "0")}",
              textAlign: TextAlign.center,
              style: const TextStyle(color: AppColors.mainColor, fontWeight: FontWeight.bold, fontSize: 13),
            ),
          ],
        ),
      ),
    );
  }
}
