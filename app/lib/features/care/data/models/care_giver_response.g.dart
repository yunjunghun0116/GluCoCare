// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'care_giver_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CareGiverResponse _$CareGiverResponseFromJson(Map<String, dynamic> json) => CareGiverResponse(
  id: (json['id'] as num).toInt(),
  patientId: (json['patientId'] as num).toInt(),
  patientName: json['patientName'] as String,
);

Map<String, dynamic> _$CareGiverResponseToJson(CareGiverResponse instance) => <String, dynamic>{
  'id': instance.id,
  'patientId': instance.patientId,
  'patientName': instance.patientName,
};
