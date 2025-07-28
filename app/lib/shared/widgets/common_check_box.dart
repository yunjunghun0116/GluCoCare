import 'package:app/shared/constants/app_colors.dart';
import 'package:flutter/material.dart';

class CommonCheckBox extends StatefulWidget {
  final bool value;
  final Function onTap;
  final double size;

  const CommonCheckBox({super.key, required this.value, required this.onTap, required this.size});

  @override
  State<CommonCheckBox> createState() => _CommonCheckBoxState();
}

class _CommonCheckBoxState extends State<CommonCheckBox> {
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => widget.onTap(!widget.value),
      child: Container(
        alignment: Alignment.center,
        width: widget.size,
        height: widget.size,
        decoration: BoxDecoration(
          color: AppColors.whiteColor,
          border: Border.all(color: widget.value ? AppColors.mainColor : AppColors.fontGray200Color),
        ),
        child: widget.value ? Icon(Icons.check, color: AppColors.mainColor, size: widget.size - 4) : null,
      ),
    );
  }
}
