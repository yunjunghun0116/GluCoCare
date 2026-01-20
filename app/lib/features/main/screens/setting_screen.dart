import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/core/data/repositories/secure_repository.dart';
import 'package:app/features/auth/presentation/screens/sign_in_screen.dart';
import 'package:app/features/member/presentation/providers.dart';
import 'package:app/features/member/presentation/screens/member_screen.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../patient/presentation/screens/patient_screen.dart';

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

  void logOut() async {
    LocalRepository().delete(LocalRepositoryKey.accessToken);
    await SecureRepository().deleteRefreshToken();
    if (!mounted) return;
    Navigator.pushAndRemoveUntil(context, MaterialPageRoute(builder: (_) => SignInScreen()), (route) => false);
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
              onTap: logOut,
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
        getActiveButton(
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
        getActiveButton(
          title: "혈당 관리가 필요한가요?",
          onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => PatientScreen())),
        ),
      ],
    );
  }

  Widget getActiveButton({required String title, required VoidCallback onTap}) {
    return GestureDetector(
      onTap: onTap,
      behavior: HitTestBehavior.opaque,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 20),
        width: double.infinity,
        height: 50,
        decoration: BoxDecoration(
          color: AppColors.backgroundColor,
          border: Border(bottom: BorderSide(color: AppColors.fontGray400Color)),
        ),
        child: Row(
          children: [
            Expanded(
              child: Text(
                title,
                style: TextStyle(
                  fontSize: 16,
                  height: 20 / 16,
                  color: AppColors.fontGray800Color,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            SizedBox(width: 20),
            Icon(Icons.arrow_forward_ios, size: 16, color: AppColors.fontGray800Color),
          ],
        ),
      ),
    );
  }
}
