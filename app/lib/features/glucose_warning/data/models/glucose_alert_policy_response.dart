class GlucoseAlertPolicyResponse {
  final int id;
  final int careRelationId;
  final int highRiskValue;
  final int veryHighRiskValue;
  final int lowRiskValue;

  GlucoseAlertPolicyResponse({
    required this.id,
    required this.careRelationId,
    required this.highRiskValue,
    required this.veryHighRiskValue,
    required this.lowRiskValue,
  });

  factory GlucoseAlertPolicyResponse.fromJson(Map<String, dynamic> json) => GlucoseAlertPolicyResponse(
    id: json['id'],
    careRelationId: json['careRelationId'],
    highRiskValue: json['highRiskValue'],
    veryHighRiskValue: json['veryHighRiskValue'],
    lowRiskValue: json['lowRiskValue'],
  );
}
