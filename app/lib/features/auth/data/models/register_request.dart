class RegisterRequest {
  final String email;
  final String password;
  final String name;

  RegisterRequest({required this.email, required this.password, required this.name});

  @override
  String toString() {
    return 'RegisterRequest{email: $email, password: $password, name: $name}';
  }
}
