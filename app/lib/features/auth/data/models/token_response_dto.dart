import 'package:json_annotation/json_annotation.dart';

part 'token_response_dto.g.dart';

@JsonSerializable()
class TokenResponseDto {
  final String accessToken;
  final String refreshToken;

  TokenResponseDto({required this.accessToken, required this.refreshToken});

  factory TokenResponseDto.fromJson(Map<String, dynamic> json) => _$TokenResponseDtoFromJson(json);

  Map<String, dynamic> toJson() => _$TokenResponseDtoToJson(this);

  @override
  String toString() {
    return 'TokenResponseDto{accessToken: $accessToken, refreshToken: $refreshToken}';
  }
}
