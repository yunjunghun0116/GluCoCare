import 'package:app/features/mission/data/models/daily_mission_response.dart';
import 'package:app/features/mission/data/models/mission_type.dart';
import 'package:app/features/mission/presentation/providers.dart';
import 'package:app/features/mission/presentation/widgets/mission_button.dart';
import 'package:app/features/mission/presentation/widgets/point_container.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../shared/constants/app_colors.dart';
import '../../../../shared/widgets/common_app_bar.dart';

class MissionScreen extends ConsumerStatefulWidget {
  const MissionScreen({super.key});

  @override
  ConsumerState<MissionScreen> createState() => _MissionScreenState();
}

class _MissionScreenState extends ConsumerState<MissionScreen> {
  List<DailyMissionResponse> _missions = [];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      setDailyMissions();
    });
  }

  Future<void> setDailyMissions() async {
    var missions = await ref.read(missionControllerProvider.notifier).readDailyMissions();
    if (missions == null) return;
    setState(() => _missions = missions);
  }

  bool isShowMissionState(DailyMissionResponse mission) {
    if (mission.isCompleted) return false;
    if (mission.isFailed) return false;
    if (mission.missionType == MissionType.timeInRange) return true;
    if (mission.missionType == MissionType.stableGlucose) return true;
    return false;
  }

  String getMissionStateTitle(DailyMissionResponse mission) {
    switch (mission.missionType) {
      case MissionType.timeInRange:
        {
          return "현재 TIR : ${mission.currentValue.toStringAsFixed(1)}%";
        }
      case MissionType.stableGlucose:
        {
          return "현재 CV : ${mission.currentValue.toStringAsFixed(1)}%";
        }
      default:
        {
          return "";
        }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(title: "오늘의 미션", onBack: () => Navigator.pop(context), actions: [PointContainer()]),
      backgroundColor: AppColors.backgroundColor,
      body: ListView(
        children: [
          Container(
            padding: const EdgeInsets.only(left: 20, right: 20, bottom: 20),
            child: Text(
              "오늘의 미션은 나 뿐만 아니라 \n내 가족을 통해서도 완료를 할 수 있습니다.\n가족과 함께 혈당 관리를 해보아요.",
              style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.mainColor, fontWeight: FontWeight.bold),
            ),
          ),
          ..._missions.map(
            (mission) => Container(
              margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
              padding: const EdgeInsets.symmetric(vertical: 16),
              decoration: BoxDecoration(
                color: AppColors.whiteColor,
                borderRadius: BorderRadius.circular(10),
                boxShadow: [
                  BoxShadow(color: AppColors.blackColor.withValues(alpha: 0.1), blurRadius: 12, offset: Offset(0, 4)),
                ],
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    child: Text(
                      mission.title,
                      style: TextStyle(
                        fontSize: 16,
                        height: 20 / 16,
                        color: AppColors.fontGray800Color,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  SizedBox(height: 10),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    child: Text(
                      mission.description,
                      style: TextStyle(fontSize: 12, height: 20 / 12, color: AppColors.fontGray400Color),
                    ),
                  ),
                  SizedBox(height: 20),
                  if (isShowMissionState(mission))
                    Container(
                      padding: const EdgeInsets.only(bottom: 10),
                      child: Center(
                        child: Text(
                          getMissionStateTitle(mission),
                          style: TextStyle(fontSize: 12, height: 20 / 12, color: AppColors.fontGray400Color),
                        ),
                      ),
                    ),
                  MissionButton(
                    onTap: () async {
                      if (await ref.read(missionControllerProvider.notifier).completeDailyMission(mission.id)) {
                        setDailyMissions();
                      }
                    },
                    missionResponse: mission,
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
