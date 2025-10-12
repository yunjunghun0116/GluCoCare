import 'package:json_annotation/json_annotation.dart';

part 'read_patient_response.g.dart';

@JsonSerializable()
class ReadPatientResponse {
  final int id;
  final String name;
  final String cgmServerUrl;

  ReadPatientResponse({required this.id, required this.name, required this.cgmServerUrl});

  factory ReadPatientResponse.fromJson(Map<String, dynamic> json) => _$ReadPatientResponseFromJson(json);

  Map<String, dynamic> toJson() => _$ReadPatientResponseToJson(this);

  @override
  String toString() {
    return 'ReadPatientResponse{id: $id, name: $name, cgmServerUrl: $cgmServerUrl}';
  }
}
