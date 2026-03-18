import 'package:app/core/health/health_connector.dart';
import 'package:app/features/glucose_history/presentation/providers.dart';
import 'package:app/features/main/screens/setting_screen.dart';
import 'package:app/features/member/presentation/providers.dart';
import 'package:app/features/patient/presentation/providers.dart';
import 'package:app/shared/widgets/common_app_bar.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/notification/notification_service.dart';
import '../../../shared/constants/app_colors.dart';
import 'home_screen.dart';

class MainScreen extends ConsumerStatefulWidget {
  const MainScreen({super.key});

  @override
  ConsumerState<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends ConsumerState<MainScreen> {
  int _currentIndex = 0;

  @override
  void initState() {
    super.initState();
    initialize();
  }

  void initialize() async {
    await notificationServiceInitialize();
    await healthConnectorInitialize();
  }

  Future<void> healthConnectorInitialize() async {
    var isPatient = await ref.read(patientControllerProvider.notifier).readIsPatient();
    if (!isPatient) return;
    // 1. 최초 실행 시 예측 기능을 위한 데이터 전송 동의 dialog
    // 2. 만약 그걸 안할경우 아무 기능도 동작 안하도록 하며 이게 true가 되기 전까지는 false일때도 계속 들어올 때마다 묻기
    await HealthConnector().initialize();
    ref.read(healthControllerProvider.notifier).startFetch();
  }

  Future<void> notificationServiceInitialize() async {
    await NotificationService().requestPermission();
    var token = await FirebaseMessaging.instance.getToken();
    if (token == null) return;
    ref.read(fcmTokenControllerProvider.notifier).updateFCMToken(token);

    FirebaseMessaging.instance.onTokenRefresh.listen((newToken) {
      ref.read(fcmTokenControllerProvider.notifier).updateFCMToken(newToken);
    });
  }

  Widget _getScreen() {
    Widget screen;
    switch (_currentIndex) {
      case 0:
        screen = HomeScreen();
        break;
      case 1:
      default:
        screen = SettingScreen();
        break;
    }
    return SafeArea(child: screen);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(
        title: "GluCoCare",
        centerTitle: true,
        showLeading: false,
        actions: [
          GestureDetector(
            onTap: () {},
            child: Icon(Icons.info_outline_rounded, size: 24, color: AppColors.mainColor),
          ),
          SizedBox(width: 20),
        ],
      ),
      backgroundColor: AppColors.backgroundColor,
      body: _getScreen(),
      bottomNavigationBar: Container(
        color: AppColors.backgroundColor,
        child: SafeArea(
          child: Container(
            color: AppColors.backgroundColor,
            width: double.infinity,
            height: 60,
            child: Row(
              children: [
                _getBottomNavigationBarItem(
                  activeIcon: Icons.home,
                  inactiveIcon: Icons.home_outlined,
                  index: 0,
                  title: '홈',
                  onTap: () => setState(() => _currentIndex = 0),
                ),
                _getBottomNavigationBarItem(
                  activeIcon: Icons.settings,
                  inactiveIcon: Icons.settings_outlined,
                  index: 1,
                  title: '설정',
                  onTap: () => setState(() => _currentIndex = 1),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _getBottomNavigationBarItem({
    required IconData activeIcon,
    required IconData inactiveIcon,
    required int index,
    required String title,
    required Function() onTap,
  }) {
    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        behavior: HitTestBehavior.opaque,
        child: Container(
          color: AppColors.backgroundColor,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Icon(_currentIndex == index ? activeIcon : inactiveIcon),
              SizedBox(height: 10),
              Text(
                title,
                style: TextStyle(fontSize: 12, height: 20 / 12, color: AppColors.fontGray600Color),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
