import 'dart:async';
import 'dart:developer';

import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/core/health/health_connector.dart';
import 'package:app/features/glucose_history/data/models/health_upload_request.dart';
import 'package:app/shared/constants/app_values.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:dio/dio.dart';
import 'package:flutter_foreground_task/flutter_foreground_task.dart';

@pragma('vm:entry-point')
void foregroundTaskCallback() {
  FlutterForegroundTask.setTaskHandler(GlucoseSyncHandler());
}

class GlucoseSyncHandler extends TaskHandler {
  Timer? _timer;

  @override
  Future<void> onStart(DateTime timestamp, TaskStarter starter) async {
    log("[ForegroundSync] Service started");
    await LocalRepository().initialize();
    _timer = Timer.periodic(Duration(minutes: 5), (_) => _sync());
  }

  Future<void> _sync() async {
    try {
      await LocalRepository().initialize();
      var accessToken = LocalRepository().read<String?>(LocalRepositoryKey.accessToken);

      if (accessToken == null || accessToken.isEmpty) {
        log("[ForegroundSync] No access token");
        return;
      }

      var dio = Dio(
        BaseOptions(
          baseUrl: AppValues.serverBaseUrl,
          connectTimeout: Duration(seconds: 30),
          receiveTimeout: Duration(seconds: 30),
          headers: {
            'Content-Type': 'application/json',
            'ngrok-skip-browser-warning': 'true',
            'Authorization': 'Bearer $accessToken',
          },
        ),
      );

      var data = await HealthConnector().fetchBloodGlucose();

      if (data.isEmpty) {
        log("[ForegroundSync] No data");
        return;
      }

      var requests = data
          .map((point) => HealthUploadRequest.fromPoint(point))
          .map((request) => request.toJson())
          .toList();

      var response = await dio.post('/api/health', data: requests);

      if (response.statusCode == 200) {
        final nowDate = DateTime.now();
        await LocalRepository().save<int>(LocalRepositoryKey.lastSyncDateTime, nowDate.millisecondsSinceEpoch);
        log("[ForegroundSync] Synced ${requests.length} records");
      }
    } catch (e, stack) {
      log("[ForegroundSync] Error: $e\n$stack");
    }
  }

  @override
  Future<void> onDestroy(DateTime timestamp, bool isTimeout) async {
    _timer?.cancel();
    log("[ForegroundSync] Service destroyed");
  }

  @override
  void onRepeatEvent(DateTime timestamp) {}
}

class ForegroundSyncService {
  static Future<void> initialize() async {
    FlutterForegroundTask.init(
      androidNotificationOptions: AndroidNotificationOptions(
        channelId: 'glucocare_sync',
        channelName: '혈당 모니터링',
        channelDescription: '백그라운드에서 혈당 데이터를 동기화합니다.',
        onlyAlertOnce: true,
      ),
      iosNotificationOptions: IOSNotificationOptions(),
      foregroundTaskOptions: ForegroundTaskOptions(eventAction: ForegroundTaskEventAction.nothing()),
    );
  }

  static Future<void> start() async {
    if (await FlutterForegroundTask.isRunningService) return;

    await FlutterForegroundTask.startService(
      serviceTypes: [ForegroundServiceTypes.dataSync],
      notificationTitle: 'GluCoCare',
      notificationText: '혈당 모니터링 중',
      callback: foregroundTaskCallback,
    );
  }

  static Future<void> stop() async {
    await FlutterForegroundTask.stopService();
  }
}
