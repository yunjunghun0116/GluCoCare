import 'package:json_annotation/json_annotation.dart';

part 'care_relation_response.g.dart';

@JsonSerializable()
class CareRelationResponse {
  final int id;
  final int patientId;
  final String patientName;

  CareRelationResponse({required this.id, required this.patientId, required this.patientName});

  factory CareRelationResponse.fromJson(Map<String, dynamic> json) => _$CareRelationResponseFromJson(json);

  Map<String, dynamic> toJson() => _$CareRelationResponseToJson(this);

  @override
  String toString() {
    return 'CareRelationResponse{id: $id, patientId: $patientId, patientName: $patientName}';
  }
}
