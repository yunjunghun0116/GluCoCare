import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/widgets/common_app_bar.dart';
import 'package:app/shared/widgets/common_button.dart';
import 'package:flutter/material.dart';

typedef StringCallback = void Function(String);

class InputScreen extends StatefulWidget {
  final String title;
  final String content;
  final String hintText;
  final StringCallback onTap;
  final String? initialValue;

  const InputScreen({
    super.key,
    required this.title,
    required this.content,
    required this.hintText,
    required this.onTap,
    this.initialValue,
  });

  @override
  State<InputScreen> createState() => _InputScreenState();
}

class _InputScreenState extends State<InputScreen> {
  final _controller = TextEditingController();

  @override
  void initState() {
    super.initState();
    if (widget.initialValue != null) {
      _controller.text = widget.initialValue!;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.whiteColor,
      appBar: CommonAppBar(title: widget.title),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              widget.content,
              style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray800Color),
            ),
            SizedBox(height: 20),
            Container(
              width: double.infinity,
              height: 300,
              padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(color: const Color(0xFFF7F7F7), borderRadius: BorderRadius.circular(12)),
              child: TextField(
                controller: _controller,
                maxLines: null,
                minLines: 10,
                scrollPhysics: ClampingScrollPhysics(),
                decoration: InputDecoration(
                  contentPadding: EdgeInsets.all(10),
                  hintText: widget.hintText,
                  border: InputBorder.none,
                  enabledBorder: InputBorder.none,
                  focusedBorder: InputBorder.none,
                  isDense: true,
                  counterText: '',
                  hintStyle: TextStyle(fontSize: 14, color: AppColors.fontGray400Color, height: 20 / 14),
                ),
                style: TextStyle(fontSize: 14, color: AppColors.fontGray800Color, height: 20 / 14),
              ),
            ),
            SizedBox(height: 20),
            CommonButton(value: true, onTap: () => widget.onTap(_controller.text), title: "입력하기"),
          ],
        ),
      ),
    );
  }
}
