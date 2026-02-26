import '../../data/models/glucose_history_response.dart';
import '../../data/models/predict_glucose_response.dart';

class GlucoseChartYAxis {
  final int min;
  final int max;
  final int interval;
  final List<int> labels;

  const GlucoseChartYAxis._({required this.min, required this.max, required this.interval, required this.labels});

  factory GlucoseChartYAxis.calculate({
    required List<GlucoseHistoryResponse> records,
    List<PredictGlucoseResponse> normalPredictList = const [],
    List<PredictGlucoseResponse> exercisePredictList = const [],
  }) {
    var allValues = [
      records.map((e) => e.sgv).reduce((a, b) => a < b ? a : b),
      records.map((e) => e.sgv).reduce((a, b) => a > b ? a : b),
      if (normalPredictList.isNotEmpty) normalPredictList.map((e) => e.mean).reduce((a, b) => a < b ? a : b).toInt(),
      if (normalPredictList.isNotEmpty) normalPredictList.map((e) => e.mean).reduce((a, b) => a > b ? a : b).toInt(),
      if (exercisePredictList.isNotEmpty)
        exercisePredictList.map((e) => e.mean).reduce((a, b) => a < b ? a : b).toInt(),
      if (exercisePredictList.isNotEmpty)
        exercisePredictList.map((e) => e.mean).reduce((a, b) => a > b ? a : b).toInt(),
    ];

    var rawMin = allValues.reduce((a, b) => a < b ? a : b);
    var rawMax = allValues.reduce((a, b) => a > b ? a : b);
    var range = rawMax - rawMin;

    var padding = (range * 0.1).round();
    padding = padding < 10 ? 10 : padding;
    padding = padding > 30 ? 30 : padding;

    var calculatedMin = ((rawMin - padding) / 10).floor() * 10;
    var calculatedMax = ((rawMax + padding) / 10).ceil() * 10;

    var min = calculatedMin < 30 ? 30 : calculatedMin;
    var max = calculatedMax;

    var interval = range <= 100
        ? 25
        : range <= 200
        ? 50
        : 100;

    var labels = <int>[];
    var start = (min / interval).ceil() * interval;
    for (var y = start; y <= max; y += interval) {
      labels.add(y);
    }

    return GlucoseChartYAxis._(min: min, max: max, interval: interval, labels: labels.reversed.toList());
  }
}
