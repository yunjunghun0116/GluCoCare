import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:health/health.dart';

class HealthConnector {
  static final HealthConnector _instance = HealthConnector._internal();

  factory HealthConnector() => _instance;

  HealthConnector._internal() : health = Health();

  final Health health;

  static final List<HealthDataType> types = [HealthDataType.BLOOD_GLUCOSE];

  Future<bool> initialize() async {
    try {
      var isAvailable = await _isHealthConnectAvailable();
      if (!isAvailable) return false;

      bool authorized = await health.requestAuthorization(
        types,
        permissions: types.map((e) => HealthDataAccess.READ).toList(),
      );

      return authorized;
    } catch (e) {
      throw CustomException(ExceptionMessage.healthConnectPermission);
    }
  }

  Future<bool> _isHealthConnectAvailable() async {
    var isAvailable = await health.isHealthConnectAvailable();
    if (!isAvailable) {
      await health.installHealthConnect();
      isAvailable = await health.isHealthConnectAvailable();
    }
    return isAvailable;
  }

  Future<List<HealthDataPoint>> fetchBloodGlucose() async {
    var nowDate = DateTime.now();
    late DateTime startDate;
    try {
      var lastSyncDateTime = LocalRepository().read<String>(LocalRepositoryKey.lastSyncDateTime);
      startDate = DateTime.parse(lastSyncDateTime);
    } catch (e) {
      startDate = nowDate.subtract(Duration(days: 90));
    }

    var glucoseData = await health.getHealthDataFromTypes(
      types: [HealthDataType.BLOOD_GLUCOSE],
      startTime: startDate,
      endTime: nowDate,
    );

    return glucoseData;
  }
}
