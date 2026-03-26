// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'daily_mission_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DailyMissionResponse _$DailyMissionResponseFromJson(Map<String, dynamic> json) => DailyMissionResponse(
  id: (json['id'] as num).toInt(),
  title: json['title'] as String,
  description: json['description'] as String,
  missionType: $enumDecode(_$MissionTypeEnumMap, json['missionType']),
  threshold: (json['threshold'] as num).toDouble(),
  currentValue: (json['currentValue'] as num).toDouble(),
  canComplete: json['canComplete'] as bool,
  isCompleted: json['isCompleted'] as bool,
  isFailed: json['isFailed'] as bool,
  rewardPoint: (json['rewardPoint'] as num).toInt(),
  missionDate: DateTime.parse(json['missionDate'] as String),
);

Map<String, dynamic> _$DailyMissionResponseToJson(DailyMissionResponse instance) => <String, dynamic>{
  'id': instance.id,
  'title': instance.title,
  'description': instance.description,
  'missionType': _$MissionTypeEnumMap[instance.missionType]!,
  'threshold': instance.threshold,
  'currentValue': instance.currentValue,
  'canComplete': instance.canComplete,
  'isCompleted': instance.isCompleted,
  'isFailed': instance.isFailed,
  'rewardPoint': instance.rewardPoint,
  'missionDate': instance.missionDate.toIso8601String(),
};

const _$MissionTypeEnumMap = {
  MissionType.timeInRange: 'TIME_IN_RANGE',
  MissionType.noHypoglycemia: 'NO_HYPOGLYCEMIA',
  MissionType.noHyperglycemia: 'NO_HYPERGLYCEMIA',
  MissionType.morningNormal: 'MORNING_NORMAL',
  MissionType.stableGlucose: 'STABLE_GLUCOSE',
};
