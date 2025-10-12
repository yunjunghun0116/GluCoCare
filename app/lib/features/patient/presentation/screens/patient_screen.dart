import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/features/patient/data/models/read_patient_response.dart';
import 'package:app/features/patient/presentation/providers.dart';
import 'package:app/shared/constants/app_reg_exp.dart';
import 'package:app/shared/widgets/common_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../shared/constants/app_colors.dart';
import '../../../../shared/widgets/common_app_bar.dart';
import '../../../../shared/widgets/common_text_field.dart';

class PatientScreen extends ConsumerStatefulWidget {
  const PatientScreen({super.key});

  @override
  ConsumerState<PatientScreen> createState() => _PatientScreenState();
}

class _PatientScreenState extends ConsumerState<PatientScreen> {
  final _nameController = TextEditingController();

  bool existsPatient = false;
  ReadPatientResponse? patientResponse;

  @override
  void initState() {
    super.initState();
    initializePatient();
  }

  void initializePatient() {
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      FocusScope.of(context).unfocus();

      existsPatient = await ref.read(patientController.notifier).existsPatients();
      if (existsPatient) {
        patientResponse = await ref.read(patientController.notifier).readPatient();
        _nameController.text = patientResponse?.name ?? "";
      }
      setState(() {});
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(title: "Care Receiver ${existsPatient ? "정보" : "등록"}", onBack: () => Navigator.pop(context)),
      backgroundColor: AppColors.backgroundColor,
      body: Builder(
        builder: (context) {
          if (existsPatient) {
            return readCareReceiverContainer();
          }
          return registerCareReceiverForm();
        },
      ),
    );
  }

  Widget readCareReceiverContainer() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 30),
      child: RichText(
        text: TextSpan(
          style: TextStyle(fontSize: 16, height: 20 / 16, color: AppColors.mainColor, fontWeight: FontWeight.bold),
          children: [
            TextSpan(text: "고유 ID : ${patientResponse?.id}\n\n"),
            TextSpan(text: "성명\n"),
            TextSpan(
              text: "${patientResponse?.name ?? ""}\n\n",
              style: TextStyle(
                fontSize: 14,
                height: 20 / 14,
                color: AppColors.fontGray400Color,
                fontWeight: FontWeight.bold,
              ),
            ),
            TextSpan(text: "연속혈당측정기 SERVER URL\n"),
            TextSpan(
              text: patientResponse?.cgmServerUrl ?? "",
              style: TextStyle(
                fontSize: 14,
                height: 20 / 14,
                color: AppColors.fontGray400Color,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget registerCareReceiverForm() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
          child: Text(
            "성명",
            style: TextStyle(fontSize: 16, height: 20 / 16, color: AppColors.mainColor, fontWeight: FontWeight.bold),
          ),
        ),
        CommonTextField(controller: _nameController, hintText: "성명을 입력해 주세요."),
        SizedBox(height: 30),
        CommonButton(
          value: true,
          onTap: () async {
            if (!AppRegExp.nameRegExp.hasMatch(_nameController.text)) {
              throw CustomException(ExceptionMessage.wrongCareReceiverNameRegExp);
            }
            await ref.read(patientController.notifier).createPatient(_nameController.text);
            initializePatient();
          },
          title: "등록",
        ),
      ],
    );
  }
}
