import '../../../../shared/utils/glucose_util.dart';
import 'glucose_history_response.dart';

class PredictGlucoseResponse {
  final int id;
  final DateTime dateTime;
  final double mean;
  final double min;
  final double max;

  PredictGlucoseResponse({
    required this.id,
    required this.dateTime,
    required this.mean,
    required this.min,
    required this.max,
  });

  factory PredictGlucoseResponse.fromJson(Map<String, dynamic> json) {
    final epochMs = (json['dateTime'] as num).toInt();
    final dt = GlucoseUtil.getLocalDate(epochMs);

    return PredictGlucoseResponse(id: json['id'], dateTime: dt, mean: json['mean'], min: json['min'], max: json['max']);
  }

  factory PredictGlucoseResponse.fromGlucoseHistoryResponse(GlucoseHistoryResponse glucoseHistory) {
    return PredictGlucoseResponse(
      id: glucoseHistory.id,
      dateTime: glucoseHistory.dateTime,
      mean: glucoseHistory.sgv.toDouble(),
      min: glucoseHistory.sgv.toDouble(),
      max: glucoseHistory.sgv.toDouble(),
    );
  }
}
