import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../constants/app_colors.dart';

class CommonTextButton extends StatefulWidget {
  final String text;
  final VoidCallback onTap;
  final Color color;
  final FontWeight fontWeight;
  const CommonTextButton({
    super.key,
    required this.text,
    required this.onTap,
    this.color = AppColors.fontGray400Color,
    this.fontWeight = FontWeight.normal,
  });

  @override
  State<CommonTextButton> createState() => _CommonTextButtonState();
}

class _CommonTextButtonState extends State<CommonTextButton> {
  bool _isPressed = false;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTapDown: (_) => setState(() => _isPressed = true),
      onTapUp: (_) => setState(() => _isPressed = false),
      onTapCancel: () => setState(() => _isPressed = false),
      onTap: () {
        HapticFeedback.lightImpact();
        widget.onTap();
      },
      behavior: HitTestBehavior.opaque,
      child: AnimatedOpacity(
        opacity: _isPressed ? 0.5 : 1.0,
        duration: Duration(milliseconds: 100),
        child: Padding(
          padding: EdgeInsets.all(8), // 터치 영역 확대
          child: Text(
            widget.text,
            style: TextStyle(fontSize: 14, height: 20 / 14, color: widget.color, fontWeight: widget.fontWeight),
          ),
        ),
      ),
    );
  }
}
