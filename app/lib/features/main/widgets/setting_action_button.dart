import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../../shared/constants/app_colors.dart';

class SettingActionButton extends StatefulWidget {
  final String title;
  final VoidCallback onTap;
  final double fontSize;

  const SettingActionButton({super.key, required this.title, required this.onTap, this.fontSize = 16});

  @override
  State<SettingActionButton> createState() => _SettingActionButtonState();
}

class _SettingActionButtonState extends State<SettingActionButton> {
  bool _isPressed = false;

  void _handleTapDown(TapDownDetails details) {
    setState(() => _isPressed = true);
  }

  void _handleTapUp(TapUpDetails details) {
    if (!mounted) return;
    setState(() => _isPressed = false);
  }

  void _handleTapCancel() {
    if (!mounted) return;
    setState(() => _isPressed = false);
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      color: _isPressed ? AppColors.fontGray50Color : AppColors.backgroundColor,
      child: InkWell(
        onTapDown: _handleTapDown,
        onTapUp: _handleTapUp,
        onTapCancel: _handleTapCancel,
        onTap: () {
          HapticFeedback.lightImpact();
          widget.onTap();
        },
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          width: double.infinity,
          height: 50,
          decoration: BoxDecoration(
            border: Border(bottom: BorderSide(color: AppColors.fontGray200Color)),
          ),
          child: Row(
            children: [
              Expanded(
                child: Text(
                  widget.title,
                  style: TextStyle(
                    fontSize: widget.fontSize,
                    height: 20 / widget.fontSize,
                    color: AppColors.fontGray800Color,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              const SizedBox(width: 20),
              Icon(Icons.arrow_forward_ios, size: 16, color: AppColors.fontGray800Color),
            ],
          ),
        ),
      ),
    );
  }
}
