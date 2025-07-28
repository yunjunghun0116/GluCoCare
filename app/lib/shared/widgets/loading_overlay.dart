import 'package:flutter/material.dart';

class LoadingOverlay extends StatelessWidget {
  const LoadingOverlay({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Stack(
        children: [
          Opacity(opacity: 0.5, child: const ModalBarrier(dismissible: false, color: Colors.black)),
          Center(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const CircularProgressIndicator(),
                const SizedBox(height: 20),
                Text(
                  "지금 처리중입니다...",
                  style: TextStyle(color: Colors.white, fontSize: 16, height: 20 / 16),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
