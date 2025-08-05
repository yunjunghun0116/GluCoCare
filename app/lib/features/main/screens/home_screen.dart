import 'package:app/core/providers.dart';
import 'package:app/features/member/presentation/providers.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/data/repositories/local_repository.dart';

class HomeScreen extends ConsumerStatefulWidget {
  const HomeScreen({super.key});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen> {
  var _name = "";

  @override
  void initState() {
    super.initState();
    _nameInitialize();
  }

  Future<void> _nameInitialize() async {
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      var accessToken = LocalRepository().read<String>(LocalRepositoryKey.accessToken);
      var result = await ref.read(memberControllerProvider.notifier).getName(accessToken);
      if (result == null) return;
      setState(() => _name = result);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(children: [Text(_name)]);
  }
}
