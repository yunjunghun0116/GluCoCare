import 'package:health/health.dart';

class HealthUploadRequest {
  final DateTime date;
  final int sgv;

  HealthUploadRequest({required this.date, required this.sgv});

  factory HealthUploadRequest.fromPoint(HealthDataPoint point) {
    var json = point.toJson();
    return HealthUploadRequest(sgv: (json['value']['numericValue'] as num).round(), date: point.dateFrom);
  }

  Map<String, dynamic> toJson() => <String, dynamic>{'dateTime': date.millisecondsSinceEpoch, 'sgv': sgv};
}
