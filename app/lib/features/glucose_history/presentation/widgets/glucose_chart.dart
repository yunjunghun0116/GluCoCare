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
  final Set<String> _labelShowSet = {};
  double _interval = 2.0;

  double calculateWidthForChart() {
    if (widget.records.isEmpty) return MediaQuery.of(context).size.width;

    // 시작 시간과 끝 시간 계산
    var minDate = widget.records.first.dateTime.subtract(Duration(hours: 3));
    var maxDate = widget.records.last.dateTime.add(Duration(hours: 3)).difference(DateTime.now()).isNegative
        ? widget.records.last.dateTime.add(Duration(hours: 3))
        : DateTime.now();

    // 전체 시간 범위(시간 단위)
    var totalHours = maxDate.difference(minDate).inHours;

    // interval당 픽셀 수
    var pixelsPerInterval = 60.0;

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

  @override
  Widget build(BuildContext context) {
    _labelShowSet.clear();
    var maxDate = widget.records.last.dateTime.add(Duration(hours: 3)).difference(DateTime.now()).isNegative
        ? widget.records.last.dateTime.add(Duration(hours: 3))
        : DateTime.now();

    WidgetsBinding.instance.addPostFrameCallback((_) async {
      _controller.jumpTo(_controller.position.maxScrollExtent);
    });

    return ListView(
      physics: ClampingScrollPhysics(),
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 5),
          child: Text(
            "간격 설정하기",
            style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.mainColor, fontWeight: FontWeight.bold),
          ),
        ),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Row(children: [1.0, 2.0, 3.0, 6.0].map((number) => getIntervalButton(number)).toList()),
        ),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 25),
          height: 40,
          child: Text(
            "혈당\n(mg/dL)",
            textAlign: TextAlign.center,
            style: TextStyle(fontSize: 10, color: AppColors.fontGray600Color),
          ),
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
                    SizedBox(height: 40),
                  ],
                ),
              ),
              Expanded(
                child: SingleChildScrollView(
                  controller: _controller,
                  scrollDirection: Axis.horizontal,
                  child: SizedBox(
                    width: calculateWidthForChart(),
                    height: 400,
                    child: SfCartesianChart(
                      primaryXAxis: DateTimeAxis(
                        minimum: widget.records.first.dateTime.subtract(Duration(hours: 3)),
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
                            var monthDayString = DateFormat('MM/dd').format(date);
                            var hourMinuteString = DateFormat('HH:mm').format(date);
                            return ChartAxisLabel(
                              "$monthDayString\n$hourMinuteString",
                              TextStyle(fontSize: 12, color: AppColors.fontGray800Color, fontWeight: FontWeight.bold),
                            );
                          }
                          return ChartAxisLabel(
                            DateFormat('HH:mm').format(date),
                            TextStyle(fontSize: 12, color: AppColors.fontGray800Color, fontWeight: FontWeight.bold),
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
                      tooltipBehavior: TooltipBehavior(
                        enable: true,
                        builder: (dynamic data, dynamic point, dynamic series, int pointIndex, int seriesIndex) {
                          final response = data as GlucoseHistoryResponse;
                          final dateStr = DateFormat('yyyy년 MM월 dd일').format(response.dateTime);
                          final timeStr = DateFormat('HH시 mm분').format(response.dateTime);
                          return Container(
                            padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                            decoration: BoxDecoration(color: Colors.black87, borderRadius: BorderRadius.circular(6)),
                            child: Column(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Text(dateStr, style: TextStyle(color: Colors.white, fontSize: 12)),
                                Text(timeStr, style: TextStyle(color: Colors.white, fontSize: 12)),
                                Text(
                                  '혈당: ${response.sgv}',
                                  style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                      series: <CartesianSeries>[
                        LineSeries<GlucoseHistoryResponse, DateTime>(
                          dataSource: widget.records,
                          xValueMapper: (dto, _) => dto.dateTime,
                          yValueMapper: (dto, _) => dto.sgv,
                          width: 2,
                          markerSettings: MarkerSettings(isVisible: false, width: 3, height: 3),
                        ),
                      ],
                    ),
                  ),
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
}
