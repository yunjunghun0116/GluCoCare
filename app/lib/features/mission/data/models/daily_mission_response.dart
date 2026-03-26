import 'package:json_annotation/json_annotation.dart';

import 'mission_type.dart';

part 'daily_mission_response.g.dart';

@JsonSerializable()
class DailyMissionResponse {
  final int id;
  final String title;
  final String description;
  final MissionType missionType;
  final double threshold;
  final double currentValue;
  final bool canComplete;
  final bool isCompleted;
  final bool isFailed;
  final int rewardPoint;
  final DateTime missionDate;

  DailyMissionResponse({
    required this.id,
    required this.title,
    required this.description,
    required this.missionType,
    required this.threshold,
    required this.currentValue,
    required this.canComplete,
    required this.isCompleted,
    required this.isFailed,
    required this.rewardPoint,
    required this.missionDate,
  });

  factory DailyMissionResponse.fromJson(Map<String, dynamic> json) => _$DailyMissionResponseFromJson(json);

  Map<String, dynamic> toJson() => _$DailyMissionResponseToJson(this);

  @override
  String toString() {
    return 'DailyMissionResponse{id: $id, title: $title, description: $description, missionType: $missionType, threshold: $threshold, currentValue: $currentValue, canComplete: $canComplete, isCompleted: $isCompleted, isFailed: $isFailed, rewardPoint: $rewardPoint, missionDate: $missionDate}';
  }
}
