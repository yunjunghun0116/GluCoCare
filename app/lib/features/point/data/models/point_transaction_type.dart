import 'package:json_annotation/json_annotation.dart';

enum PointTransactionType {
  @JsonValue('EARN')
  earn,
  @JsonValue('SPEND')
  spend,
  @JsonValue('ADMIN_ADJUST')
  adminAdjust,
}
