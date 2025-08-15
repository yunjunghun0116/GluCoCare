import 'package:app/features/auth/presentation/screens/sign_in_screen.dart';
import 'package:app/features/member/presentation/providers.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/utils/local_util.dart';
import 'package:app/shared/widgets/common_app_bar.dart';
import 'package:app/shared/widgets/common_text_field.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../widgets/leave_confirm_dialog.dart';

class MemberScreen extends ConsumerStatefulWidget {
  final String name;

  const MemberScreen({required this.name, super.key});

  @override
  ConsumerState<MemberScreen> createState() => _MemberScreenState();
}

class _MemberScreenState extends ConsumerState<MemberScreen> {
  final _nameController = TextEditingController();
  bool isNameChanged = false;

  @override
  void initState() {
    super.initState();
    _nameController.text = widget.name;
  }

  void updateName() async {
    isNameChanged = await ref.read(memberControllerProvider.notifier).updateName(_nameController.text);
    if (!mounted) return;
    FocusScope.of(context).unfocus();
    LocalUtil.showMessage(context, message: "닉네임이 변경되었습니다.");
  }

  void deleteMember() async {
    var result = await showDialog<bool?>(context: context, builder: (context) => LeaveConfirmDialog());
    if (result == null) return;
    if (!result) return;
    await ref.read(memberControllerProvider.notifier).delete();
    if (!mounted) return;
    Navigator.pushAndRemoveUntil(context, MaterialPageRoute(builder: (_) => SignInScreen()), (route) => false);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(title: "개인정보 수정", onBack: () => Navigator.pop(context, isNameChanged)),
      backgroundColor: AppColors.backgroundColor,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: CommonTextField(controller: _nameController, hintText: "닉네임을 입력해 주세요."),
              ),
              GestureDetector(
                onTap: () => updateName(),
                child: Container(
                  width: 80,
                  height: 52,
                  alignment: Alignment.center,
                  decoration: BoxDecoration(color: AppColors.mainColor, borderRadius: BorderRadius.circular(15)),
                  child: Text(
                    "변경",
                    style: TextStyle(
                      fontSize: 16,
                      height: 20 / 16,
                      color: AppColors.whiteColor,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ),
              SizedBox(width: 20),
            ],
          ),
          Spacer(),
          Center(
            child: GestureDetector(
              onTap: () => deleteMember(),
              child: Text(
                "회원탈퇴",
                style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray800Color),
              ),
            ),
          ),
          SizedBox(height: 100),
        ],
      ),
    );
  }
}
