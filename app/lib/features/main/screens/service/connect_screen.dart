import 'dart:io';

import 'package:app/features/glucose_history/presentation/providers.dart';
import 'package:app/shared/constants/app_values.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../shared/constants/app_colors.dart';
import '../../../../shared/widgets/common_app_bar.dart';

class ConnectScreen extends ConsumerStatefulWidget {
  const ConnectScreen({super.key});

  @override
  ConsumerState<ConnectScreen> createState() => _ConnectScreenState();
}

class _ConnectScreenState extends ConsumerState<ConnectScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(title: "데이터 연동", onBack: () => Navigator.pop(context)),
      backgroundColor: AppColors.backgroundColor,
      body: FutureBuilder(
        future: ref.read(healthControllerProvider.notifier).isAvailable(),
        builder: (context, asyncSnapshot) {
          var isConnected = false;
          if (asyncSnapshot.data != null) {
            isConnected = asyncSnapshot.data!;
          }
          return Column(
            children: [
              if (Platform.isIOS)
                _connectButton(isConnected: isConnected, title: "건강", image: AppValues.appleHealthImage)
              else
                _connectButton(isConnected: isConnected, title: "헬스커넥트", image: AppValues.healthConnectImage),
            ],
          );
        },
      ),
    );
  }

  Widget _connectButton({required bool isConnected, required String title, required String image}) {
    return InkWell(
      onTap: () async {
        if (isConnected) return;
        await ref.read(healthControllerProvider.notifier).requestPermission();
      },
      splashColor: AppColors.backgroundColor,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
        child: Row(
          children: [
            SizedBox(width: 50, height: 50, child: Image.asset(image)),
            const SizedBox(width: 10),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: const TextStyle(fontSize: 14, height: 20 / 14, fontWeight: FontWeight.bold),
                  ),
                  Text(
                    isConnected ? "혈당 데이터 연동 중" : "연동되지 않음 - 탭하여 연결",
                    style: TextStyle(fontSize: 12, height: 20 / 12, color: AppColors.fontGray400Color),
                  ),
                ],
              ),
            ),
            Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Container(
                  width: 8,
                  height: 8,
                  decoration: BoxDecoration(
                    color: isConnected ? AppColors.okColor : AppColors.noColor,
                    shape: BoxShape.circle,
                  ),
                ),
                const SizedBox(width: 6),
                Text(
                  isConnected ? '연동됨' : '연동 안됨',
                  style: TextStyle(
                    fontSize: 14,
                    height: 20 / 14,
                    color: isConnected ? AppColors.okColor : AppColors.noColor,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
