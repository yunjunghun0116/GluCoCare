enum MyExercise {
  walking(image: 'assets/exercise/walking_icon.png', name: '산책', met: 2.0),
  fastWalking(image: 'assets/exercise/fast_walking_icon.png', name: '빠른 걷기', met: 3.0),
  running(image: 'assets/exercise/running_icon.png', name: '런닝', met: 6.0),
  cycle(image: 'assets/exercise/cycle_icon.png', name: '사이클', met: 4.0),
  weight(image: 'assets/exercise/weight_icon.png', name: '웨이트', met: 5.0),
  badminton(image: 'assets/exercise/badminton_icon.png', name: '배드민턴', met: 5.5),
  swimming(image: 'assets/exercise/swimming_icon.png', name: '수영', met: 6.0);
  // walking(image: 'assets/exercise/walking.png', name: '산책', met: 2.0),
  // fastWalking(image: 'assets/exercise/fast_walking.png', name: '빠른 걷기', met: 3.0),
  // running(image: 'assets/exercise/running.png', name: '런닝', met: 6.0),
  // cycle(image: 'assets/exercise/cycle.png', name: '사이클', met: 4.0),
  // weight(image: 'assets/exercise/weight.png', name: '웨이트', met: 5.0),
  // badminton(image: 'assets/exercise/badminton.png', name: '배드민턴', met: 5.5),
  // swimming(image: 'assets/exercise/swimming.png', name: '수영', met: 6.0);

  const MyExercise({required this.image, required this.name, required this.met});

  final String image;
  final String name;
  final double met;
}
