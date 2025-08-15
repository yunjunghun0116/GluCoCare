import 'package:json_annotation/json_annotation.dart';

part 'care_giver_response.g.dart';

@JsonSerializable()
class CareGiverResponse {
  final int id;
  final int patientId;
  final String patientName;

  CareGiverResponse({required this.id, required this.patientId, required this.patientName});

  factory CareGiverResponse.fromJson(Map<String, dynamic> json) => _$CareGiverResponseFromJson(json);

  Map<String, dynamic> toJson() => _$CareGiverResponseToJson(this);

  @override
  String toString() {
    return 'CareGiverResponse{id: $id, patientId: $patientId, patientName: $patientName}';
  }
}
