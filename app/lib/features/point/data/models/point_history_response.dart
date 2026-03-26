import 'package:json_annotation/json_annotation.dart';

import 'point_transaction_type.dart';

part 'point_history_response.g.dart';

@JsonSerializable()
class PointHistoryResponse {
  final PointTransactionType type;
  final int amount;
  final int balanceAfter;
  final String description;

  PointHistoryResponse({
    required this.type,
    required this.amount,
    required this.balanceAfter,
    required this.description,
  });

  factory PointHistoryResponse.fromJson(Map<String, dynamic> json) => _$PointHistoryResponseFromJson(json);

  Map<String, dynamic> toJson() => _$PointHistoryResponseToJson(this);

  @override
  String toString() {
    return 'PointHistoryResponse{type: $type, amount: $amount, balanceAfter: $balanceAfter, description: $description}';
  }
}
