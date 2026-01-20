import 'package:app/features/glucose_history/data/models/glucose_history_response.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

class GlucoseChart extends ConsumerStatefulWidget {
  final List<GlucoseHistoryResponse> records;

  const GlucoseChart({super.key, required this.records});

  @override
  ConsumerState<GlucoseChart> createState() => _GlucoseChartState();
}

class _GlucoseChartState extends ConsumerState<GlucoseChart> {
  final ScrollController _controller = ScrollController();
  late final TrackballBehavior _trackball;
  final Set<String> _labelShowSet = {};
  double _interval = 2;
  double? _selectedGlucoseHistoryXPosition;
  int? _selectedGlucoseHistoryIndex;

  DateTime get minDate => widget.records.first.dateTime.subtract(Duration(hours: _interval.toInt()));

  DateTime get maxDate => widget.records.last.dateTime.add(Duration(hours: 3)).difference(DateTime.now()).isNegative
      ? widget.records.last.dateTime.add(Duration(hours: 3))
      : DateTime.now();

  double calculateWidthForChart() {
    if (widget.records.isEmpty) return MediaQuery.of(context).size.width;
    // 전체 시간 범위(시간 단위)
    var totalHours = maxDate.difference(minDate).inHours;
    // interval당 픽셀 수
    var pixelsPerInterval = 80;
    // 전체 너비 = (전체 시간 / interval) * interval당 픽셀
    var totalIntervals = totalHours / _interval;
    var calculatedWidth = totalIntervals * pixelsPerInterval;
    // 최소 너비 보장
    var minWidth = MediaQuery.of(context).size.width;
    return calculatedWidth > minWidth ? calculatedWidth : minWidth;
  }

  List<GlucoseHistoryResponse> getNormalGlucoseHistories() {
    return [
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 15), sgv: 104),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 20), sgv: 102),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 25), sgv: 101),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 30), sgv: 99),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 35), sgv: 102),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 40), sgv: 98),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 45), sgv: 101),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 50), sgv: 106),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 55), sgv: 108),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 00), sgv: 104),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 05), sgv: 103),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 10), sgv: 102),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 15), sgv: 100),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 20), sgv: 104),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 25), sgv: 103),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 30), sgv: 104),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 35), sgv: 103),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 40), sgv: 104),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 45), sgv: 103),
    ];
  }

  List<GlucoseHistoryResponse> getMealGlucoseHistories() {
    return [
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 15), sgv: 104),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 20), sgv: 110),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 25), sgv: 118),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 30), sgv: 126),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 35), sgv: 138),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 40), sgv: 152),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 45), sgv: 165),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 50), sgv: 168),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 55), sgv: 167),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 00), sgv: 172),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 05), sgv: 168),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 10), sgv: 169),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 15), sgv: 165),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 20), sgv: 160),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 25), sgv: 158),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 30), sgv: 156),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 35), sgv: 150),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 40), sgv: 143),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 45), sgv: 140),
    ];
  }

  List<GlucoseHistoryResponse> getExerciseGlucoseHistories() {
    return [
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 15), sgv: 104),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 20), sgv: 105),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 25), sgv: 100),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 30), sgv: 97),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 35), sgv: 94),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 40), sgv: 92),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 45), sgv: 89),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 50), sgv: 86),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 8, 55), sgv: 85),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 00), sgv: 80),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 05), sgv: 76),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 10), sgv: 74),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 15), sgv: 68),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 20), sgv: 66),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 25), sgv: 65),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 30), sgv: 68),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 35), sgv: 74),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 40), sgv: 77),
      GlucoseHistoryResponse(id: 10000, dateTime: DateTime(2026, 1, 11, 9, 45), sgv: 80),
    ];
  }

  String getKey(DateTime date) {
    return "${date.month}-${date.day}";
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
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      _controller.jumpTo(_controller.position.maxScrollExtent);
    });
  }

  @override
  Widget build(BuildContext context) {
    _labelShowSet.clear();

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
                                labelAlignment: LabelAlignment.center,
                                labelPosition: ChartDataLabelPosition.outside,
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
                                LineSeries<GlucoseHistoryResponse, DateTime>(
                                  dataSource: getNormalGlucoseHistories(),
                                  xValueMapper: (dto, _) => dto.dateTime,
                                  yValueMapper: (dto, _) => dto.sgv,
                                  width: 2,
                                  color: Color(0xFF2F8F9D),
                                  markerSettings: MarkerSettings(isVisible: false),
                                  name: "가만히 있을 때",
                                ),
                                LineSeries<GlucoseHistoryResponse, DateTime>(
                                  dataSource: getMealGlucoseHistories(),
                                  xValueMapper: (dto, _) => dto.dateTime,
                                  yValueMapper: (dto, _) => dto.sgv,
                                  width: 2,
                                  color: Colors.yellow,
                                  markerSettings: MarkerSettings(isVisible: false),
                                  name: "음식 섭취",
                                ),
                                LineSeries<GlucoseHistoryResponse, DateTime>(
                                  dataSource: getExerciseGlucoseHistories(),
                                  xValueMapper: (dto, _) => dto.dateTime,
                                  yValueMapper: (dto, _) => dto.sgv,
                                  width: 2,
                                  color: Colors.orange,
                                  markerSettings: MarkerSettings(isVisible: false),
                                  name: "운동할 때",
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
                              Container(width: 30, height: 3, color: Colors.yellow),
                              SizedBox(width: 10),
                              Text(
                                "식사 하기",
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
                                "운동 하기",
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
                                "가만히 있기",
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
      onTap: () => setState(() => _interval = value),
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
