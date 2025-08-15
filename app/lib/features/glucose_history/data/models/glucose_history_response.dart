import 'package:app/shared/utils/glucose_util.dart';

class GlucoseHistoryResponse {
  final int id;
  final DateTime dateTime;
  final int sgv;

  GlucoseHistoryResponse({required this.id, required this.dateTime, required this.sgv});

  factory GlucoseHistoryResponse.fromJson(Map<String, dynamic> json) {
    var dateTime = GlucoseUtil.getLocalDate(json['date']);
    return GlucoseHistoryResponse(id: json['id'], dateTime: dateTime, sgv: json['sgv']);
  }
}
