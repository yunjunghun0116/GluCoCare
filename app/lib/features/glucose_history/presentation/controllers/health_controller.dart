import 'dart:async';

import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/features/glucose_history/data/models/health_upload_request.dart';
import 'package:app/shared/constants/local_repository_key.dart';

import '../../../../core/base/base_controller.dart';
import '../../../../core/health/health_connector.dart';

class HealthState extends BaseState {
  final DateTime? lastSyncTime;

  const HealthState({super.isLoading, this.lastSyncTime});

  @override
  HealthState copyWith({bool? isLoading, DateTime? lastSyncTime}) {
    return HealthState(isLoading: isLoading ?? this.isLoading, lastSyncTime: lastSyncTime ?? this.lastSyncTime);
  }
}

class HealthController extends BaseController<HealthState> {
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
    var nowDate = DateTime.now();
    var data = await _healthConnector.fetchBloodGlucose();
    if (data.isEmpty) return;

    var requests = data
        .map((point) => HealthUploadRequest.fromPoint(point))
        .map((request) => request.toJson())
        .toList();
    var response = await postRequest("/api/health", data: requests);

    if (response.statusCode == 200) {
      LocalRepository().save(LocalRepositoryKey.lastSyncDateTime, nowDate);
      state = state.copyWith(lastSyncTime: nowDate);
    }
  }

  @override
  void dispose() {
    stopFetch();
    super.dispose();
  }
}
