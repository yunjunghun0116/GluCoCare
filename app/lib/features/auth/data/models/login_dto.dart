class LoginDto {
  final String email;
  final String password;

  LoginDto({required this.email, required this.password});

  @override
  String toString() {
    return 'LoginDto{email: $email, password: $password}';
  }
}
