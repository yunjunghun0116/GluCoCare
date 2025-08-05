import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/features/care_giver/data/models/care_giver_response_dto.dart';
import 'package:app/features/care_giver/presentation/providers.dart';
import 'package:app/features/care_giver/presentation/screens/create_care_giver_screen.dart';
import 'package:app/features/glucose_history/presentation/widgets/glucose_chart.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/data/repositories/local_repository.dart';

class HomeScreen extends ConsumerStatefulWidget {
  const HomeScreen({super.key});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen> {
  CareGiverResponseDto? careGiver;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      await careGiversInitialize();
    });
  }

  Future<void> careGiversInitialize() async {
    try {
      var lateCareGiverId = LocalRepository().read<int>(LocalRepositoryKey.lateCareGiverId);
      var result = await ref.read(careGiverControllerProvider.notifier).getCareGiver(lateCareGiverId);
      if (result == null) throw CustomException(ExceptionMessage.badRequest);
      setState(() => careGiver = result);
    } on CustomException catch (e) {
      var result = await ref.read(careGiverControllerProvider.notifier).getAllCareGiver();
      if (result == null || result.isEmpty) return;
      await LocalRepository().save<int>(LocalRepositoryKey.lateCareGiverId, result.first.id);
      setState(() => careGiver = result.first);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        GestureDetector(
          onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => CreateCareGiverScreen())),
          child: Container(
            color: AppColors.mainColor,
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
            child: Row(
              children: [
                Text(
                  careGiver?.patientName ?? "Care Receiver 를 선택해 주세요.",
                  style: TextStyle(
                    fontSize: 16,
                    height: 20 / 16,
                    fontWeight: FontWeight.bold,
                    color: AppColors.whiteColor,
                  ),
                ),
                SizedBox(width: 10),
                Icon(Icons.arrow_forward_ios, size: 16, color: AppColors.whiteColor),
              ],
            ),
          ),
        ),
        if (careGiver != null) GlucoseChart(patientId: careGiver!.patientId),
      ],
    );
  }
}
