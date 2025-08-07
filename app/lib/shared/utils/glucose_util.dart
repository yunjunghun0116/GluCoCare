import 'dart:math';

import 'package:app/features/glucose_history/data/models/glucose_history_response_dto.dart';
import 'package:app/shared/utils/today_glucose.dart';

final class GlucoseUtil {
  static TodayGlucose? getLastDateGlucoseData(List<GlucoseHistoryResponseDto> lists) {
    var today = lists.last.dateTime;
    var filteredList = lists
        .where((glucoseHistory) => isSameDate(glucoseHistory.dateTime, today))
        .map((glucoseHistory) => glucoseHistory.sgv)
        .toList();
    if (filteredList.isEmpty) return null;
    var minValue = filteredList.first;
    var maxValue = filteredList.first;
    var sumValue = filteredList.first;
    for (var i = 1; i < filteredList.length; i++) {
      minValue = min(minValue, filteredList[i]);
      maxValue = max(maxValue, filteredList[i]);
      sumValue += filteredList[i];
    }

    return TodayGlucose(min: minValue, max: maxValue, avg: sumValue / filteredList.length);
  }

  static bool isSameDate(DateTime d1, DateTime d2) {
    if (d1.year != d2.year) return false;
    if (d1.month != d2.month) return false;
    if (d1.day != d2.day) return false;
    return true;
  }

  static DateTime getLocalDate(int date) {
    var dateTime = DateTime.fromMillisecondsSinceEpoch(date, isUtc: true).toLocal();
    return dateTime;
  }
}
