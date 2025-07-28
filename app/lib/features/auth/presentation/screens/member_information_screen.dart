import 'package:flutter/material.dart';

import '../../../../core/exceptions/custom_exception.dart';
import '../../../../core/exceptions/exception_message.dart';
import '../../../../shared/constants/app_colors.dart';
import '../../../../shared/constants/app_reg_exp.dart';
import '../../../../shared/widgets/common_button.dart';
import '../../../../shared/widgets/common_text_field.dart';

class MemberInformationScreen extends StatefulWidget {
  final Function onPressed;

  const MemberInformationScreen({super.key, required this.onPressed});

  @override
  State<MemberInformationScreen> createState() => _MemberInformationScreenState();
}

class _MemberInformationScreenState extends State<MemberInformationScreen> {
  final _nameController = TextEditingController();

  bool get _buttonValue => _nameController.text.isNotEmpty;

  @override
  void dispose() {
    _nameController.dispose();
    super.dispose();
  }

  void nextPressed() {
    nameValidate();
    widget.onPressed(_nameController.text);
  }

  void nameValidate() {
    if (!AppRegExp.nameRegExp.hasMatch(_nameController.text)) {
      throw CustomException(ExceptionMessage.wrongNameRegExp);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Expanded(
          child: SingleChildScrollView(
            physics: ClampingScrollPhysics(),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                SizedBox(height: 12),
                Container(
                  padding: EdgeInsets.symmetric(horizontal: 20),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "가입 정보 입력",
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                          color: AppColors.fontGray900Color,
                          height: 1,
                        ),
                      ),
                      SizedBox(height: 10),
                      Text(
                        "이름은 언제든 변경할 수 있어요.",
                        style: TextStyle(fontSize: 14, color: AppColors.fontGray500Color, height: 20 / 14),
                      ),
                    ],
                  ),
                ),
                SizedBox(height: 36),
                Row(
                  children: [
                    SizedBox(width: 80, child: Text("이름", textAlign: TextAlign.center)),
                    Expanded(
                      child: CommonTextField(
                        controller: _nameController,
                        hintText: "이름",
                        onChanged: (String str) => setState(() {}),
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 20),
              ],
            ),
          ),
        ),
        CommonButton(value: _buttonValue, onTap: nextPressed, title: "다음"),
        SizedBox(height: 20),
      ],
    );
  }
}
