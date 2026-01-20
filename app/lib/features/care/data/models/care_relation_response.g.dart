// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'care_relation_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CareRelationResponse _$CareRelationResponseFromJson(Map<String, dynamic> json) => CareRelationResponse(
  id: (json['id'] as num).toInt(),
  patientId: (json['patientId'] as num).toInt(),
  patientName: json['patientName'] as String,
);

Map<String, dynamic> _$CareRelationResponseToJson(CareRelationResponse instance) => <String, dynamic>{
  'id': instance.id,
  'patientId': instance.patientId,
  'patientName': instance.patientName,
};
