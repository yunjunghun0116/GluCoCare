import 'dart:developer';
import 'dart:io';

import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

class NotificationService {
  static final NotificationService _instance = NotificationService._internal();

  factory NotificationService() => _instance;

  NotificationService._internal();

  final FlutterLocalNotificationsPlugin _flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();

  Future<void> initialize() async {
    var initializationSettingsAndroid = AndroidInitializationSettings('@mipmap/ic_launcher');

    var initializationSettingsDarwin = DarwinInitializationSettings(
      requestSoundPermission: false,
      requestBadgePermission: false,
      requestAlertPermission: false,
    );

    var initializationSettings = InitializationSettings(
      android: initializationSettingsAndroid,
      iOS: initializationSettingsDarwin,
    );

    await _flutterLocalNotificationsPlugin.initialize(initializationSettings);
  }

  Future<void> requestPermission() async {
    if (Platform.isAndroid) {
      var success = await _flutterLocalNotificationsPlugin
          .resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()
          ?.requestNotificationsPermission();
      log("Notification Service Permission Status : $success");
    }
    if (Platform.isIOS) {
      var settings = await FirebaseMessaging.instance.requestPermission(alert: true, badge: true, sound: true);
      // 필요할 경우 이곳에서 setForegroundNotificationPresentationOptions 를 호출할 수도 있음.
      log("Notification Service Permission Status : ${settings.authorizationStatus}");
    }
  }
}
