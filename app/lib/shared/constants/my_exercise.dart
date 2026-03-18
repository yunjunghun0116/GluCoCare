enum MyExercise {
  walking(image: 'assets/exercise/walking.png', name: '산책', met: 1.5),
  fastWalking(image: 'assets/exercise/fast_walking.png', name: '빠른 걷기', met: 3.0),
  running(image: 'assets/exercise/running.png', name: '런닝', met: 3.5),
  cycle(image: 'assets/exercise/cycle.png', name: '사이클', met: 3.8),
  weight(image: 'assets/exercise/weight.png', name: '웨이트', met: 3.3),
  badminton(image: 'assets/exercise/badminton.png', name: '배드민턴', met: 4.0),
  swimming(image: 'assets/exercise/swimming.png', name: '수영', met: 4.3);

  const MyExercise({required this.image, required this.name, required this.met});

  final String image;
  final String name;
  final double met;
}
