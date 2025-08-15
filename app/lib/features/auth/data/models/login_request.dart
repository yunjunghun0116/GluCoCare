class LoginRequest {
  final String email;
  final String password;

  LoginRequest({required this.email, required this.password});

  @override
  String toString() {
    return 'LoginRequest{email: $email, password: $password}';
  }
}
