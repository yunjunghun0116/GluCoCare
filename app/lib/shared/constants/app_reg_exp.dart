abstract final class AppRegExp {
  static final RegExp nameRegExp = RegExp(r'[ㄱ-ㅎ가-힣a-zA-Z0-9]+$');
  static final RegExp ageRegExp = RegExp(r'^[0-9]+$');
  static final RegExp passwordRegExp = RegExp(r'[0-9a-zA-Z]{8}$');
  static final RegExp emailRegExp = RegExp(r'[0-9a-z]([\-.\w]*[0-9a-z\-_+])*@([0-9a-z][\-\w]*[0-9a-z]\.)+[a-z]{2,9}');
}
