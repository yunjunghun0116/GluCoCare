import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../constants/app_colors.dart';

class CommonButton extends StatefulWidget {
  final bool value; // 활성화 여부
  final VoidCallback onTap;
  final String title;

  const CommonButton({super.key, required this.value, required this.onTap, required this.title});

  @override
  State<CommonButton> createState() => _CommonButtonState();
}

class _CommonButtonState extends State<CommonButton> {
  bool _isPressed = false;

  void _handleTapDown(TapDownDetails details) {
    if (!widget.value) return;
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

  Color getBackgroundColor() {
    if (widget.value) {
      if (_isPressed) {
        return Color.lerp(AppColors.mainColor, Colors.black, 0.08)!;
      }
      return AppColors.mainColor;
    }
    return AppColors.fontGray100Color;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20),
      child: AnimatedScale(
        scale: _isPressed ? 0.97 : 1.0,
        duration: const Duration(milliseconds: 90),
        curve: Curves.easeOut,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 90),
          curve: Curves.easeOut,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(20),
            boxShadow: widget.value
                ? [
                    BoxShadow(
                      color: AppColors.blackColor.withAlpha(_isPressed ? 20 : 40),
                      blurRadius: _isPressed ? 4 : 10,
                      offset: Offset(0, _isPressed ? 2 : 5),
                    ),
                  ]
                : null,
          ),
          child: Material(
            color: getBackgroundColor(),
            borderRadius: BorderRadius.circular(20),
            child: InkWell(
              borderRadius: BorderRadius.circular(20),
              onTapDown: _handleTapDown,
              onTapUp: _handleTapUp,
              onTapCancel: _handleTapCancel,
              onTap: widget.value ? widget.onTap : null,
              child: SizedBox(
                width: double.infinity,
                height: 50,
                child: Center(
                  child: Text(
                    widget.title,
                    style: TextStyle(
                      fontSize: 16,
                      color: widget.value ? AppColors.whiteColor : AppColors.fontGray200Color,
                      fontWeight: FontWeight.bold,
                      height: 20 / 16,
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

// class _CommonButtonState extends State<CommonButton> {
//   @override
//   Widget build(BuildContext context) {
//     return GestureDetector(
//       onTap: widget.onTap,
//       child: Container(
//         margin: EdgeInsets.symmetric(horizontal: 20),
//         child: Container(
//           alignment: Alignment.center,
//           width: double.infinity,
//           height: 50,
//           decoration: BoxDecoration(
//             borderRadius: BorderRadius.circular(20),
//             color: widget.value ? AppColors.mainColor : AppColors.fontGray100Color,
//           ),
//           child: Text(
//             widget.title,
//             style: TextStyle(
//               fontSize: 16,
//               color: widget.value ? AppColors.whiteColor : AppColors.fontGray200Color,
//               fontWeight: FontWeight.bold,
//               height: 20 / 16,
//             ),
//           ),
//         ),
//       ),
//     );
//   }
// }
