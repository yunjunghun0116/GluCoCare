import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/features/care/presentation/providers.dart';
import 'package:app/features/glucose_history/data/models/glucose_history_response.dart';
import 'package:app/features/glucose_history/data/models/predict_glucose_response.dart';
import 'package:app/features/glucose_history/presentation/providers.dart';
import 'package:app/features/glucose_history/presentation/widgets/glucose_chart_y_axis.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

import '../../../../core/data/repositories/local_repository.dart';
import '../../../../core/exceptions/custom_exception.dart';
import '../../../../shared/constants/local_repository_key.dart';
import 'glucose_chart_tooltip.dart';

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

  late final TrackballBehavior _trackballNormal;
  late final TrackballBehavior _trackballHidden;
  bool _isInPredictionZone = false;
  bool _isPredictLoading = true;
  final Set<String> _labelShowSet = {};
  double _interval = 1;
  final _tooltipNotifier = ValueNotifier<({double xPosition, int index})?>(null);
  late GlucoseChartYAxis _yAxis;

  DateTime get minDate => widget.records.first.dateTime.subtract(Duration(hours: _interval.toInt()));

  DateTime get maxDate => widget.records.last.dateTime.add(Duration(hours: 3)).difference(DateTime.now()).isNegative
      ? widget.records.last.dateTime.add(Duration(hours: 2))
      : DateTime.now();

  double? _cachedChartWidth;

  double get chartWidth => _cachedChartWidth ??= calculateWidthForChart();

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

  Future<void> _initPredictGlucoseList() async {
    late int careRelationId;
    try {
      careRelationId = LocalRepository().read<int>(LocalRepositoryKey.lateCareRelationId);
    } on CustomException catch (_) {
      var result = await ref.read(careRelationControllerProvider.notifier).getAllCareRelations();
      if (result == null || result.isEmpty) return;
      await LocalRepository().save<int>(LocalRepositoryKey.lateCareRelationId, result.first.id);
      careRelationId = result.first.id;
    }
    try {
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
    } catch (e) {
      throw CustomException(ExceptionMessage.internalServerError);
    } finally {
      _yAxis = GlucoseChartYAxis.calculate(
        records: widget.records,
        normalPredictList: _normalPredictGlucoseList,
        exercisePredictList: _exercisePredictGlucoseList,
      );
      setState(() => _isPredictLoading = false);
    }
  }

  @override
  void initState() {
    super.initState();
    _trackballNormal = TrackballBehavior(
      lineColor: AppColors.subColor4,
      enable: true,
      activationMode: ActivationMode.longPress,
      shouldAlwaysShow: true,
      lineType: TrackballLineType.vertical,
      tooltipDisplayMode: TrackballDisplayMode.none,
    );
    _trackballHidden = TrackballBehavior(
      lineColor: Colors.transparent,
      enable: true,
      activationMode: ActivationMode.longPress,
      shouldAlwaysShow: true,
      lineType: TrackballLineType.vertical,
      tooltipDisplayMode: TrackballDisplayMode.none,
    );
    _yAxis = GlucoseChartYAxis.calculate(records: widget.records);

    WidgetsBinding.instance.addPostFrameCallback((_) {
      _controller.jumpTo(_controller.position.maxScrollExtent);
      _initPredictGlucoseList();
    });
  }

  @override
  void dispose() {
    _tooltipNotifier.dispose();
    _controller.dispose();
    super.dispose();
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
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              SizedBox(
                width: 50,
                height: 400,
                child: Column(
                  children: [
                    SizedBox(height: 10),
                    Expanded(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: _yAxis.labels
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
                      physics: ClampingScrollPhysics(),
                      scrollDirection: Axis.horizontal,
                      child: SizedBox(
                        width: chartWidth,
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
                                minimum: _yAxis.min.toDouble(),
                                maximum: _yAxis.max.toDouble(),
                                interval: _yAxis.interval.toDouble(),
                                axisLine: const AxisLine(width: 0),
                                labelStyle: const TextStyle(fontSize: 0),
                                majorTickLines: const MajorTickLines(size: 0),
                                plotBands: [
                                  PlotBand(
                                    start: 70,
                                    end: 140,
                                    color: AppColors.glucoseNormalBandColor.withValues(alpha: 0.4),
                                  ),
                                  PlotBand(
                                    start: 140,
                                    end: 180,
                                    color: AppColors.glucoseWarningBandColor.withValues(alpha: 0.4),
                                  ),
                                  PlotBand(
                                    start: 180,
                                    end: _yAxis.max.toDouble(),
                                    color: AppColors.glucoseDangerBandColor.withValues(alpha: 0.4),
                                  ),
                                  PlotBand(
                                    start: _yAxis.min.toDouble(),
                                    end: 70,
                                    color: AppColors.glucoseDangerBandColor.withValues(alpha: 0.4),
                                  ),
                                ],
                              ),
                              tooltipBehavior: TooltipBehavior(enable: false),
                              trackballBehavior: _isInPredictionZone ? _trackballHidden : _trackballNormal,
                              onTrackballPositionChanging: (TrackballArgs args) {
                                var info = args.chartPointInfo;

                                if (info.seriesIndex != 0) {
                                  if (!_isInPredictionZone) {
                                    setState(() => _isInPredictionZone = true);
                                    _tooltipNotifier.value = null;
                                  }
                                  return;
                                }

                                if (_isInPredictionZone) {
                                  setState(() => _isInPredictionZone = false);
                                }

                                var idx = info.dataPointIndex;
                                var xPosition = info.xPosition;
                                if (_tooltipNotifier.value?.index == idx) return;

                                _tooltipNotifier.value = (xPosition: xPosition!, index: idx!);
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
                                  width: 3,
                                  color: Color(0xFF2F8F9D),
                                  markerSettings: MarkerSettings(isVisible: false),
                                  name: "휴식 평균",
                                ),
                                LineSeries<PredictGlucoseResponse, DateTime>(
                                  dataSource: _exercisePredictGlucoseList,
                                  xValueMapper: (dto, _) => dto.dateTime,
                                  yValueMapper: (dto, _) => dto.mean,
                                  width: 3,
                                  color: Colors.orange,
                                  markerSettings: MarkerSettings(isVisible: false),
                                  name: "운동 평균",
                                ),
                              ],
                            ),
                            ValueListenableBuilder(
                              valueListenable: _tooltipNotifier,
                              builder: (context, value, _) {
                                if (value == null) return const SizedBox.shrink();
                                return GlucoseChartTooltip(
                                  record: widget.records[value.index],
                                  xPosition: value.xPosition,
                                );
                              },
                            ),
                          ],
                        ),
                      ),
                    ),
                    Positioned(
                      top: 10,
                      right: 10,
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
                          if (_isPredictLoading)
                            Container(
                              margin: const EdgeInsets.only(top: 10),
                              width: 24,
                              height: 24,
                              child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.mainColor),
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
        setState(() {
          _labelShowSet.clear();
          _cachedChartWidth = null;
          _interval = value;
          _tooltipNotifier.value = null;
        });
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
}
