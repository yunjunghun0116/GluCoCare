import 'dart:developer';
import 'dart:io';

import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/core/health/health_connector.dart';
import 'package:app/features/glucose_history/data/models/health_upload_request.dart';
import 'package:app/shared/constants/app_values.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:dio/dio.dart';
import 'package:firebase_messaging/firebase_messaging.dart';

@pragma('vm:entry-point')
Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  if (Platform.isAndroid) return; // Android는 ForegroundSyncService가 처리
  if (message.data['type'] == 'sync') {
    await _sync();
  }
}

Future<void> _sync() async {
  try {
    await LocalRepository().initialize();

    final accessToken = LocalRepository().read<String?>(LocalRepositoryKey.accessToken);
    if (accessToken == null || accessToken.isEmpty) {
      log("[BackgroundSync] No access token");
      return;
    }

    final dio = Dio(
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

    final data = await HealthConnector().fetchBloodGlucose();
    if (data.isEmpty) {
      log("[BackgroundSync] No data");
      return;
    }

    final requests = data
        .map((point) => HealthUploadRequest.fromPoint(point))
        .map((request) => request.toJson())
        .toList();

    final response = await dio.post('/api/health', data: requests);

    if (response.statusCode == 200) {
      final nowDate = DateTime.now();
      await LocalRepository().save<int>(LocalRepositoryKey.lastSyncDateTime, nowDate.millisecondsSinceEpoch);
      log("[BackgroundSync] Synced ${requests.length} records");
    }
  } catch (e, stack) {
    log("[BackgroundSync] Error: $e\n$stack");
  }
}

class BackgroundFetchService {
  static void initialize() {
    FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);
  }
}
