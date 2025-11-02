import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class CommonTimePicker extends StatefulWidget {
  const CommonTimePicker({super.key});

  @override
  State<CommonTimePicker> createState() => _CommonTimePickerState();
}

class _CommonTimePickerState extends State<CommonTimePicker> {
  late final FixedExtentScrollController _hourController;
  late final FixedExtentScrollController _minuteController;
  late int _hour;
  late int _minute;

  @override
  void initState() {
    super.initState();
    var nowDate = DateTime.now();
    _hour = nowDate.hour;
    _minute = nowDate.minute;
    _hourController = FixedExtentScrollController(initialItem: nowDate.hour);
    _minuteController = FixedExtentScrollController(initialItem: nowDate.minute);
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
                      scrollController: _hourController,
                      itemExtent: 35,
                      onSelectedItemChanged: (int index) => _hour = index,
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
                        24,
                        (index) => Container(
                          margin: const EdgeInsets.only(left: 30),
                          alignment: Alignment.center,
                          child: Text('$index시', style: const TextStyle(fontSize: 24)),
                        ),
                      ),
                    ),
                  ),
                  Expanded(
                    child: CupertinoPicker(
                      useMagnifier: true,
                      scrollController: _minuteController,
                      itemExtent: 35,
                      onSelectedItemChanged: (int index) => _minute = index,
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
                        60,
                        (index) => Container(
                          margin: const EdgeInsets.only(right: 30),
                          alignment: Alignment.center,
                          child: Text('$index분', style: const TextStyle(fontSize: 24)),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            GestureDetector(
              onTap: () {
                var selectedTime = TimeOfDay(hour: _hour, minute: _minute);
                if (selectedTime.hour != _hour || selectedTime.minute != _minute) {
                  throw CustomException(ExceptionMessage.invalidTime);
                }
                Navigator.pop(context, selectedTime);
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
