import 'package:app/features/care/presentation/providers.dart';
import 'package:app/features/glucose_history/data/models/glucose_history_response.dart';
import 'package:app/features/glucose_history/data/models/predict_glucose_response.dart';
import 'package:app/features/glucose_history/presentation/providers.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

import '../../../../core/data/repositories/local_repository.dart';
import '../../../../core/exceptions/custom_exception.dart';
import '../../../../shared/constants/local_repository_key.dart';

class GlucoseChart extends ConsumerStatefulWidget {
  final List<GlucoseHistoryResponse> records;

  const GlucoseChart({super.key, required this.records});

  @override
  ConsumerState<GlucoseChart> createState() => _GlucoseChartState();
}

class _GlucoseChartState extends ConsumerState<GlucoseChart> {
  final ScrollController _controller = ScrollController();
  final List<PredictGlucoseResponse> _normalPredictGlucoseList = [];
  final List<PredictGlucoseResponse> _exercisePredictGlucoseList = [];
  late final TrackballBehavior _trackball;
  final Set<String> _labelShowSet = {};
  double _interval = 1;
  double? _selectedGlucoseHistoryXPosition;
  int? _selectedGlucoseHistoryIndex;

  DateTime get minDate => widget.records.first.dateTime.subtract(Duration(hours: _interval.toInt()));

  DateTime get maxDate => widget.records.last.dateTime.add(Duration(hours: 3)).difference(DateTime.now()).isNegative
      ? widget.records.last.dateTime.add(Duration(hours: 2))
      : DateTime.now();

  double calculateWidthForChart() {
    if (widget.records.isEmpty) return MediaQuery.of(context).size.width;
    // 전체 시간 범위(시간 단위)
    var totalHours = maxDate.difference(minDate).inHours;
    // interval당 픽셀 수
    var pixelsPerInterval = 100;
    // 전체 너비 = (전체 시간 / interval) * interval당 픽셀
    var totalIntervals = totalHours / _interval;
    var calculatedWidth = totalIntervals * pixelsPerInterval;
    // 최소 너비 보장
    var minWidth = MediaQuery.of(context).size.width;
    return calculatedWidth > minWidth ? calculatedWidth : minWidth;
  }

  String getKey(DateTime date) {
    return "${date.month}-${date.day}";
  }

  void initPredictGlucoseList() async {
    late int careRelationId;
    try {
      careRelationId = LocalRepository().read<int>(LocalRepositoryKey.lateCareRelationId);
    } on CustomException catch (_) {
      var result = await ref.read(careRelationControllerProvider.notifier).getAllCareRelations();
      if (result == null || result.isEmpty) return;
      await LocalRepository().save<int>(LocalRepositoryKey.lateCareRelationId, result.first.id);
      careRelationId = result.first.id;
    }

    var normalPredictGlucoseList = await ref
        .read(glucoseHistoryControllerProvider.notifier)
        .getPredictGlucose(careRelationId);
    if (normalPredictGlucoseList != null && normalPredictGlucoseList.isNotEmpty) {
      _normalPredictGlucoseList
        ..clear()
        ..addAll(normalPredictGlucoseList);
    }

    var exercisePredictGlucoseList = await ref
        .read(glucoseHistoryControllerProvider.notifier)
        .getPredictGlucoseWithExercise(careRelationId);
    if (exercisePredictGlucoseList != null && exercisePredictGlucoseList.isNotEmpty) {
      _exercisePredictGlucoseList
        ..clear()
        ..addAll(exercisePredictGlucoseList);
    }

    setState(() {});
  }

