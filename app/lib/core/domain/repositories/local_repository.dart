abstract class LocalRepository {
  Future<void> initialize();

  Future<void> save<T>(String key, dynamic value);

  T read<T>(String key);

  void delete(String key);
}
