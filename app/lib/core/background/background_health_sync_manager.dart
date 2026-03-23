import 'dart:io';

import 'package:app/core/background/background_fetch_service.dart';
import 'package:app/core/background/foreground_sync_service.dart';

class BackgroundHealthSyncManager {
  static Future<void> initialize() async {
    if (Platform.isAndroid) {
      await ForegroundSyncService.initialize();
    }
    if (Platform.isIOS) {
      BackgroundFetchService.initialize();
    }
  }

  static Future<void> start() async {
    if (Platform.isAndroid) {
      await ForegroundSyncService.start();
    }
  }

  static Future<void> stop() async {
    if (Platform.isAndroid) {
      await ForegroundSyncService.stop();
    }
  }
}
