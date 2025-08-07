import 'package:app/shared/utils/glucose_util.dart';

class GlucoseHistoryResponseDto {
  final int id;
  final DateTime dateTime;
  final int sgv;

  GlucoseHistoryResponseDto({required this.id, required this.dateTime, required this.sgv});

  factory GlucoseHistoryResponseDto.fromJson(Map<String, dynamic> json) {
    var dateTime = GlucoseUtil.getLocalDate(json['date']);
    return GlucoseHistoryResponseDto(id: json['id'], dateTime: dateTime, sgv: json['sgv']);
  }
}
