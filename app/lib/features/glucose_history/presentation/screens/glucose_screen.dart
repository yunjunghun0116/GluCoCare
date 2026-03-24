import 'package:app/features/glucose_history/presentation/widgets/glucose_chart.dart';
import 'package:app/features/glucose_history/presentation/widgets/glucose_statistical_information.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/health/health_connector.dart';
import '../../../../shared/constants/app_colors.dart';
import '../../../care/data/models/care_relation_response.dart';
import '../../../patient/presentation/providers.dart';
import '../../data/models/glucose_history_response.dart';
import '../controllers/health_controller.dart';
import '../providers.dart';

class GlucoseScreen extends ConsumerStatefulWidget {
  final CareRelationResponse careRelation;

  const GlucoseScreen({super.key, required this.careRelation});

  @override
  ConsumerState<GlucoseScreen> createState() => _GlucoseScreenState();
}

class _GlucoseScreenState extends ConsumerState<GlucoseScreen> {
  bool _isLoading = false;
  int _tapIndex = 0;
  List<GlucoseHistoryResponse> _records = [];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => setGlucoseHistories());
  }

  Future<void> setGlucoseHistories() async {
    if (_isLoading) return;
    setState(() => _isLoading = true);
    try {
      if (await ref.read(patientControllerProvider.notifier).readIsPatient()) {
        await HealthConnector().initialize();
        await ref.read(healthControllerProvider.notifier).fetchOnce();
      }

      var result = await ref
          .read(glucoseHistoryControllerProvider.notifier)
          .getAllGlucoseHistories(widget.careRelation.id);

      if (mounted) {
        setState(() => _records = result ?? []);
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Widget getScreen() {
    if (_records.isEmpty) {
      return Column(
        children: [
          SizedBox(height: 20),
          Center(
            child: Text(
              "해당 등록자의 혈당 정보가 존재하지 않습니다.\n혈당 정보를 업로드해주세요.",
              style: TextStyle(fontSize: 16, height: 20 / 16, color: AppColors.mainColor),
              textAlign: TextAlign.center,
            ),
          ),
          SizedBox(height: 20),
          GestureDetector(
            onTap: _isLoading ? null : setGlucoseHistories,
            child: _isLoading
                ? SizedBox(
                    width: 24,
                    height: 24,
                    child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.mainColor),
                  )
                : Icon(Icons.refresh, color: AppColors.mainColor),
          ),
        ],
      );
    }
    switch (_tapIndex) {
      case 0:
        return GlucoseChart(
          key: ValueKey(_records.length),
          records: _records,
          onRefresh: setGlucoseHistories,
          isLoading: _isLoading,
        );
      case 1:
      default:
        return GlucoseStatisticalInformation(careGiver: widget.careRelation, records: _records);
    }
  }

  @override
  Widget build(BuildContext context) {
    ref.listen<HealthState>(healthControllerProvider, (previous, next) {
      if (next.lastSyncTime != null && next.lastSyncTime != previous?.lastSyncTime) {
        setGlucoseHistories();
      }
    });
    return Column(
      children: [
        Row(
          children: [
            getTapButton(index: 0, title: "차트 보기"),
            getTapButton(index: 1, title: "통계 정보 보기"),
          ],
        ),
        getScreen(),
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
