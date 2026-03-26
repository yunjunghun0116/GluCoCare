import 'package:app/features/mission/data/models/daily_mission_response.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:flutter/material.dart';

class MissionButton extends StatelessWidget {
  final VoidCallback onTap;
  final DailyMissionResponse missionResponse;

  const MissionButton({super.key, required this.onTap, required this.missionResponse});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: EdgeInsets.symmetric(horizontal: 20),
        child: Container(
          alignment: Alignment.center,
          width: double.infinity,
          height: 50,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(20),
            color: missionResponse.isCompleted
                ? AppColors.missionCompletedBackgroundColor
                : missionResponse.canComplete
                ? AppColors.missionActiveBackgroundColor
                : AppColors.missionDisabledBackgroundColor,
            border: Border.all(
              color: missionResponse.isCompleted
                  ? AppColors.missionCompletedBorderColor
                  : missionResponse.canComplete
                  ? AppColors.missionActiveBorderColor
                  : AppColors.missionDisabledBorderColor,
            ),
          ),
          child: Text(
            missionResponse.isCompleted
                ? "미션을 완료했어요"
                : missionResponse.canComplete
                ? "미션 완료하기"
                : "아직 달성 조건이 부족해요",
            style: TextStyle(
              color: missionResponse.isCompleted
                  ? AppColors.missionCompletedTextColor
                  : missionResponse.canComplete
                  ? AppColors.missionActiveTextColor
                  : AppColors.missionDisabledTextColor,
            ),
          ),
        ),
      ),
    );
  }
}
