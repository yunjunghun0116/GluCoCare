import 'package:app/shared/widgets/common_button.dart';
import 'package:flutter/material.dart';

import '../../../shared/constants/app_colors.dart';

class ConsentDialog extends StatelessWidget {
  final VoidCallback onAgree;
  final VoidCallback onDeny;

  const ConsentDialog({super.key, required this.onAgree, required this.onDeny});

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      clipBehavior: Clip.hardEdge,
      backgroundColor: AppColors.backgroundColor,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: double.infinity,
            color: AppColors.backgroundColor,
            padding: const EdgeInsets.symmetric(vertical: 20),
            child: Column(
              children: [
                SizedBox(width: 52, height: 52, child: Image.asset("assets/logo/app_logo.png", fit: BoxFit.contain)),
                const SizedBox(height: 10),
                const Text(
                  '혈당 예측 서비스 안내',
                  style: TextStyle(fontSize: 20, height: 1, fontWeight: FontWeight.bold, color: AppColors.mainColor),
                ),
                const SizedBox(height: 6),
                Text(
                  '정확한 예측을 위해 아래 데이터가 수집되어\nGluCoCare 서버로 전송됩니다\n또한, GluCoCare의 모든 데이터는\n외부로 전송되지 않습니다',
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 12, color: AppColors.mainColor, height: 20 / 12),
                ),
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '수집되는 데이터',
                  style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: AppColors.fontGray400Color),
                ),
                const SizedBox(height: 10),
                _item(Icons.water_drop_outlined, '연속혈당(CGM) 수치', '혈당 측정값 및 측정 시간'),
                const SizedBox(height: 10),
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: AppColors.informationBackgroundColor,
                    borderRadius: BorderRadius.circular(10),
                    border: Border(left: BorderSide(color: AppColors.informationBorderColor, width: 3)),
                  ),
                  child: Text(
                    '수집된 데이터는 오직 혈당 예측 목적으로만 사용되며, 언제든지 설정에서 동의를 철회할 수 있습니다.',
                    style: TextStyle(fontSize: 11, color: const Color(0xFF7A5C1E), height: 1.5),
                  ),
                ),
              ],
            ),
          ),

          Padding(
            padding: const EdgeInsets.symmetric(vertical: 20),
            child: Column(
              children: [
                CommonButton(value: true, onTap: onAgree, title: "동의하고 시작하기"),
                const SizedBox(height: 8),
                SizedBox(
                  width: double.infinity,
                  child: TextButton(
                    onPressed: onDeny,
                    child: Text('나중에 결정하기', style: TextStyle(fontSize: 14, color: AppColors.fontGray400Color)),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _item(IconData icon, String title, String desc) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
      decoration: BoxDecoration(color: const Color(0xFFF9F9F9), borderRadius: BorderRadius.circular(12)),
      child: Row(
        children: [
          Container(
            width: 32,
            height: 32,
            decoration: BoxDecoration(color: const Color(0xFFE1F5EE), borderRadius: BorderRadius.circular(8)),
            child: Icon(icon, color: AppColors.mainColor, size: 16),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(title, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600)),
                const SizedBox(height: 2),
                Text(desc, style: TextStyle(fontSize: 11, color: AppColors.fontGray400Color, height: 1.4)),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
