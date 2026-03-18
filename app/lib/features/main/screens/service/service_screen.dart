import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/shared/constants/app_values.dart';
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../../../shared/constants/app_colors.dart';
import '../../../../shared/widgets/common_app_bar.dart';
import '../../widgets/setting_action_button.dart';

class ServiceScreen extends StatelessWidget {
  const ServiceScreen({super.key});

  Future<void> _launchUrl(String url) async {
    var uri = Uri.parse(url);
    if (!await launchUrl(uri)) {
      throw CustomException(ExceptionMessage.internalServerError);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(title: "서비스 정보", onBack: () => Navigator.pop(context)),
      backgroundColor: AppColors.backgroundColor,
      body: Column(
        children: [
          SettingActionButton(title: "서비스 소개", onTap: () => _launchUrl(AppValues.serviceUrl)),
          SettingActionButton(title: "개인정보 처리방침", onTap: () => _launchUrl(AppValues.privacyUrl)),
          SettingActionButton(title: "의료정보", onTap: () => _launchUrl(AppValues.medicalUrl)),
        ],
      ),
    );
  }
}
