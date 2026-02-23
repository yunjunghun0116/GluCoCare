import 'package:app/core/base/base_controller.dart';
import 'package:app/core/providers.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'controllers/care_relation_controller.dart';

// --- Controller Providers ---
final careRelationControllerProvider = StateNotifierProvider<CareRelationController, BaseState>((ref) {
  var dio = ref.read(dioProvider);
  return CareRelationController(BaseState(), dio);
});
