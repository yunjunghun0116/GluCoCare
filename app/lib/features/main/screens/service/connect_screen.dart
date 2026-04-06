import 'dart:io';

import 'package:app/features/glucose_history/presentation/providers.dart';
import 'package:app/features/main/widgets/health_connect_button.dart';
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
        future: ref.read(healthControllerProvider.notifier).isAuthorized(),
        builder: (context, asyncSnapshot) {
          var isConnected = false;
          if (asyncSnapshot.data != null) {
            isConnected = asyncSnapshot.data!;
          }
          if (Platform.isIOS) {
            return Column(
              children: [ConnectButton(isConnected: isConnected, title: "건강", image: AppValues.appleHealthImage)],
            );
          }
          return Column(
            children: [ConnectButton(isConnected: isConnected, title: "헬스커넥트", image: AppValues.healthConnectImage)],
          );
        },
      ),
    );
  }
}
