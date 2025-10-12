import 'package:json_annotation/json_annotation.dart';

part 'create_patient_response.g.dart';

@JsonSerializable()
class CreatePatientResponse {
  final int id;
  final String name;
  final String cgmServerUrl;

  CreatePatientResponse({required this.id, required this.name, required this.cgmServerUrl});

  factory CreatePatientResponse.fromJson(Map<String, dynamic> json) => _$CreatePatientResponseFromJson(json);

  Map<String, dynamic> toJson() => _$CreatePatientResponseToJson(this);

  @override
  String toString() {
    return 'CreatePatientResponse{id: $id, name: $name, cgmServerUrl: $cgmServerUrl}';
  }
}
