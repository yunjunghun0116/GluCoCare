// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'care_giver_response_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CareGiverResponseDto _$CareGiverResponseDtoFromJson(Map<String, dynamic> json) => CareGiverResponseDto(
  id: (json['id'] as num).toInt(),
  patientId: (json['patientId'] as num).toInt(),
  patientName: json['patientName'] as String,
);

Map<String, dynamic> _$CareGiverResponseDtoToJson(CareGiverResponseDto instance) => <String, dynamic>{
  'id': instance.id,
  'patientId': instance.patientId,
  'patientName': instance.patientName,
};
