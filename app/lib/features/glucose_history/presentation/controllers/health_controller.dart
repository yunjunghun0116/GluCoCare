import 'dart:async';

import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/features/glucose_history/data/models/health_upload_request.dart';
import 'package:app/shared/constants/local_repository_key.dart';

import '../../../../core/base/base_controller.dart';
import '../../../../core/health/health_connector.dart';

class HealthController extends BaseController<BaseState> {
  final HealthConnector _healthConnector;
  Timer? _timer;

  HealthController(super.state, super._dio, {required HealthConnector healthConnector})
    : _healthConnector = healthConnector;

  Future<void> startFetch() async {
    await _fetch();
    _timer?.cancel();
    _timer = Timer.periodic(const Duration(minutes: 3), (_) => _fetch());
  }

  void stopFetch() {
    _timer?.cancel();
    _timer = null;
  }

  Future<void> _fetch() async {
    var data = await _healthConnector.fetchBloodGlucose();
    var requests = data.map((point) => HealthUploadRequest.fromPoint(point)).toList();
    var response = await postRequest("/api/health", data: {"glucoseRequestList": requests});

    if (response.statusCode == 200) {
      LocalRepository().save(LocalRepositoryKey.lastSyncDateTime, DateTime.now().toIso8601String());
    }
  }

  @override
  void dispose() {
    stopFetch();
    super.dispose();
  }
}
