// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'point_history_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PointHistoryResponse _$PointHistoryResponseFromJson(Map<String, dynamic> json) => PointHistoryResponse(
  type: $enumDecode(_$PointTransactionTypeEnumMap, json['type']),
  amount: (json['amount'] as num).toInt(),
  balanceAfter: (json['balanceAfter'] as num).toInt(),
  description: json['description'] as String,
);

Map<String, dynamic> _$PointHistoryResponseToJson(PointHistoryResponse instance) => <String, dynamic>{
  'type': _$PointTransactionTypeEnumMap[instance.type]!,
  'amount': instance.amount,
  'balanceAfter': instance.balanceAfter,
  'description': instance.description,
};

const _$PointTransactionTypeEnumMap = {
  PointTransactionType.earn: 'EARN',
  PointTransactionType.spend: 'SPEND',
  PointTransactionType.adminAdjust: 'ADMIN_ADJUST',
};
