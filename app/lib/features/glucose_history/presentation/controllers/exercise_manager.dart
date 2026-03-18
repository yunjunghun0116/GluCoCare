import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../shared/constants/my_exercise.dart';

class ExerciseManager extends StateNotifier<MyExercise> {
  ExerciseManager() : super(MyExercise.walking);

  void update(MyExercise exercise) {
    state = exercise;
  }
}
