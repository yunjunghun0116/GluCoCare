import 'package:app/features/point/presentation/providers.dart';
import 'package:flutter/material.dart';
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
      var point = await ref.read(pointControllerProvider.notifier).readPoint();
      if (point == null) return;
      setState(() => _point = point.point);
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Text(
          "나의 보유 포인트: $_point",
          style: TextStyle(fontSize: 12, height: 20 / 12, color: AppColors.fontGray200Color),
        ),
        SizedBox(width: 6),
        GestureDetector(
          onTap: _isLoading ? null : setPoint, // 로딩중 탭 막기
          child: _isLoading
              ? SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.mainColor),
                )
              : Icon(Icons.refresh, color: AppColors.mainColor, size: 20),
        ),
        SizedBox(width: 20),
      ],
    );
  }
}
