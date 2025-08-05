import 'package:app/features/glucose_history/data/models/glucose_history_response_dto.dart';
import 'package:app/features/glucose_history/presentation/providers.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

class GlucoseChart extends ConsumerStatefulWidget {
  final int patientId;

  const GlucoseChart({super.key, required this.patientId});

  @override
  ConsumerState<GlucoseChart> createState() => _GlucoseChartState();
}

class _GlucoseChartState extends ConsumerState<GlucoseChart> {
  final _controller = ScrollController();
  var labelShowSet = <String>{};
  var interval = 2.0;
  var records = <GlucoseHistoryResponseDto>[];

  double calculateWidthForChart(int dataLength) {
    var baseWidth = MediaQuery.of(context).size.width + 200;
    var widthPerPoint = interval;
    return baseWidth + widthPerPoint * records.length;
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      await setGlucoseHistories();
    });
  }

  Future<void> setGlucoseHistories() async {
    var result = await ref.read(glucoseHistoryControllerProvider.notifier).getAllGlucoseHistories(widget.patientId);
    if (result == null || result.isEmpty) return;
    setState(() => records = result);
  }

  String getKey(DateTime date) {
    return "${date.month}-${date.day}";
  }

  @override
  Widget build(BuildContext context) {
    if (records.isEmpty) return Text('데이터가 없습니다.');
    labelShowSet.clear();
    var maxDate = records.last.dateTime.add(Duration(hours: 12)).difference(DateTime.now()).isNegative
        ? records.last.dateTime.add(Duration(hours: 12))
        : DateTime.now();

    WidgetsBinding.instance.addPostFrameCallback((_) async {
      _controller.jumpTo(_controller.position.maxScrollExtent);
    });

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(height: 10),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 5),
          child: Text(
            "간격 설정하기",
            style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.mainColor, fontWeight: FontWeight.bold),
          ),
        ),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
          child: Row(children: [1.0, 2.0, 3.0, 6.0].map((number) => getIntervalButton(number)).toList()),
        ),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: SingleChildScrollView(
            controller: _controller,
            scrollDirection: Axis.horizontal,
            child: SizedBox(
              width: calculateWidthForChart(records.length),
              height: 300,
              child: SfCartesianChart(
                primaryXAxis: DateTimeAxis(
                  minimum: records.first.dateTime.subtract(Duration(hours: 12)),
                  maximum: maxDate,
                  interval: interval,
                  labelAlignment: LabelAlignment.center,
                  labelPosition: ChartDataLabelPosition.outside,
                  axisLabelFormatter: (AxisLabelRenderDetails details) {
                    var date = DateTime.fromMillisecondsSinceEpoch(details.value as int);
                    var key = getKey(date);
                    var isAlreadyShown = labelShowSet.contains(key);

                    if (!isAlreadyShown) {
                      labelShowSet.add(key);
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
                primaryYAxis: NumericAxis(minimum: 50, maximum: 300, interval: 50),
                tooltipBehavior: TooltipBehavior(
                  enable: true,
                  builder: (dynamic data, dynamic point, dynamic series, int pointIndex, int seriesIndex) {
                    final dto = data as GlucoseHistoryResponseDto;
                    final dateStr = DateFormat('yyyy년 MM월 dd일').format(dto.dateTime);
                    final timeStr = DateFormat('HH시 mm분').format(dto.dateTime);
                    return Container(
                      padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(color: Colors.black87, borderRadius: BorderRadius.circular(6)),
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text(dateStr, style: TextStyle(color: Colors.white, fontSize: 12)),
                          Text(timeStr, style: TextStyle(color: Colors.white, fontSize: 12)),
                          Text(
                            '혈당: ${dto.sgv}',
                            style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                          ),
                        ],
                      ),
                    );
                  },
                ),
                series: <CartesianSeries>[
                  LineSeries<GlucoseHistoryResponseDto, DateTime>(
                    dataSource: records,
                    xValueMapper: (dto, _) => dto.dateTime,
                    yValueMapper: (dto, _) => dto.sgv,
                    width: 2,
                    markerSettings: MarkerSettings(isVisible: true, width: 4, height: 4),
                  ),
                ],
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget getIntervalButton(double value) {
    var isSelected = interval == value;
    return GestureDetector(
      onTap: () => setState(() => interval = value),
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
