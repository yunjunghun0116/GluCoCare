import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/shared/constants/app_colors.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:app/shared/widgets/common_app_bar.dart';
import 'package:app/shared/widgets/common_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../data/models/care_relation_response.dart';
import '../providers.dart';
import '../widgets/care_relation_button.dart';
import 'create_care_relation_screen.dart';

class CareGiverScreen extends ConsumerStatefulWidget {
  const CareGiverScreen({super.key});

  @override
  ConsumerState<CareGiverScreen> createState() => _CareGiverScreenState();
}

class _CareGiverScreenState extends ConsumerState<CareGiverScreen> {
  List<CareRelationResponse> _careRelations = [];

  void selectCareReceiver(CareRelationResponse careRelation) async {
    await LocalRepository().save<int>(LocalRepositoryKey.lateCareRelationId, careRelation.id);
    if (!mounted) return;
    Navigator.pop(context, careRelation.id);
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      careGiversInitialize();
    });
  }

  Future<void> careGiversInitialize() async {
    var result = await ref.read(careRelationControllerProvider.notifier).getAllCareRelations();
    if (result == null) return;
    setState(() => _careRelations = result);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(title: "혈당 관리"),
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
          Container(
            width: double.infinity,
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
            color: AppColors.mainColor,
            child: Text(
              "등록된 사람들",
              style: TextStyle(fontSize: 16, height: 20 / 16, fontWeight: FontWeight.bold, color: AppColors.whiteColor),
            ),
          ),
          ..._careRelations.map(
            (careRelation) =>
                CareRelationButton(careRelation: careRelation, onTap: () => selectCareReceiver(careRelation)),
          ),
        ],
      ),
    );
  }
}
