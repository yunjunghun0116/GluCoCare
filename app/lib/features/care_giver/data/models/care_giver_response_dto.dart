import 'package:json_annotation/json_annotation.dart';

part 'care_giver_response_dto.g.dart';

@JsonSerializable()
class CareGiverResponseDto {
  final int id;
  final int patientId;
  final String patientName;

  CareGiverResponseDto({required this.id, required this.patientId, required this.patientName});

  factory CareGiverResponseDto.fromJson(Map<String, dynamic> json) => _$CareGiverResponseDtoFromJson(json);

  Map<String, dynamic> toJson() => _$CareGiverResponseDtoToJson(this);

  @override
  String toString() {
    return 'CareGiverResponseDto{id: $id, patientId: $patientId, patientName: $patientName}';
  }
}
