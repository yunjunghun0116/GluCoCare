import 'package:json_annotation/json_annotation.dart';

class GlucoseHistoryResponseDto {
  final int id;
  final DateTime dateTime;
  final int sgv;

  GlucoseHistoryResponseDto({required this.id, required this.dateTime, required this.sgv});

  factory GlucoseHistoryResponseDto.fromJson(Map<String, dynamic> json) {
    final dateTime = DateTime.fromMillisecondsSinceEpoch(json['date']);
    final localTime = dateTime.toLocal();

    return GlucoseHistoryResponseDto(id: json['id'], dateTime: localTime, sgv: json['sgv']);
  }
}
