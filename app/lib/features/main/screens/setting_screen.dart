import 'package:app/features/main/screens/service/connect_screen.dart';
import 'package:app/features/main/screens/service/service_screen.dart';
import 'package:app/features/member/presentation/providers.dart';
import 'package:app/features/member/presentation/screens/member_screen.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/utils/sign_util.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../patient/presentation/screens/patient_screen.dart';
import '../widgets/setting_action_button.dart';

class SettingScreen extends ConsumerStatefulWidget {
  const SettingScreen({super.key});

  @override
  ConsumerState<SettingScreen> createState() => _SettingScreenState();
}

class _SettingScreenState extends ConsumerState<SettingScreen> {
  String name = "";

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      initializeName();
    });
  }

  void initializeName() async {
    var result = await ref.read(memberControllerProvider.notifier).readName();
    if (result == null) return;
    setState(() => name = result);
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            SizedBox(width: 20),
            RichText(
              text: TextSpan(
                style: TextStyle(fontSize: 16, height: 20 / 16, color: AppColors.mainColor),
                children: [
                  TextSpan(
                    text: name,
                    style: TextStyle(fontWeight: FontWeight.bold),
                  ),
                  TextSpan(text: " 님, 안녕하세요"),
                ],
              ),
            ),
            Spacer(),
            GestureDetector(
              onTap: () => SignUtil.logout(context),
              child: Text(
                "로그아웃",
                style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray600Color),
              ),
            ),
            SizedBox(width: 20),
          ],
        ),
        SizedBox(height: 20),
        Container(width: double.infinity, height: 5, color: AppColors.subColor4),
        SettingActionButton(
          title: "개인정보 수정하기",
          onTap: () async {
            var result = await Navigator.push<bool?>(
              context,
              MaterialPageRoute(builder: (_) => MemberScreen(name: name)),
            );
            if (result == null) return;
            if (!result) return;
            initializeName();
          },
        ),
        SettingActionButton(
          title: "혈당 관리가 필요한가요?",
          onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => PatientScreen())),
        ),
        SettingActionButton(
          title: "서비스 정보",
          onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => ServiceScreen())),
        ),
        SettingActionButton(
          title: "데이터 연동",
          onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => ConnectScreen())),
        ),
      ],
    );
  }
}