  @override
  void initState() {
    super.initState();
    _trackball = TrackballBehavior(
      lineColor: AppColors.subColor4,
      enable: true,
      activationMode: ActivationMode.longPress,
      shouldAlwaysShow: true,
      lineType: TrackballLineType.vertical,
      // 세로선
      tooltipDisplayMode: TrackballDisplayMode.none,
    );
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _controller.jumpTo(_controller.position.maxScrollExtent);
      initPredictGlucoseList();
    });
  }

  @override
  Widget build(BuildContext context) {
    return ListView(
      physics: ClampingScrollPhysics(),
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
          child: Text(
            "혈당 (mg/dL)",
            style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.mainColor, fontWeight: FontWeight.bold),
          ),
        ),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Row(children: [1.0, 2.0, 3.0, 6.0].map((number) => getIntervalButton(number)).toList()),
        ),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              SizedBox(
                width: 50,
                height: 400,
                child: Column(
                  children: [
                    Expanded(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [300, 250, 200, 150, 100, 50]
                            .map(
                              (y) => Text(
                                '$y',
                                style: TextStyle(fontSize: 12, height: 20 / 12, color: AppColors.fontGray600Color),
                              ),
                            )
                            .toList(),
                      ),
                    ),
                    SizedBox(height: 30),
                  ],
                ),
              ),
              Expanded(
                child: Stack(
                  children: [
                    SingleChildScrollView(
                      controller: _controller,
                      scrollDirection: Axis.horizontal,
                      child: SizedBox(
                        width: calculateWidthForChart(),
                        height: 400,
                        child: Stack(
                          children: [
                            SfCartesianChart(
                              margin: EdgeInsets.symmetric(vertical: 10),
                              primaryXAxis: DateTimeAxis(
                                minimum: minDate,
                                maximum: maxDate,
                                interval: _interval,
                                intervalType: DateTimeIntervalType.hours,
                                labelAlignment: LabelAlignment.center,
                                labelPosition: ChartDataLabelPosition.outside,
                                labelIntersectAction: AxisLabelIntersectAction.hide,
                                axisLabelFormatter: (AxisLabelRenderDetails details) {
                                  var date = DateTime.fromMillisecondsSinceEpoch(details.value as int);
                                  var key = getKey(date);
                                  var isAlreadyShown = _labelShowSet.contains(key);

                                  if (!isAlreadyShown) {
                                    _labelShowSet.add(key);
                                    var monthDayString = DateFormat('MM.dd').format(date);
                                    return ChartAxisLabel(
                                      monthDayString,
                                      TextStyle(
                                        fontSize: 12,
                                        color: AppColors.fontGray800Color,
                                        fontWeight: FontWeight.bold,
                                        height: 20 / 12,
                                      ),
                                    );
                                  }
                                  return ChartAxisLabel(
                                    "${date.hour < 12 ? "오전" : "오후"} ${date.hour > 12 ? date.hour - 12 : date.hour}시",
                                    TextStyle(
                                      fontSize: 12,
                                      color: AppColors.fontGray800Color,
                                      fontWeight: FontWeight.bold,
                                      height: 20 / 12,
                                    ),
                                  );
                                },
                              ),
                              primaryYAxis: NumericAxis(
                                minimum: 50,
                                maximum: 300,
                                interval: 50,
                                axisLine: const AxisLine(width: 0),
                                labelStyle: const TextStyle(fontSize: 0),
                                majorTickLines: const MajorTickLines(size: 0),
                              ),
                              tooltipBehavior: TooltipBehavior(enable: false),
                              trackballBehavior: _trackball,
                              onTrackballPositionChanging: (TrackballArgs args) {
                                var info = args.chartPointInfo;
                                var idx = info.dataPointIndex;
                                var xPosition = info.xPosition;
                                if (_selectedGlucoseHistoryIndex == idx) return;
                                setState(() {
                                  _selectedGlucoseHistoryXPosition = xPosition;
                                  _selectedGlucoseHistoryIndex = info.dataPointIndex;
                                });
                              },
                              series: <CartesianSeries>[
                                LineSeries<GlucoseHistoryResponse, DateTime>(
                                  dataSource: widget.records,
                                  xValueMapper: (dto, _) => dto.dateTime,
                                  yValueMapper: (dto, _) => dto.sgv,
                                  width: 3,
                                  pointColorMapper: (dto, _) {
                                    var sgv = dto.sgv;
                                    if (sgv >= 180) {
                                      return AppColors.glucoseDangerColor;
                                    }
                                    if (sgv >= 140) {
                                      return AppColors.glucoseWarningColor;
                                    }
                                    if (sgv < 70) {
                                      return AppColors.glucoseDangerColor;
                                    }
                                    return AppColors.glucoseNormalColor;
                                  },
                                  markerSettings: MarkerSettings(isVisible: false),
                                ),
                                LineSeries<PredictGlucoseResponse, DateTime>(
                                  dataSource: _normalPredictGlucoseList,
                                  xValueMapper: (dto, _) => dto.dateTime,
                                  yValueMapper: (dto, _) => dto.mean,
                                  width: 2,
                                  color: Color(0xFF2F8F9D),
                                  markerSettings: MarkerSettings(isVisible: false),
                                  name: "휴식 평균",
                                ),
                                LineSeries<PredictGlucoseResponse, DateTime>(
                                  dataSource: _exercisePredictGlucoseList,
                                  xValueMapper: (dto, _) => dto.dateTime,
                                  yValueMapper: (dto, _) => dto.mean,
                                  width: 2,
                                  color: Colors.orange,
                                  markerSettings: MarkerSettings(isVisible: false),
                                  name: "운동 평균",
                                ),
                              ],
                            ),
                            if (_selectedGlucoseHistoryIndex != null) getSelectedToolTip(),
                          ],
                        ),
                      ),
                    ),
                    Positioned(
                      top: 0,
                      right: 0,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.end,
                        children: [
                          Text(
                            "예상 혈당",
                            style: TextStyle(
                              fontSize: 14,
                              height: 20 / 14,
                              color: AppColors.mainColor,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Container(width: 30, height: 3, color: Colors.green),
                              SizedBox(width: 10),
                              Text(
                                "음식 섭취",
                                style: TextStyle(fontSize: 12, height: 14 / 12, color: AppColors.mainColor),
                              ),
                            ],
                          ),
                          Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Container(width: 30, height: 3, color: Colors.orange),
                              SizedBox(width: 10),
                              Text(
                                "운동",
                                style: TextStyle(fontSize: 12, height: 14 / 12, color: AppColors.mainColor),
                              ),
                            ],
                          ),
                          Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Container(width: 30, height: 3, color: Color(0xFF2F8F9D)),
                              SizedBox(width: 10),
                              Text(
                                "휴식",
                                style: TextStyle(fontSize: 12, height: 14 / 12, color: AppColors.mainColor),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget getIntervalButton(double value) {
    var isSelected = _interval == value;

    return GestureDetector(
      onTap: () {
        _labelShowSet.clear();
        setState(() => _interval = value);
        WidgetsBinding.instance.addPostFrameCallback((_) {
          _controller.jumpTo(_controller.position.maxScrollExtent);
        });
      },
      child: Container(
        alignment: Alignment.center,
        width: 60,
        height: 40,
        decoration: BoxDecoration(
          color: isSelected ? AppColors.mainColor : AppColors.backgroundColor,
          borderRadius: BorderRadius.circular(5),
        ),
        child: Text(
          "${value.toInt()}시간",
          style: TextStyle(
            fontSize: 14,
            height: 20 / 14,
            color: isSelected ? AppColors.whiteColor : AppColors.mainColor,
          ),
        ),
      ),
    );
  }

  Widget getSelectedToolTip() {
    var labelWidth = 100.0;
    double? left;
    if (_selectedGlucoseHistoryXPosition != null) {
      left = _selectedGlucoseHistoryXPosition! + 10 - labelWidth / 2;
    }
    var selectedGlucoseHistory = widget.records[_selectedGlucoseHistoryIndex!];
    var date = selectedGlucoseHistory.dateTime;
    return Positioned(
      top: 12,
      left: left,
      width: labelWidth,
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 10),
        child: Column(
          children: [
            Text(
              "${selectedGlucoseHistory.sgv}",
              textAlign: TextAlign.center,
              style: const TextStyle(color: AppColors.mainColor, fontWeight: FontWeight.bold, fontSize: 20, height: 1),
            ),
            Text(
              "${date.hour < 12 ? "오전" : "오후"} ${date.hour > 12 ? date.hour - 12 : date.hour}:${date.minute.toString().padLeft(2, "0")}",
              textAlign: TextAlign.center,
              style: const TextStyle(color: AppColors.mainColor, fontWeight: FontWeight.bold, fontSize: 13),
            ),
          ],
        ),
      ),
    );
  }
}
