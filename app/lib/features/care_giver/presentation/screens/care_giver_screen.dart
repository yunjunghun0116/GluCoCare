import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/features/care_giver/data/models/care_giver_response_dto.dart';
import 'package:app/features/care_giver/presentation/providers.dart';
import 'package:app/features/care_giver/presentation/screens/create_care_giver_screen.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:app/shared/widgets/common_app_bar.dart';
import 'package:app/shared/widgets/common_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../shared/widgets/input_screen.dart';

class CareGiverScreen extends ConsumerStatefulWidget {
  const CareGiverScreen({super.key});

  @override
  ConsumerState<CareGiverScreen> createState() => _CareGiverScreenState();
}

class _CareGiverScreenState extends ConsumerState<CareGiverScreen> {
  var _careGivers = <CareGiverResponseDto>[];

  void selectCareReceiver(CareGiverResponseDto careGiver) async {
    await LocalRepository().save<int>(LocalRepositoryKey.lateCareGiverId, careGiver.id);
    if (!mounted) return;
    Navigator.pop(context, careGiver.id);
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      careGiversInitialize();
    });
  }

  Future<void> careGiversInitialize() async {
    var result = await ref.read(careGiverControllerProvider.notifier).getAllCareGiver();
    if (result == null) return;
    setState(() => _careGivers = result);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(title: "Care Receiver"),
      backgroundColor: AppColors.backgroundColor,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CommonButton(
            value: true,
            onTap: () async {
              var result = await Navigator.push<bool?>(
                context,
                MaterialPageRoute(builder: (inputScreenContext) => CreateCareGiverScreen()),
              );
              if (result == null) return;
              if (!result) return;
              careGiversInitialize();
            },
            title: "등록하기",
          ),
          SizedBox(height: 20),
          Container(width: double.infinity, height: 1, color: AppColors.mainColor),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
            child: Text(
              "등록된 Care Receiver 목록",
              style: TextStyle(fontSize: 16, height: 20 / 16, fontWeight: FontWeight.bold, color: AppColors.mainColor),
            ),
          ),
          Container(width: double.infinity, height: 1, color: AppColors.mainColor),
          ..._careGivers.map((careGiver) => getCareGiverCard(careGiver)),
        ],
      ),
    );
  }

  Widget getCareGiverCard(CareGiverResponseDto careGiver) {
    return GestureDetector(
      onTap: () => selectCareReceiver(careGiver),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        decoration: BoxDecoration(
          border: Border(bottom: BorderSide(color: AppColors.mainColor)),
          color: AppColors.backgroundColor,
        ),
        child: Row(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    "Care Receiver 정보",
                    style: TextStyle(
                      fontSize: 14,
                      height: 20 / 14,
                      color: AppColors.subColor3,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  SizedBox(height: 4),
                  Text(
                    "ID : ${careGiver.patientId}, 이름 : ${careGiver.patientName}",
                    style: TextStyle(
                      fontSize: 16,
                      height: 20 / 16,
                      color: AppColors.subColor2,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  SizedBox(height: 4),
                ],
              ),
            ),
            Icon(Icons.arrow_forward_ios, size: 16, color: AppColors.mainColor),
          ],
        ),
      ),
    );
  }
}
