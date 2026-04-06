import 'package:app/features/care/data/models/care_relation_response.dart';
import 'package:app/features/glucose_warning/data/models/glucose_alert_policy_response.dart';
import 'package:app/features/glucose_warning/presentation/providers.dart';
import 'package:app/features/glucose_warning/presentation/screens/glucose_value_select_screen.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/widgets/common_app_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

class GlucoseWarningScreen extends ConsumerStatefulWidget {
  final CareRelationResponse careGiver;
  final GlucoseAlertPolicyResponse glucoseAlertPolicy;

  const GlucoseWarningScreen({super.key, required this.careGiver, required this.glucoseAlertPolicy});

  @override
  ConsumerState<GlucoseWarningScreen> createState() => _GlucoseWarningScreenState();
}

class _GlucoseWarningScreenState extends ConsumerState<GlucoseWarningScreen> {
  late GlucoseAlertPolicyResponse _glucoseAlertPolicy;

  @override
  void initState() {
    super.initState();
    setState(() => _glucoseAlertPolicy = widget.glucoseAlertPolicy);
  }

  Future<void> setGlucoseAlertPolicy() async {
    var result = await ref.read(glucoseWarningControllerProvider.notifier).getGlucoseAlertPolicy(widget.careGiver);
    setState(() => _glucoseAlertPolicy = result);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(title: "혈당 위험 구간 알림 설정"),
      backgroundColor: AppColors.backgroundColor,
      body: Column(
        children: [
          getGlucoseAlertContainer(
            title: "고혈당 매우 위험",
            value: _glucoseAlertPolicy.veryHighRiskValue,
            onTap: () async {
              var result = await Navigator.push<int?>(
                context,
                MaterialPageRoute(
                  builder: (_) =>
                      GlucoseValueSelectScreen(title: "고혈당 매우 위험", minValue: 160, maxValue: 200, unit: "mg/dL 이상"),
                ),
              );
              if (result == null) return;
              var success = await ref
                  .read(glucoseWarningControllerProvider.notifier)
                  .updateVeryHighRisk(widget.glucoseAlertPolicy, result);
              if (success) {
                setGlucoseAlertPolicy();
              }
            },
          ),
          getGlucoseAlertContainer(
            title: "고혈당 위험",
            value: _glucoseAlertPolicy.highRiskValue,
            onTap: () async {
              var result = await Navigator.push<int?>(
                context,
                MaterialPageRoute(
                  builder: (_) =>
                      GlucoseValueSelectScreen(title: "고혈당 위험", minValue: 120, maxValue: 160, unit: "mg/dL 이상"),
                ),
              );
              if (result == null) return;
              var success = await ref
                  .read(glucoseWarningControllerProvider.notifier)
                  .updateHighRisk(widget.glucoseAlertPolicy, result);
              if (success) {
                setGlucoseAlertPolicy();
              }
            },
          ),
          getGlucoseAlertContainer(
            title: "저혈당 위험",
            value: _glucoseAlertPolicy.lowRiskValue,
            onTap: () async {
              var result = await Navigator.push<int?>(
                context,
                MaterialPageRoute(
                  builder: (_) =>
                      GlucoseValueSelectScreen(title: "저혈당 위험", minValue: 50, maxValue: 80, unit: "mg/dL 이하"),
                ),
              );
              if (result == null) return;
              var success = await ref
                  .read(glucoseWarningControllerProvider.notifier)
                  .updateLowRisk(widget.glucoseAlertPolicy, result);
              if (success) {
                setGlucoseAlertPolicy();
              }
            },
          ),
        ],
      ),
    );
  }

  Widget getGlucoseAlertContainer({required String title, required int value, required VoidCallback onTap}) {
    return GestureDetector(
      onTap: () {
        HapticFeedback.lightImpact();
        onTap();
      },
      behavior: HitTestBehavior.opaque,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
        child: Row(
          children: [
            Text(
              "$title 알림 설정 : ",
              style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray800Color),
            ),
            Text(
              "$value mg/dL",
              style: TextStyle(fontSize: 16, height: 20 / 16, color: AppColors.mainColor, fontWeight: FontWeight.bold),
            ),
            Spacer(),
            Icon(Icons.arrow_forward_ios, size: 16, color: AppColors.fontGray600Color),
          ],
        ),
      ),
    );
  }
}
