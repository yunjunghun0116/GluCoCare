import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/widgets/common_app_bar.dart';
import 'package:app/shared/widgets/common_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../shared/widgets/common_text_field.dart';
import '../../data/models/create_care_relation_request.dart';
import '../providers.dart';

class CreateCareGiverScreen extends ConsumerStatefulWidget {
  const CreateCareGiverScreen({super.key});

  @override
  ConsumerState<CreateCareGiverScreen> createState() => _CreateCareGiverScreenState();
}

class _CreateCareGiverScreenState extends ConsumerState<CreateCareGiverScreen> {
  final TextEditingController _idController = TextEditingController();
  final TextEditingController _nameController = TextEditingController();

  void createCareGiver() async {
    try {
      var createCareGiverDto = CreateCareRelationRequest(
        patientId: int.parse(_idController.text),
        patientName: _nameController.text,
      );
      await ref.read(careRelationControllerProvider.notifier).createCareRelation(createCareGiverDto);
      if (!mounted) return;
      Navigator.pop(context, true);
    } catch (e) {
      throw CustomException(ExceptionMessage.invalidPatientInformation);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.backgroundColor,
      appBar: CommonAppBar(title: "함께 관리할 사람 등록"),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.all(16),
            child: Text(
              "함께 혈당을 관리할 사람의\n정보를 입력해 주세요.",
              style: TextStyle(
                fontSize: 16,
                height: 20 / 16,
                color: AppColors.fontGray800Color,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          SizedBox(height: 20),
          Row(
            children: [
              SizedBox(width: 80, child: Text("이름", textAlign: TextAlign.center)),
              Expanded(
                child: CommonTextField(
                  controller: _nameController,
                  hintText: "이름을 입력해 주세요.",
                  onChanged: (String str) => setState(() {}),
                ),
              ),
            ],
          ),
          SizedBox(height: 20),
          Row(
            children: [
              SizedBox(width: 80, child: Text("고유 ID", textAlign: TextAlign.center)),
              Expanded(
                child: CommonTextField(
                  controller: _idController,
                  hintText: "고유 ID를 입력해 주세요.",
                  onChanged: (String str) => setState(() {}),
                ),
              ),
            ],
          ),
          SizedBox(height: 20),
          CommonButton(value: true, onTap: () => createCareGiver(), title: "입력하기"),
        ],
      ),
    );
  }
}
