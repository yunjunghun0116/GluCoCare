import 'package:health/health.dart';

class HealthUploadRequest {
  final String date;
  final int sgv;

  HealthUploadRequest({required this.date, required this.sgv});

  factory HealthUploadRequest.fromPoint(HealthDataPoint point) {
    var json = point.toJson();
    return HealthUploadRequest(
      sgv: (json['value']['numericValue'] as num).round(),
      date: point.dateFrom.toUtc().toIso8601String(),
    );
  }
}
