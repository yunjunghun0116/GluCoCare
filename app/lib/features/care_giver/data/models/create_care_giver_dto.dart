class CreateCareGiverDto {
  final int patientId;
  final String patientName;

  CreateCareGiverDto({required this.patientId, required this.patientName});

  @override
  String toString() {
    return 'CreateCareGiverDto{patientId: $patientId, patientName: $patientName}';
  }
}
