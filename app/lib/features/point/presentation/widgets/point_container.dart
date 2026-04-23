import 'package:app/features/point/presentation/providers.dart';
import 'package:app/shared/widgets/common_loading_indicator.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../shared/constants/app_colors.dart';

class PointContainer extends ConsumerStatefulWidget {
  const PointContainer({super.key});

  @override
  ConsumerState<PointContainer> createState() => _PointContainerState();
}

class _PointContainerState extends ConsumerState<PointContainer> {
  int _point = 0;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => setPoint());
  }

  void setPoint() async {
    if (_isLoading) return;
    try {
      setState(() => _isLoading = true);
      await Future.delayed(Duration(milliseconds: 200));
      var point = await ref.read(pointControllerProvider.notifier).readPoint();
      if (point == null) return;
      setState(() => _point = point.point);
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
      decoration: BoxDecoration(
        color: AppColors.mainColor.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        children: [
          Image.asset("assets/icon/points.png", width: 32, height: 32),
          SizedBox(width: 10),
          Text(
            "내 포인트",
            style: TextStyle(fontSize: 14, height: 20 / 14, color: AppColors.mainColor, fontWeight: FontWeight.bold),
          ),
          SizedBox(width: 6),
          AnimatedSwitcher(
            duration: const Duration(milliseconds: 200),
            child: Text(
              "${_isLoading ? ".." : _point} P",
              key: ValueKey(_isLoading),
              style: TextStyle(fontSize: 14, height: 20 / 14, fontWeight: FontWeight.bold, color: AppColors.mainColor),
            ),
          ),
          Spacer(),
          GestureDetector(
            onTap: _isLoading ? null : setPoint,
            child: Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(color: Colors.white, shape: BoxShape.circle),
              child: _isLoading
                  ? Padding(padding: const EdgeInsets.all(8), child: CommonLoadingIndicator())
                  : Icon(Icons.refresh_rounded, color: AppColors.mainColor, size: 20),
            ),
          ),
        ],
      ),
    );
  }
}
