
public class NumberBaseBall {
	@SuppressWarnings("unused")
	private int answer;

	// constructor
	public NumberBaseBall() {
		//
	}

	public int makeNewNum() {
		int randomNum[] = new int[4];

		for (int i = 0; i < 4; i++) {
			randomNum[i] = (int) (Math.random() * 9 + 1);
			for (int j = 0; j < i; j++) {
				if (randomNum[i] == randomNum[j]) {
					// 같은 수가 존재한다면 다시 랜덤 수 생성
					i--;
				}
			}
		}

		int num = randomNum[0] * 1000 + randomNum[1] * 100 + randomNum[2] * 10 + randomNum[3];

		// System.out.println("NUM: " + num);

		return num;
	}
}