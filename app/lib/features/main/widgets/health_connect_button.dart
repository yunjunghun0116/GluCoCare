import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../shared/constants/app_colors.dart';
import '../../glucose_history/presentation/providers.dart';

class ConnectButton extends ConsumerStatefulWidget {
  const ConnectButton({super.key, required this.isConnected, required this.title, required this.image});

  final bool isConnected;
  final String title;
  final String image;

  @override
  ConsumerState<ConnectButton> createState() => _ConnectButtonState();
}

class _ConnectButtonState extends ConsumerState<ConnectButton> {
  bool _isPressed = false;

  void _handleTapDown(TapDownDetails details) {
    if (!mounted) return;
    setState(() => _isPressed = true);
  }

  void _handleTapUp(TapUpDetails details) {
    if (!mounted) return;
    setState(() => _isPressed = false);
  }

  void _handleTapCancel() {
    if (!mounted) return;
    setState(() => _isPressed = false);
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      child: Ink(
        decoration: BoxDecoration(color: _isPressed ? AppColors.fontGray50Color : AppColors.backgroundColor),
        child: InkWell(
          onTapDown: _handleTapDown,
          onTapUp: _handleTapUp,
          onTapCancel: _handleTapCancel,
          onTap: () async {
            if (widget.isConnected) return;
            await ref.read(healthControllerProvider.notifier).requestPermission();
          },
          splashColor: AppColors.backgroundColor,
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
            child: Row(
              children: [
                SizedBox(width: 50, height: 50, child: Image.asset(widget.image)),
                const SizedBox(width: 10),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        widget.title,
                        style: const TextStyle(fontSize: 14, height: 20 / 14, fontWeight: FontWeight.bold),
                      ),
                      Text(
                        widget.isConnected ? "혈당 데이터 연동 중" : "연동되지 않음 - 탭하여 연결",
                        style: TextStyle(fontSize: 12, height: 20 / 12, color: AppColors.fontGray400Color),
                      ),
                    ],
                  ),
                ),
                Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      width: 8,
                      height: 8,
                      decoration: BoxDecoration(
                        color: widget.isConnected ? AppColors.okColor : AppColors.noColor,
                        shape: BoxShape.circle,
                      ),
                    ),
                    const SizedBox(width: 6),
                    Text(
                      widget.isConnected ? '연동됨' : '연동 안됨',
                      style: TextStyle(
                        fontSize: 14,
                        height: 20 / 14,
                        color: widget.isConnected ? AppColors.okColor : AppColors.noColor,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
