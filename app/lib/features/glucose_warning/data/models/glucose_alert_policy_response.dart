class GlucoseAlertPolicyResponse {
  final int id;
  final int careGiverId;
  final int highRiskValue;
  final int veryHighRiskValue;

  GlucoseAlertPolicyResponse({
    required this.id,
    required this.careGiverId,
    required this.highRiskValue,
    required this.veryHighRiskValue,
  });

  factory GlucoseAlertPolicyResponse.fromJson(Map<String, dynamic> json) => GlucoseAlertPolicyResponse(
    id: json['id'],
    careGiverId: json['careGiverId'],
    highRiskValue: json['highRiskValue'],
    veryHighRiskValue: json['veryHighRiskValue'],
  );
}
