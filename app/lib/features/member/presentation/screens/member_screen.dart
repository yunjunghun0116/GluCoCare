import 'package:app/features/auth/presentation/screens/sign_in_screen.dart';
import 'package:app/features/member/presentation/providers.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/utils/local_util.dart';
import 'package:app/shared/widgets/common_app_bar.dart';
import 'package:app/shared/widgets/common_small_button.dart';
import 'package:app/shared/widgets/common_text_button.dart';
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
              CommonSmallButton(title: "변경", onTap: updateName),
              SizedBox(width: 20),
            ],
          ),
          Spacer(),
          Center(
            child: CommonTextButton(text: "회원탈퇴", onTap: () => deleteMember()),
          ),
          SizedBox(height: 100),
        ],
      ),
    );
  }
}
