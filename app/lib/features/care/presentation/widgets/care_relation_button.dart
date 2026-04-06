import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../../../shared/constants/app_colors.dart';
import '../../data/models/care_relation_response.dart';

class CareRelationButton extends StatefulWidget {
  final CareRelationResponse careRelation;
  final VoidCallback onTap;

  const CareRelationButton({super.key, required this.careRelation, required this.onTap});

  @override
  State<CareRelationButton> createState() => _CareRelationButtonState();
}

class _CareRelationButtonState extends State<CareRelationButton> {
  bool _isPressed = false;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: _isPressed ? AppColors.fontGray50Color : AppColors.backgroundColor,
      child: InkWell(
        onTapDown: (_) => setState(() => _isPressed = true),
        onTapUp: (_) => setState(() => _isPressed = false),
        onTapCancel: () => setState(() => _isPressed = false),
        onTap: () {
          HapticFeedback.lightImpact();
          widget.onTap();
        },
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          decoration: BoxDecoration(
            border: Border(bottom: BorderSide(color: AppColors.mainColor)),
          ),
          child: Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      "정보",
                      style: TextStyle(
                        fontSize: 14,
                        height: 20 / 14,
                        color: AppColors.subColor3,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    SizedBox(height: 4),
                    Row(
                      children: [
                        Text(
                          "이름 : ${widget.careRelation.patientName}",
                          style: TextStyle(
                            fontSize: 16,
                            height: 20 / 16,
                            color: AppColors.subColor2,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        SizedBox(width: 6),
                        Text(
                          "(ID : ${widget.careRelation.patientId})",
                          style: TextStyle(
                            fontSize: 14,
                            height: 20 / 14,
                            color: AppColors.subColor2,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                    SizedBox(height: 4),
                  ],
                ),
              ),
              Icon(Icons.arrow_forward_ios, size: 16, color: AppColors.mainColor),
            ],
          ),
        ),
      ),
    );
  }
}
