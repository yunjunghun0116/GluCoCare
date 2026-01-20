import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:health/health.dart';

class HealthConnector {
  static final HealthConnector _instance = HealthConnector._internal();

  factory HealthConnector() => _instance;

  HealthConnector._internal() : health = Health();

  final Health health;

  static final List<HealthDataType> types = [HealthDataType.BLOOD_GLUCOSE];

  Future<bool> initialize() async {
    try {
      var isAvailable = await isHealthConnectAvailable();
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

  Future<bool> isHealthConnectAvailable() async {
    var isAvailable = await health.isHealthConnectAvailable();
    if (!isAvailable) {
      await health.installHealthConnect();
      isAvailable = await health.isHealthConnectAvailable();
    }
    return isAvailable;
  }

  Future<void> readBloodGlucose() async {
    var endDate = DateTime.now();
    var startDate = endDate.subtract(Duration(days: 14));
    List<HealthDataPoint> glucoseData = await health.getHealthDataFromTypes(
      types: [HealthDataType.BLOOD_GLUCOSE],
      startTime: startDate,
      endTime: endDate,
    );

    for (var point in glucoseData) {
      print('혈당: ${point.value} ${point.unit}'); // 예: 120 mg/dL
      var pointData = point.toJson();
      print("json : ${pointData['value']['numericValue']}");
      var date = point.dateFrom;
      print('측정 시간: ${date.toUtc().toIso8601String()}'); // 이 값으로 데이터 저장하면 될 듯
      //1759386657665
    }
  }
}
