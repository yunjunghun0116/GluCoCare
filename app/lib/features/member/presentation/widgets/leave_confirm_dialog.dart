import 'package:app/shared/widgets/common_text_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../../../shared/constants/app_colors.dart';

class LeaveConfirmDialog extends StatefulWidget {
  const LeaveConfirmDialog({super.key});

  @override
  State<LeaveConfirmDialog> createState() => _LeaveConfirmDialogState();
}

class _LeaveConfirmDialogState extends State<LeaveConfirmDialog> {
  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      child: Container(
        height: 200,
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                SizedBox(width: 20),
                Expanded(
                  child: Center(
                    child: Text(
                      "정말 탈퇴 하시겠습니까?",
                      style: TextStyle(
                        fontSize: 16,
                        color: AppColors.fontGray800Color,
                        fontWeight: FontWeight.bold,
                        height: 20 / 16,
                      ),
                    ),
                  ),
                ),
                GestureDetector(
                  behavior: HitTestBehavior.opaque,
                  onTap: () {
                    HapticFeedback.lightImpact();
                    Navigator.pop(context, false);
                  },
                  child: SizedBox(width: 20, child: Icon(Icons.close, size: 24)),
                ),
              ],
            ),
            SizedBox(height: 10),
            Center(
              child: Text(
                "탈퇴 시 혈당 기록과 미션 달성 내역,\n포인트가 모두 삭제되며 복구할 수 없어요.",
                style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray400Color),
                textAlign: TextAlign.center,
              ),
            ),
            Spacer(),
            Center(
              child: CommonTextButton(text: "네", onTap: () => Navigator.pop(context, true)),
            ),
            Center(
              child: CommonTextButton(
                text: "아니오",
                onTap: () => Navigator.pop(context, false),
                color: AppColors.mainColor,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget getButton({required String title, required Color color, required Function() onTap}) {
    return GestureDetector(
      onTap: onTap,
      behavior: HitTestBehavior.opaque,
      child: Container(
        margin: const EdgeInsets.all(16),
        width: double.infinity,
        alignment: Alignment.center,
        child: Text(
          title,
          style: TextStyle(fontSize: 14, height: 20 / 14, fontWeight: FontWeight.bold, color: color),
        ),
      ),
    );
  }
}
