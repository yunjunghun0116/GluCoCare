import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../../../../shared/constants/app_colors.dart';
import '../../../point/presentation/widgets/point_container.dart';

class MissionStatus extends StatelessWidget {
  final int completedCount;
  final int totalCount;
  const MissionStatus({super.key, required this.completedCount, required this.totalCount});

  String get todayText {
    return DateFormat("M월 d일 EEEE", "ko").format(DateTime.now());
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                todayText,
                style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray400Color),
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    "오늘의 미션",
                    style: TextStyle(fontSize: 16, height: 20 / 16, fontWeight: FontWeight.bold),
                  ),
                  Container(
                    padding: EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                    decoration: BoxDecoration(
                      color: AppColors.mainColor.withValues(alpha: 0.1),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text(
                      "$completedCount / $totalCount 완료",
                      style: TextStyle(
                        fontSize: 13,
                        height: 20 / 13,
                        fontWeight: FontWeight.bold,
                        color: AppColors.mainColor,
                      ),
                    ),
                  ),
                ],
              ),
              SizedBox(height: 12),
              LinearProgressIndicator(
                value: totalCount == 0 ? 0.0 : completedCount / totalCount,
                minHeight: 10,
                backgroundColor: AppColors.fontGray100Color,
                valueColor: AlwaysStoppedAnimation(AppColors.mainColor),
                borderRadius: BorderRadius.circular(10),
              ),
            ],
          ),
        ),
        SizedBox(height: 10),
        Padding(padding: const EdgeInsets.symmetric(horizontal: 20), child: PointContainer()),
      ],
    );
  }
}
