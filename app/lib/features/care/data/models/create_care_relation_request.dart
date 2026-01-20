class CreateCareRelationRequest {
  final int patientId;
  final String patientName;

  CreateCareRelationRequest({required this.patientId, required this.patientName});

  @override
  String toString() {
    return 'CreateCareRelationRequest{patientId: $patientId, patientName: $patientName}';
  }
}
