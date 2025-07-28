import 'package:app/features/auth/presentation/providers.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/exceptions/custom_exception.dart';
import '../../../../core/exceptions/exception_message.dart';
import '../../../../shared/constants/app_colors.dart';
import '../../../../shared/constants/app_reg_exp.dart';
import '../../../../shared/widgets/common_button.dart';
import '../../../../shared/widgets/common_text_field.dart';

class EmailPasswordScreen extends ConsumerStatefulWidget {
  final Function onPressed;

  const EmailPasswordScreen({super.key, required this.onPressed});

  @override
  ConsumerState<EmailPasswordScreen> createState() => _EmailPasswordScreenState();
}

class _EmailPasswordScreenState extends ConsumerState<EmailPasswordScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _passwordCheckController = TextEditingController();

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    _passwordCheckController.dispose();
    super.dispose();
  }

  bool get _buttonValue =>
      _emailController.text.isNotEmpty &&
      _passwordController.text.isNotEmpty &&
      _passwordCheckController.text.isNotEmpty;

  void nextPressed() async {
    await emailValidate();
    passwordValidate();
    widget.onPressed(_emailController.text, _passwordController.text);
  }

  Future<void> emailValidate() async {
    if (await ref.read(authControllerProvider.notifier).validateUniqueEmail(_emailController.text)) {
      throw CustomException(ExceptionMessage.emailDuplicated);
    }
    if (!AppRegExp.emailRegExp.hasMatch(_emailController.text)) {
      throw CustomException(ExceptionMessage.wrongEmailRegExp);
    }
  }

  void passwordValidate() {
    if (!AppRegExp.passwordRegExp.hasMatch(_passwordController.text)) {
      throw CustomException(ExceptionMessage.wrongPasswordRegExp);
    }
    if (_passwordController.text != _passwordCheckController.text) {
      throw CustomException(ExceptionMessage.notEqualPasswordAndPasswordCheck);
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
                        "패스워드는 언제든 변경할 수 있어요.",
                        style: TextStyle(fontSize: 14, color: AppColors.fontGray500Color, height: 20 / 14),
                      ),
                    ],
                  ),
                ),
                SizedBox(height: 36),
                Row(
                  children: [
                    SizedBox(width: 80, child: Text("이메일", textAlign: TextAlign.center)),
                    Expanded(
                      child: CommonTextField(
                        controller: _emailController,
                        hintText: "이메일",
                        inputType: TextInputType.emailAddress,
                        onChanged: (String str) => setState(() {}),
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 20),
                Row(
                  children: [
                    SizedBox(width: 80, child: Text("패스워드", textAlign: TextAlign.center)),
                    Expanded(
                      child: CommonTextField(
                        controller: _passwordController,
                        hintText: "패스워드(8자 이상 영문 또는 숫자)",
                        obscureText: true,
                        onChanged: (String str) => setState(() {}),
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 8),
                Row(
                  children: [
                    SizedBox(width: 80),
                    Expanded(
                      child: CommonTextField(
                        controller: _passwordCheckController,
                        hintText: "패스워드 확인",
                        obscureText: true,
                        onChanged: (String str) => setState(() {}),
                      ),
                    ),
                  ],
                ),
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
