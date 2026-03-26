import 'package:json_annotation/json_annotation.dart';

part 'point_response.g.dart';

@JsonSerializable()
class PointResponse {
  final int point;

  PointResponse({required this.point});

  factory PointResponse.fromJson(Map<String, dynamic> json) => _$PointResponseFromJson(json);

  Map<String, dynamic> toJson() => _$PointResponseToJson(this);

  @override
  String toString() {
    return 'PointResponse{point: $point}';
  }
}
