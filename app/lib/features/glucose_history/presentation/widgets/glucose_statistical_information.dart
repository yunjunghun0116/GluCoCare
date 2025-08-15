import 'package:app/features/glucose_history/data/models/glucose_history_response.dart';
import 'package:app/features/glucose_warning/presentation/providers.dart';
import 'package:app/features/glucose_warning/presentation/screens/glucose_warning_screen.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/utils/glucose_util.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';

import '../../../care/data/models/care_giver_response.dart';

class GlucoseStatisticalInformation extends ConsumerStatefulWidget {
  final CareGiverResponse careGiver;
  final List<GlucoseHistoryResponse> records;

  const GlucoseStatisticalInformation({super.key, required this.careGiver, required this.records});

  @override
  ConsumerState<GlucoseStatisticalInformation> createState() => _GlucoseStatisticalInformationState();
}

class _GlucoseStatisticalInformationState extends ConsumerState<GlucoseStatisticalInformation> {
  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Builder(
          builder: (context) {
            var todayGlucose = GlucoseUtil.getLastDateGlucoseData(widget.records);
            if (todayGlucose == null) return Container();
            var lastDate = widget.records.last.dateTime;
            var lastDateString = DateFormat("MM월 dd일").format(lastDate);

            return Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    "통계 정보($lastDateString)",
                    style: TextStyle(
                      fontSize: 16,
                      height: 20 / 16,
                      color: AppColors.mainColor,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  SizedBox(height: 10),
                  Text("최고 혈당 수치 : ${todayGlucose.max} mg/dL"),
                  Text("최저 혈당 수치 : ${todayGlucose.min} mg/dL"),
                  Text("평균 혈당 수치 : ${todayGlucose.avg.toStringAsFixed(2)} mg/dL"),
                ],
              ),
            );
          },
        ),
        SizedBox(height: 50),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
          child: Text(
            "추가 설정",
            style: TextStyle(fontSize: 16, height: 20 / 16, color: AppColors.mainColor, fontWeight: FontWeight.bold),
          ),
        ),
        Container(width: double.infinity, height: 1, color: AppColors.fontGray400Color),
        getActiveButton(
          title: "혈당 위험 구간 알림 설정하기",
          onTap: () async {
            var glucoseAlertPolicy = await ref
                .read(glucoseWarningControllerProvider.notifier)
                .getGlucoseAlertPolicy(widget.careGiver);
            if (!mounted) return;
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (_) =>
                    GlucoseWarningScreen(careGiver: widget.careGiver, glucoseAlertPolicy: glucoseAlertPolicy),
              ),
            );
          },
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
                  fontSize: 14,
                  height: 20 / 14,
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
