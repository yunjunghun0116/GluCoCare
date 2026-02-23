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
    return PredictGlucoseResponse(
      id: json['id'],
      dateTime: DateTime.parse(json['date']),
      mean: json['mean'],
      min: json['min'],
      max: json['max'],
    );
  }
}
