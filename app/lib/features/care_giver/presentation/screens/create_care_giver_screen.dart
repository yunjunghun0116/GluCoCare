import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/features/care_giver/data/models/care_giver_response_dto.dart';
import 'package:app/features/care_giver/presentation/providers.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:app/shared/widgets/common_app_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

class CreateCareGiverScreen extends ConsumerStatefulWidget {
  const CreateCareGiverScreen({super.key});

  @override
  ConsumerState<CreateCareGiverScreen> createState() => _CreateCareGiverScreenState();
}

class _CreateCareGiverScreenState extends ConsumerState<CreateCareGiverScreen> {
  var _careGivers = <CareGiverResponseDto>[];

  void selectCareReceiver(CareGiverResponseDto careGiver) async {
    await LocalRepository().save<int>(LocalRepositoryKey.lateCareGiverId, careGiver.id);
    if (!mounted) return;
    Navigator.pop(context);
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
      appBar: CommonAppBar(title: "Care Receiver 등록"),
      backgroundColor: AppColors.backgroundColor,
      body: Column(children: [..._careGivers.map((careGiver) => getCareGiverCard(careGiver))]),
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
                    style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray800Color),
                  ),
                  Text(
                    "ID : ${careGiver.patientId}, 이름 : ${careGiver.patientName}",
                    style: TextStyle(
                      fontSize: 16,
                      height: 20 / 16,
                      color: AppColors.fontGray800Color,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
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
