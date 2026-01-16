import 'package:app/core/base/base_controller.dart';
import 'package:app/core/providers.dart';
import 'package:app/features/member/presentation/controllers/fcm_token_controller.dart';
import 'package:app/features/member/presentation/controllers/member_controller.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// --- Controller Providers ---
final memberControllerProvider = StateNotifierProvider<MemberController, BaseState>((ref) {
  var dio = ref.read(dioProvider);
  return MemberController(BaseState(), dio);
});
final fcmTokenControllerProvider = StateNotifierProvider<FCMTokenController, BaseState>((ref) {
  var dio = ref.read(dioProvider);
  return FCMTokenController(BaseState(), dio);
});
