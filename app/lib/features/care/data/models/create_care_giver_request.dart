class CreateCareGiverRequest {
  final int patientId;
  final String patientName;

  CreateCareGiverRequest({required this.patientId, required this.patientName});

  @override
  String toString() {
    return 'CreateCareGiverRequest{patientId: $patientId, patientName: $patientName}';
  }
}
