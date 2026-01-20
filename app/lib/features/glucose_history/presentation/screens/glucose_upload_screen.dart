import 'dart:developer';

import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/features/glucose_history/data/models/create_glucose_history_request.dart';
import 'package:app/features/glucose_history/presentation/providers.dart';
import 'package:app/shared/utils/local_util.dart';
import 'package:app/shared/widgets/common_button.dart';
import 'package:app/shared/widgets/common_date_picker.dart';
import 'package:app/shared/widgets/common_time_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../shared/constants/app_colors.dart';
import '../../../../shared/constants/local_repository_key.dart';
import '../../../../shared/widgets/common_app_bar.dart';
import '../../../../shared/widgets/common_text_field.dart';

class GlucoseUploadScreen extends ConsumerStatefulWidget {
  const GlucoseUploadScreen({super.key});

  @override
  ConsumerState<GlucoseUploadScreen> createState() => _GlucoseUploadScreenState();
}

class _GlucoseUploadScreenState extends ConsumerState<GlucoseUploadScreen> {
  final TextEditingController _valueController = TextEditingController();
  DateTime? _date;
  TimeOfDay? _timeOfDay;

  bool get canAddGlucose => _date != null && _timeOfDay != null && _valueController.text.isNotEmpty;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => FocusScope.of(context).unfocus(),
      child: Scaffold(
        appBar: CommonAppBar(title: "직접 측정 혈당 추가하기"),
        backgroundColor: AppColors.backgroundColor,
        body: Column(
          children: [
            SizedBox(height: 20),
            Center(
              child: Text(
                "직접 측정한 혈당 수치를 기록해 주세요.",
                style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray600Color),
              ),
            ),
            SizedBox(height: 20),
            Row(
              children: [
                SizedBox(width: 80, child: Text("날짜", textAlign: TextAlign.center)),
                Expanded(
                  child: GestureDetector(
                    onTap: () async {
                      var selectedDate = await showModalBottomSheet<DateTime?>(
                        context: context,
                        isScrollControlled: true,
                        backgroundColor: Colors.transparent,
                        builder: (context) => const CommonDatePicker(),
                      );
                      if (selectedDate == null) return;
                      setState(() => _date = selectedDate);
                    },
                    child: Container(
                      alignment: Alignment.center,
                      margin: const EdgeInsets.symmetric(horizontal: 20),
                      padding: const EdgeInsets.symmetric(horizontal: 18),
                      width: double.infinity,
                      height: 52,
                      decoration: BoxDecoration(
                        color: AppColors.fontGray50Color,
                        borderRadius: BorderRadius.circular(15),
                      ),
                      child: Text(_date != null ? "${_date!.year}년 ${_date!.month}월 ${_date!.day}일" : "날짜를 입력해 주세요."),
                    ),
                  ),
                ),
              ],
            ),
            SizedBox(height: 20),
            Row(
              children: [
                SizedBox(width: 80, child: Text("시간", textAlign: TextAlign.center)),
                Expanded(
                  child: GestureDetector(
                    onTap: () async {
                      var selectedTimeOfDay = await showModalBottomSheet<TimeOfDay?>(
                        context: context,
                        isScrollControlled: true,
                        backgroundColor: Colors.transparent,
                        builder: (context) => const CommonTimePicker(),
                      );
                      if (selectedTimeOfDay == null) return;
                      setState(() => _timeOfDay = selectedTimeOfDay);
                    },
                    child: Container(
                      alignment: Alignment.center,
                      margin: const EdgeInsets.symmetric(horizontal: 20),
                      padding: const EdgeInsets.symmetric(horizontal: 18),
                      width: double.infinity,
                      height: 52,
                      decoration: BoxDecoration(
                        color: AppColors.fontGray50Color,
                        borderRadius: BorderRadius.circular(15),
                      ),
                      child: Text(_timeOfDay != null ? "${_timeOfDay!.hour}시 ${_timeOfDay!.minute}분" : "시간을 입력해 주세요."),
                    ),
                  ),
                ),
              ],
            ),
            SizedBox(height: 20),
            Row(
              children: [
                SizedBox(width: 80, child: Text("혈당수치\n(mg/dL)", textAlign: TextAlign.center)),
                Expanded(
                  child: CommonTextField(
                    controller: _valueController,
                    hintText: "혈당 수치(mg/dL)",
                    inputType: TextInputType.number,
                    onChanged: (text) => setState(() {}),
                  ),
                ),
              ],
            ),
            SizedBox(height: 50),
            CommonButton(
              value: canAddGlucose,
              onTap: () async {
                if (!canAddGlucose) return;
                try {
                  var value = int.parse(_valueController.text);
                  if (value < 50) throw Exception();
                  if (value > 400) throw Exception();

                  var date = DateTime(_date!.year, _date!.month, _date!.day, _timeOfDay!.hour, _timeOfDay!.minute);
                  var lateCareRelationId = LocalRepository().read(LocalRepositoryKey.lateCareRelationId);
                  var request = CreateGlucoseHistoryRequest(
                    careRelationId: lateCareRelationId,
                    dateTime: date,
                    sgv: value,
                  );

                  var result = await ref.read(glucoseHistoryControllerProvider.notifier).createGlucoseHistory(request);
                  if (result) {
                    if (!context.mounted) return;
                    LocalUtil.showMessage(context, message: "혈당이 추가되었습니다.");
                    Navigator.pop(context);
                  }
                } catch (e) {
                  log(e.toString());
                  throw CustomException(ExceptionMessage.invalidGlucoseRange);
                }
              },
              title: "추가하기",
            ),
          ],
        ),
      ),
    );
  }
}
