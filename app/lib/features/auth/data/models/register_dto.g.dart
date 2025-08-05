// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'register_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RegisterDto _$RegisterDtoFromJson(Map<String, dynamic> json) =>
    RegisterDto(email: json['email'] as String, password: json['password'] as String, name: json['name'] as String);

Map<String, dynamic> _$RegisterDtoToJson(RegisterDto instance) => <String, dynamic>{
  'email': instance.email,
  'password': instance.password,
  'name': instance.name,
};
