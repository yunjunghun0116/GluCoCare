// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_patient_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CreatePatientResponse _$CreatePatientResponseFromJson(Map<String, dynamic> json) => CreatePatientResponse(
  id: (json['id'] as num).toInt(),
  name: json['name'] as String,
  cgmServerUrl: json['cgmServerUrl'] as String,
);

Map<String, dynamic> _$CreatePatientResponseToJson(CreatePatientResponse instance) => <String, dynamic>{
  'id': instance.id,
  'name': instance.name,
  'cgmServerUrl': instance.cgmServerUrl,
};
