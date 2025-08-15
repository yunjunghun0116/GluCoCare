import 'package:flutter/material.dart';

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
        padding: const EdgeInsets.all(20),
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
                  onTap: () => Navigator.pop(context, false),
                  child: SizedBox(width: 20, child: Icon(Icons.close, size: 24)),
                ),
              ],
            ),

            Spacer(),
            getButton(title: "네", color: AppColors.fontGray400Color, onTap: () => Navigator.pop(context, true)),
            getButton(title: "아니오", color: AppColors.fontGray800Color, onTap: () => Navigator.pop(context, false)),
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
