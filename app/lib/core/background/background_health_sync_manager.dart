import 'dart:io';

import 'package:app/core/background/foreground_sync_service.dart';

class BackgroundHealthSyncManager {
  static Future<void> initialize() async {
    if (Platform.isAndroid) {
      await ForegroundSyncService.initialize();
    }
    if (Platform.isIOS) {}
  }

  static Future<void> start() async {
    if (Platform.isAndroid) {
      await ForegroundSyncService.start();
    }
    if (Platform.isIOS) {}
  }

  static Future<void> stop() async {
    if (Platform.isAndroid) {
      await ForegroundSyncService.stop();
    }
    if (Platform.isIOS) {}
  }
}
