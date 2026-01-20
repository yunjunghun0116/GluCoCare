class CreateGlucoseHistoryRequest {
  final int careRelationId;
  final DateTime dateTime;
  final int sgv;

  CreateGlucoseHistoryRequest({required this.careRelationId, required this.dateTime, required this.sgv});
}
