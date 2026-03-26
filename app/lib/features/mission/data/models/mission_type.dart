import 'package:json_annotation/json_annotation.dart';

enum MissionType {
  @JsonValue('TIME_IN_RANGE')
  timeInRange,
  @JsonValue('NO_HYPOGLYCEMIA')
  noHypoglycemia,
  @JsonValue('NO_HYPERGLYCEMIA')
  noHyperglycemia,
  @JsonValue('MORNING_NORMAL')
  morningNormal,
  @JsonValue('STABLE_GLUCOSE')
  stableGlucose,
}
