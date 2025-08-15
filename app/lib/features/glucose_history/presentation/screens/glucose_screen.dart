import 'package:app/features/glucose_history/presentation/widgets/glucose_chart.dart';
import 'package:app/features/glucose_history/presentation/widgets/glucose_statistical_information.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../shared/constants/app_colors.dart';
import '../../../care/data/models/care_giver_response.dart';
import '../../data/models/glucose_history_response.dart';
import '../providers.dart';

class GlucoseScreen extends ConsumerStatefulWidget {
  final CareGiverResponse careGiver;

  const GlucoseScreen({super.key, required this.careGiver});

  @override
  ConsumerState<GlucoseScreen> createState() => _GlucoseScreenState();
}

class _GlucoseScreenState extends ConsumerState<GlucoseScreen> {
  int _tapIndex = 0;
  List<GlucoseHistoryResponse> _records = [];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      await setGlucoseHistories();
    });
  }

  Future<void> setGlucoseHistories() async {
    var result = await ref
        .read(glucoseHistoryControllerProvider.notifier)
        .getAllGlucoseHistories(widget.careGiver.patientId);
    if (result == null || result.isEmpty) return;
    setState(() => _records = result);
  }

  Widget getScreen() {
    if (_records.isEmpty) {
      return Column(
        children: [
          SizedBox(height: 20),
          Center(
            child: Text(
              "해당 Care Receiver의 \n혈당 정보가 존재하지 않습니다.\n혈당 정보를 업로드해주세요.",
              style: TextStyle(fontSize: 16, height: 20 / 16, color: AppColors.mainColor),
            ),
          ),
        ],
      );
    }
    switch (_tapIndex) {
      case 0:
        return GlucoseChart(records: _records);
      case 1:
      default:
        return GlucoseStatisticalInformation(careGiver: widget.careGiver, records: _records);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          children: [
            getTapButton(index: 0, title: "차트 보기"),
            getTapButton(index: 1, title: "통계 정보 보기"),
          ],
        ),
        Expanded(child: getScreen()),
      ],
    );
  }

  Widget getTapButton({required int index, required String title}) {
    var isSelected = _tapIndex == index;
    return Expanded(
      child: GestureDetector(
        onTap: () => setState(() => _tapIndex = index),
        behavior: HitTestBehavior.opaque,
        child: Container(
          height: 50,
          alignment: Alignment.center,
          child: Text(
            title,
            style: TextStyle(
              fontSize: 14,
              height: 20 / 14,
              color: isSelected ? AppColors.mainColor : AppColors.fontGray100Color,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
      ),
    );
  }
}
