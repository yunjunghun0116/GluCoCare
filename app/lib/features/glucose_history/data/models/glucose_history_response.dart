class GlucoseHistoryResponse {
  final int id;
  final DateTime dateTime;
  final int sgv;

  GlucoseHistoryResponse({required this.id, required this.dateTime, required this.sgv});

  factory GlucoseHistoryResponse.fromJson(Map<String, dynamic> json) {
    return GlucoseHistoryResponse(id: json['id'], dateTime: DateTime.parse(json['date']), sgv: json['sgv']);
  }
}
