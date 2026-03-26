import 'package:app/core/base/base_controller.dart';
import 'package:app/core/providers.dart';
import 'package:app/features/mission/presentation/controllers/mission_controller.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// --- Controller Providers ---
final missionControllerProvider = StateNotifierProvider<MissionController, BaseState>((ref) {
  var dio = ref.read(dioProvider);
  return MissionController(BaseState(), dio);
});
