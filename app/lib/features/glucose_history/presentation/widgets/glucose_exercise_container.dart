import 'package:app/features/glucose_history/presentation/providers.dart';
import 'package:app/shared/constants/my_exercise.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../shared/constants/app_colors.dart';

class GlucoseExerciseContainer extends ConsumerStatefulWidget {
  const GlucoseExerciseContainer({super.key});

  @override
  ConsumerState<GlucoseExerciseContainer> createState() => _GlucoseExerciseContainerState();
}

class _GlucoseExerciseContainerState extends ConsumerState<GlucoseExerciseContainer> {
  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(width: double.infinity, height: 1, color: AppColors.fontGray200Color),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
          child: Text(
            "이 운동을 했을 때, 혈당이 어떻게 변할까요?",
            style: TextStyle(
              fontSize: 14,
              height: 20 / 14,
              color: AppColors.fontGray900Color,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        GridView.count(
          shrinkWrap: true,
          physics: ClampingScrollPhysics(),
          crossAxisCount: 4,
          childAspectRatio: 2 / 3,
          padding: const EdgeInsets.symmetric(horizontal: 20),
          mainAxisSpacing: 10,
          crossAxisSpacing: 10,
          children: MyExercise.values.map((exercise) => _exerciseCard(exercise)).toList(),
        ),
      ],
    );
  }

  Widget _exerciseCard(MyExercise exercise) {
    var isSelected = ref.read(exerciseManagerProvider) == exercise;
    return GestureDetector(
      onTap: () {
        if (isSelected) return;
        setState(() => ref.read(exerciseManagerProvider.notifier).update(exercise));
      },
      behavior: HitTestBehavior.opaque,
      child: Container(
        decoration: BoxDecoration(
          color: isSelected ? AppColors.exerciseSelectedBackgroundColor : AppColors.fontGray50Color,
          border: Border.all(color: isSelected ? AppColors.exerciseSelectedBorderColor : AppColors.fontGray100Color),
          borderRadius: BorderRadius.circular(10),
        ),
        child: Column(
          children: [
            Container(
              padding: const EdgeInsets.symmetric(vertical: 5),
              width: 80,
              height: 80,
              child: Image.asset(exercise.image, fit: BoxFit.contain),
            ),
            Text(
              exercise.name,
              style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.fontGray600Color),
            ),
            Text(
              "강도 ${exercise.met}",
              style: TextStyle(fontSize: 12, height: 16 / 12, color: AppColors.fontGray400Color),
            ),
          ],
        ),
      ),
    );
  }
}
