import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/features/glucose_history/presentation/screens/glucose_screen.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/data/repositories/local_repository.dart';
import '../../care/data/models/care_relation_response.dart';
import '../../care/presentation/providers.dart';
import '../../care/presentation/screens/care_relation_screen.dart';

class HomeScreen extends ConsumerStatefulWidget {
  const HomeScreen({super.key});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen> {
  CareRelationResponse? careRelation;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      careRelationsInitialize();
    });
  }

  Future<void> careRelationsInitialize() async {
    try {
      var lateCareRelationId = LocalRepository().read<int>(LocalRepositoryKey.lateCareRelationId);
      var result = await ref.read(careRelationControllerProvider.notifier).getCareRelation(lateCareRelationId);
      if (result == null) throw CustomException(ExceptionMessage.badRequest);
      setState(() => careRelation = result);
    } on CustomException catch (e) {
      var result = await ref.read(careRelationControllerProvider.notifier).getAllCareRelations();
      if (result == null || result.isEmpty) return;
      await LocalRepository().save<int>(LocalRepositoryKey.lateCareRelationId, result.first.id);
      setState(() => careRelation = result.first);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        GestureDetector(
          onTap: () async {
            var result = await Navigator.push<int?>(context, MaterialPageRoute(builder: (_) => CareGiverScreen()));
            if (result == null) return;
            if (careRelation?.id == result) return;
            await careRelationsInitialize();
          },
          child: Container(
            color: AppColors.mainColor,
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
            child: Row(
              children: [
                Text(
                  careRelation?.patientName ?? "함께 혈당을 관리할 사람을 선택해 주세요.",
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
        if (careRelation != null)
          Expanded(
            child: GlucoseScreen(key: ValueKey(careRelation), careRelation: careRelation!),
          ),
      ],
    );
  }
}
