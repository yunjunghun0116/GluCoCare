import 'package:flutter/material.dart';

import '../constants/app_colors.dart';

class CommonAppBar extends StatelessWidget implements PreferredSizeWidget {
  final VoidCallback? onBack;
  final String? title;
  final List<Widget>? actions;
  final bool showLeading;

  const CommonAppBar({super.key, this.onBack, this.title, this.actions, this.showLeading = true});

  @override
  Widget build(BuildContext context) {
    return AppBar(
      foregroundColor: AppColors.fontGray800Color,
      backgroundColor: AppColors.backgroundColor,
      surfaceTintColor: AppColors.backgroundColor,
      leadingWidth: 48,
      automaticallyImplyLeading: false,
      leading: showLeading
          ? GestureDetector(
              behavior: HitTestBehavior.opaque,
              onTap:
                  onBack ??
                  () {
                    if (Navigator.canPop(context)) {
                      Navigator.pop(context);
                    }
                  },
              child: Container(
                margin: const EdgeInsets.only(left: 20),
                alignment: Alignment.center,
                child: Icon(Icons.arrow_back_ios),
              ),
            )
          : null,
      title: Text(
        title ?? '',
        style: TextStyle(fontSize: 16, height: 20 / 16, color: AppColors.fontGray800Color, fontWeight: FontWeight.bold),
      ),
      elevation: 0,
      actions: actions,
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}
