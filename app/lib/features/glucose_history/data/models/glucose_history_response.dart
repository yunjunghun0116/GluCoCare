class GlucoseHistoryResponse {
  final int id;
  final DateTime dateTime;
  final int sgv;

  GlucoseHistoryResponse({required this.id, required this.dateTime, required this.sgv});

  factory GlucoseHistoryResponse.fromJson(Map<String, dynamic> json) {
    final epochMs = (json['dateTime'] as num).toInt();
    final dt = DateTime.fromMillisecondsSinceEpoch(epochMs, isUtc: true).toLocal();

    return GlucoseHistoryResponse(id: json['id'], dateTime: dt, sgv: json['sgv']);
  }
}
