import 'package:app/core/background/background_health_sync_manager.dart';
import 'package:app/core/health/health_connector.dart';
import 'package:app/features/glucose_history/presentation/providers.dart';
import 'package:app/features/main/screens/service/service_screen.dart';
import 'package:app/features/main/screens/setting_screen.dart';
import 'package:app/features/member/presentation/providers.dart';
import 'package:app/features/mission/presentation/screens/mission_screen.dart';
import 'package:app/features/patient/presentation/providers.dart';
import 'package:app/shared/widgets/common_app_bar.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/data/repositories/local_repository.dart';
import '../../../core/notification/notification_service.dart';
import '../../../shared/constants/app_colors.dart';
import '../../../shared/constants/local_repository_key.dart';
import '../widgets/consent_dialog.dart';
import 'home_screen.dart';

class MainScreen extends ConsumerStatefulWidget {
  const MainScreen({super.key});

  @override
  ConsumerState<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends ConsumerState<MainScreen> with WidgetsBindingObserver {
  int _currentIndex = 0;

  final _screens = [HomeScreen(), MissionScreen(), SettingScreen()];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    WidgetsBinding.instance.addPostFrameCallback((_) => initialize());
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);

    switch (state) {
      case AppLifecycleState.resumed:
        // 포그라운드 복귀 시 즉시 동기화
        ref.read(healthControllerProvider.notifier).startFetch();
        BackgroundHealthSyncManager.stop();
        break;
      case AppLifecycleState.paused:
        // 백그라운드 진입 시 Timer 중지
        ref.read(healthControllerProvider.notifier).stopFetch();
        backgroundServiceStart();
        break;
      case AppLifecycleState.inactive:
      case AppLifecycleState.detached:
        ref.read(healthControllerProvider.notifier).stopFetch();
        break;
      case AppLifecycleState.hidden:
        break;
    }
  }

  void backgroundServiceStart() async {
    if (await ref.read(healthControllerProvider.notifier).isAuthorized()) {
      BackgroundHealthSyncManager.start();
    }
  }

  void initialize() async {
    await notificationServiceInitialize();
    await healthConnectorInitialize();
  }

  Future<void> healthConnectorInitialize() async {
    var isPatient = await ref.read(patientControllerProvider.notifier).readIsPatient();
    if (!isPatient) return;
    if (!mounted) return;
    if (!LocalRepository().containsKey(LocalRepositoryKey.consentAgreed) ||
        !LocalRepository().read<bool>(LocalRepositoryKey.consentAgreed)) {
      var consentAgreed = await showDialog<bool?>(
        context: context,
        barrierDismissible: false,
        builder: (_) => ConsentDialog(
          onAgree: () {
            LocalRepository().save(LocalRepositoryKey.consentAgreed, true);
            Navigator.pop(context, true);
          },
          onDeny: () => Navigator.pop(context),
        ),
      );
      if (consentAgreed == null || !consentAgreed) {
        return;
      }
    }

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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(
        title: "GluCoCare",
        centerTitle: true,
        showLeading: false,
        actions: [
          IconButton(
            onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => ServiceScreen())),
            icon: Icon(Icons.info_outline_rounded, size: 24, color: AppColors.mainColor),
          ),
        ],
      ),
      backgroundColor: AppColors.backgroundColor,
      body: SafeArea(child: _screens[_currentIndex]),
      bottomNavigationBar: Container(
        color: AppColors.backgroundColor,
        padding: const EdgeInsets.only(top: 10),
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
                ),
                _getBottomNavigationBarItem(
                  activeIcon: Icons.flag,
                  inactiveIcon: Icons.flag_outlined,
                  index: 1,
                  title: '미션',
                ),
                _getBottomNavigationBarItem(
                  activeIcon: Icons.settings,
                  inactiveIcon: Icons.settings_outlined,
                  index: 2,
                  title: '설정',
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
  }) {
    var isSelected = _currentIndex == index;
    return Expanded(
      child: GestureDetector(
        onTap: () {
          HapticFeedback.lightImpact();
          setState(() => _currentIndex = index);
        },
        behavior: HitTestBehavior.opaque,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Icon(isSelected ? activeIcon : inactiveIcon, size: 24),
            SizedBox(height: 6),
            Text(
              title,
              style: TextStyle(
                fontSize: 14,
                height: 20 / 14,
                color: AppColors.fontGray600Color,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
