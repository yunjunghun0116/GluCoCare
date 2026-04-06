import 'package:app/features/mission/data/models/daily_mission_response.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class MissionButton extends StatefulWidget {
  final VoidCallback onTap;
  final DailyMissionResponse missionResponse;

  const MissionButton({super.key, required this.onTap, required this.missionResponse});

  @override
  State<MissionButton> createState() => _MissionButtonState();
}

class _MissionButtonState extends State<MissionButton> {
  bool _isPressed = false;

  Color get backgroundColor {
    if (widget.missionResponse.isCompleted) return AppColors.missionCompletedBackgroundColor;
    if (widget.missionResponse.canComplete) return AppColors.missionActiveBackgroundColor;
    return AppColors.missionDisabledBackgroundColor;
  }

  Color get borderColor {
    if (widget.missionResponse.isCompleted) return AppColors.missionCompletedBorderColor;
    if (widget.missionResponse.canComplete) return AppColors.missionActiveBorderColor;
    return AppColors.missionDisabledBorderColor;
  }

  String get missionText {
    if (widget.missionResponse.isCompleted) return "미션을 완료했어요";
    if (widget.missionResponse.canComplete) return "미션 완료하기";
    return "아직 달성 조건이 부족해요";
  }

  Color get missionTextColor {
    if (widget.missionResponse.isCompleted) return AppColors.missionCompletedTextColor;
    if (widget.missionResponse.canComplete) return AppColors.missionActiveTextColor;
    return AppColors.missionDisabledTextColor;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20),
      child: AnimatedScale(
        scale: _isPressed ? 0.97 : 1.0,
        duration: const Duration(milliseconds: 100),
        curve: Curves.easeOut,
        child: Material(
          color: backgroundColor,
          borderRadius: BorderRadius.circular(20),
          child: InkWell(
            borderRadius: BorderRadius.circular(20),
            onTap: widget.missionResponse.canComplete
                ? () {
                    HapticFeedback.lightImpact();
                    widget.onTap();
                  }
                : null,
            onTapDown: (_) {
              if (widget.missionResponse.canComplete) {
                setState(() => _isPressed = true);
              }
            },
            onTapUp: (_) {
              setState(() => _isPressed = false);
            },
            onTapCancel: () {
              setState(() => _isPressed = false);
            },
            child: Container(
              width: double.infinity,
              height: 50,
              alignment: Alignment.center,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: borderColor),
              ),
              child: Text(
                missionText,
                style: TextStyle(color: missionTextColor, fontWeight: FontWeight.bold, fontSize: 14),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
