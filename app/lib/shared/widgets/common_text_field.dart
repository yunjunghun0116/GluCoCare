import 'package:flutter/material.dart';

import '../constants/app_colors.dart';

class CommonTextField extends StatelessWidget {
  final TextEditingController controller;
  final String hintText;
  final int? maxLength;
  final TextInputType? inputType;
  final bool obscureText;
  final Function(String)? onChanged;

  const CommonTextField({
    super.key,
    required this.controller,
    required this.hintText,
    this.maxLength,
    this.inputType,
    this.obscureText = false,
    this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      alignment: Alignment.center,
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.symmetric(horizontal: 18),
      width: double.infinity,
      height: 52,
      decoration: BoxDecoration(color: AppColors.fontGray50Color, borderRadius: BorderRadius.circular(15)),
      child: TextField(
        controller: controller,
        keyboardType: inputType,
        maxLength: maxLength,
        obscureText: obscureText,
        onChanged: (str) {
          if (onChanged == null) {
            return;
          }
          onChanged!(str);
        },
        decoration: InputDecoration(
          border: InputBorder.none,
          enabledBorder: InputBorder.none,
          focusedBorder: InputBorder.none,
          isDense: true,
          counterText: '',
          hintText: hintText,
          hintStyle: TextStyle(fontSize: 14, color: AppColors.fontGray400Color, height: 20 / 14),
        ),
        style: TextStyle(fontSize: 14, color: AppColors.fontGray800Color, height: 20 / 14),
      ),
    );
  }
}
