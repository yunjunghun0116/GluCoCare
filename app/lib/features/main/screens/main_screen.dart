import 'package:app/shared/widgets/common_app_bar.dart';
import 'package:flutter/material.dart';

import '../../../shared/constants/app_colors.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  var _currentIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CommonAppBar(title: "GluCoCare", showLeading: false),
      backgroundColor: AppColors.backgroundColor,
      bottomNavigationBar: Container(
        color: AppColors.backgroundColor,
        child: SafeArea(
          child: Container(
            color: AppColors.backgroundColor,
            width: double.infinity,
            height: 60,
            child: Row(
              children: [
                getBottomNavigationBarItem(
                  activeIcon: Icons.home,
                  inactiveIcon: Icons.home_outlined,
                  index: 0,
                  title: '홈',
                  onTap: () => setState(() => _currentIndex = 0),
                ),
                getBottomNavigationBarItem(
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

  Widget getBottomNavigationBarItem({
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
