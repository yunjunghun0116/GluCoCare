import 'package:app/features/glucose_history/presentation/providers.dart';
import 'package:app/shared/constants/my_exercise.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
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
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Center(
            child: Wrap(
              spacing: 10,
              runSpacing: 10,
              children: MyExercise.values.map((exercise) => _exerciseCard(exercise)).toList(),
            ),
          ),
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
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 150),
        curve: Curves.easeOut,
        padding: const EdgeInsets.symmetric(horizontal: 10),
        decoration: BoxDecoration(
          color: isSelected ? AppColors.exerciseSelectedBackgroundColor : AppColors.fontGray50Color,
          border: Border.all(color: isSelected ? AppColors.exerciseSelectedBorderColor : AppColors.fontGray100Color),
          borderRadius: BorderRadius.circular(10),
        ),
        child: Column(
          children: [
            Container(
              margin: const EdgeInsets.symmetric(vertical: 5),
              width: 40,
              height: 40,
              child: Image.asset(exercise.image, fit: BoxFit.contain),
            ),
            Text(
              exercise.name,
              style: TextStyle(
                fontSize: 14,
                height: 20 / 14,
                color: AppColors.fontGray600Color,
                fontWeight: FontWeight.bold,
              ),
            ),
            Text(
              "강도 ${exercise.met}",
              style: TextStyle(fontSize: 10, height: 14 / 10, color: AppColors.fontGray400Color),
            ),
            SizedBox(height: 10),
          ],
        ),
      ),
    );
  }
}
