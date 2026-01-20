// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'read_patient_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ReadPatientResponse _$ReadPatientResponseFromJson(Map<String, dynamic> json) => ReadPatientResponse(
  id: (json['id'] as num).toInt(),
  name: json['name'] as String,
  isPatient: json['isPatient'] as bool,
  accessCode: json['accessCode'] as String,
);

Map<String, dynamic> _$ReadPatientResponseToJson(ReadPatientResponse instance) => <String, dynamic>{
  'id': instance.id,
  'name': instance.name,
  'isPatient': instance.isPatient,
  'accessCode': instance.accessCode,
};
