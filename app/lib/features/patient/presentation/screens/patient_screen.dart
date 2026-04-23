import 'package:app/features/patient/data/models/read_patient_response.dart';
import 'package:app/features/patient/presentation/providers.dart';
import 'package:app/shared/utils/local_util.dart';
import 'package:app/shared/widgets/common_button.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../shared/constants/app_colors.dart';
import '../../../../shared/widgets/common_app_bar.dart';

class PatientScreen extends ConsumerStatefulWidget {
  const PatientScreen({super.key});

  @override
  ConsumerState<PatientScreen> createState() => _PatientScreenState();
}

class _PatientScreenState extends ConsumerState<PatientScreen> {
  final _nameController = TextEditingController();
  late final TapGestureRecognizer _copyRecognizer;

  bool existsPatient = false;
  ReadPatientResponse? patientResponse;

  String getDexcomServerUrl() {
    if (patientResponse == null) return "";
    return "https://${patientResponse!.accessCode}@assured-mastodon-basically.ngrok-free.app/${patientResponse!.id}/api/v1";
  }

  @override
  void initState() {
    super.initState();
    _copyRecognizer = TapGestureRecognizer()
      ..onTap = () {
        var url = getDexcomServerUrl();
        Clipboard.setData(ClipboardData(text: url));
        if (!mounted) return;
        LocalUtil.showMessage(context, message: "URL이 클립보드에 복사됐습니다.");
      };
    initializePatient();
  }

  void initializePatient() {
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      FocusScope.of(context).unfocus();

      existsPatient = await ref.read(patientControllerProvider.notifier).readIsPatient();
      if (existsPatient) {
        patientResponse = await ref.read(patientControllerProvider.notifier).readPatientInformation();
        _nameController.text = patientResponse?.name ?? "";
      }

      setState(() {});
    });
  }

  @override
  void dispose() {
    _copyRecognizer.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(title: "혈당 관리 ${existsPatient ? "정보" : "등록"}", onBack: () => Navigator.pop(context)),
      backgroundColor: AppColors.backgroundColor,
      body: Builder(
        builder: (context) {
          if (existsPatient) {
            return readCareRelationContainer();
          }
          return registerCareRelationForm();
        },
      ),
    );
  }

  Widget readCareRelationContainer() {
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
            TextSpan(text: "xDrip+ Server URL\n"),
            TextSpan(
              text: getDexcomServerUrl(),
              recognizer: _copyRecognizer,
              style: TextStyle(
                fontSize: 14,
                height: 20 / 14,
                color: AppColors.fontGray400Color,
                fontWeight: FontWeight.bold,
                decoration: TextDecoration.underline,
              ),
            ),
            WidgetSpan(child: SizedBox(width: 6)),
            WidgetSpan(
              child: GestureDetector(
                onTap: _copyRecognizer.onTap,
                child: Icon(Icons.copy_rounded, size: 20, color: AppColors.fontGray400Color),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget registerCareRelationForm() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(height: 10),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Text(
            "설명",
            style: TextStyle(fontSize: 16, height: 20 / 16, color: AppColors.mainColor, fontWeight: FontWeight.bold),
          ),
        ),
        SizedBox(height: 10),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Text(
            "혈당 변화를 기록하고,나 뿐만 아니라\n가족이나 다른 사람들과 공유함으로써 혈당을\n더 잘 이해하고 관리하기 위한 설정입니다.",
            style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray600Color),
          ),
        ),
        SizedBox(height: 30),
        CommonButton(
          value: true,
          onTap: () async {
            await ref.read(patientControllerProvider.notifier).updateToPatient();
            initializePatient();
          },
          title: "등록",
        ),
      ],
    );
  }
}
