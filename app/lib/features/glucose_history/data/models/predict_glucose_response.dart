import '../../../../shared/utils/glucose_util.dart';

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
}
