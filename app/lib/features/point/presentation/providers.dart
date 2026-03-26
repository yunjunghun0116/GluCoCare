import 'package:app/core/base/base_controller.dart';
import 'package:app/core/providers.dart';
import 'package:app/features/point/presentation/controllers/point_controller.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// --- Controller Providers ---
final pointControllerProvider = StateNotifierProvider<PointController, BaseState>((ref) {
  var dio = ref.read(dioProvider);
  return PointController(BaseState(), dio);
});
