import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:flutter/cupertino.dart';

class CommonDatePicker extends StatefulWidget {
  const CommonDatePicker({super.key});

  @override
  State<CommonDatePicker> createState() => _CommonDatePickerState();
}

class _CommonDatePickerState extends State<CommonDatePicker> {
  final int _yearCount = 2;

  late final FixedExtentScrollController _yearController;
  late final FixedExtentScrollController _monthController;
  late final FixedExtentScrollController _dayController;

  late int _year;
  late int _currentYear;
  late int _month;
  late int _day;

  @override
  void initState() {
    super.initState();
    DateTime nowDate = DateTime.now();
    _year = nowDate.year;
    _currentYear = nowDate.year;
    _month = nowDate.month;
    _day = nowDate.day;
    _yearController = FixedExtentScrollController();
    _monthController = FixedExtentScrollController(initialItem: _month - 1);
    _dayController = FixedExtentScrollController(initialItem: _day - 1);
  }

  @override
  Widget build(BuildContext context) {
    return ClipRRect(
      borderRadius: const BorderRadius.only(topLeft: Radius.circular(20), topRight: Radius.circular(20)),
      child: Container(
        height: 350,
        color: AppColors.whiteColor,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.symmetric(vertical: 10),
              alignment: Alignment.center,
              width: double.infinity,
              child: Container(width: 60, height: 4, color: AppColors.fontGray100Color),
            ),
            Expanded(
              child: Row(
                children: [
                  Expanded(
                    child: CupertinoPicker(
                      scrollController: _yearController,
                      itemExtent: 35,
                      onSelectedItemChanged: (int index) => _year = index + _currentYear,
                      selectionOverlay: Container(
                        margin: const EdgeInsets.only(left: 20),
                        height: 30,
                        decoration: BoxDecoration(
                          color: AppColors.mainColor.withAlpha(0xFF444444),
                          borderRadius: BorderRadius.only(topLeft: Radius.circular(5), bottomLeft: Radius.circular(5)),
                        ),
                      ),
                      offAxisFraction: -0.5,
                      squeeze: 1.2,
                      children: List.generate(
                        _yearCount,
                        (index) => Container(
                          alignment: Alignment.centerRight,
                          child: Text('${index + _currentYear}년', style: const TextStyle(fontSize: 24)),
                        ),
                      ),
                    ),
                  ),
                  Expanded(
                    child: CupertinoPicker(
                      useMagnifier: true,
                      scrollController: _monthController,
                      itemExtent: 35,
                      onSelectedItemChanged: (int index) => _month = index + 1,
                      selectionOverlay: Container(
                        height: 30,
                        decoration: BoxDecoration(color: AppColors.mainColor.withAlpha(0xFF444444)),
                      ),
                      squeeze: 1.2,
                      children: List.generate(
                        12,
                        (index) => Container(
                          alignment: Alignment.center,
                          child: Text('${index + 1}월', style: const TextStyle(fontSize: 24)),
                        ),
                      ),
                    ),
                  ),
                  Expanded(
                    child: CupertinoPicker(
                      scrollController: _dayController,
                      itemExtent: 35,
                      onSelectedItemChanged: (int index) => _day = index + 1,
                      selectionOverlay: Container(
                        margin: const EdgeInsets.only(right: 20),
                        height: 30,
                        decoration: BoxDecoration(
                          color: AppColors.mainColor.withAlpha(0xFF444444),
                          borderRadius: BorderRadius.only(
                            topRight: Radius.circular(5),
                            bottomRight: Radius.circular(5),
                          ),
                        ),
                      ),
                      offAxisFraction: 0.5,
                      squeeze: 1.2,
                      children: List.generate(
                        31,
                        (index) => Container(
                          alignment: Alignment.centerLeft,
                          child: Text('${index + 1}일', style: const TextStyle(fontSize: 24)),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            GestureDetector(
              onTap: () {
                var selectedDate = DateTime(_year, _month, _day);
                if (selectedDate.year != _year || selectedDate.month != _month || selectedDate.day != _day) {
                  throw CustomException(ExceptionMessage.invalidDate);
                }
                Navigator.pop(context, selectedDate);
              },
              child: Container(
                alignment: Alignment.center,
                width: double.infinity,
                color: AppColors.mainColor,
                child: SafeArea(
                  child: Container(
                    padding: const EdgeInsets.symmetric(vertical: 18),
                    child: Text(
                      '완료',
                      style: TextStyle(fontSize: 18, color: AppColors.whiteColor, fontWeight: FontWeight.bold),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
