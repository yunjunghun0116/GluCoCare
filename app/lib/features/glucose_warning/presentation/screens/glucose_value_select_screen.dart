import 'package:app/shared/widgets/common_button.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../../../../shared/constants/app_colors.dart';
import '../../../../shared/widgets/common_app_bar.dart';

class GlucoseValueSelectScreen extends StatefulWidget {
  final String title;
  final int minValue;
  final int maxValue;

  const GlucoseValueSelectScreen({super.key, required this.title, required this.minValue, required this.maxValue});

  @override
  State<GlucoseValueSelectScreen> createState() => _GlucoseValueSelectScreenState();
}

class _GlucoseValueSelectScreenState extends State<GlucoseValueSelectScreen> {
  late int _count;
  late int _selectedValue;

  @override
  void initState() {
    super.initState();
    _count = ((widget.maxValue - widget.minValue) / 5).toInt() + 1;
    _selectedValue = widget.minValue;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(title: widget.title),
      backgroundColor: AppColors.backgroundColor,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(height: 20),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: Row(
              children: [
                Text(
                  "알림 혈당 값",
                  style: TextStyle(
                    fontSize: 16,
                    height: 20 / 16,
                    color: AppColors.mainColor,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Spacer(),
                Text(
                  "${widget.minValue} mg/dL 이상",
                  style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray400Color),
                ),
              ],
            ),
          ),
          SizedBox(
            width: double.infinity,
            height: 150,
            child: CupertinoPicker(
              itemExtent: 50,
              onSelectedItemChanged: (item) {
                _selectedValue = (widget.minValue + item * 5);
              },
              children: List.generate(
                _count,
                (index) => Container(
                  alignment: Alignment.center,
                  child: Text(
                    "${widget.minValue + index * 5}",
                    style: TextStyle(
                      fontSize: 20,
                      height: 28 / 20,
                      color: AppColors.mainColor,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ),
            ),
          ),
          SizedBox(height: 50),
          CommonButton(value: true, onTap: () => Navigator.pop(context, _selectedValue), title: "설정"),
        ],
      ),
    );
  }
}
