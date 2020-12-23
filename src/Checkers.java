import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Checkers {
	/*************************************
	 * b0 b1 b2 - X position, 0..7 * b3 b4 b5 - Y position, 0..7 * b6 - color, 0
	 * black, 1 white * b7 - piece, 0 pawn, 1 dame * b8 - state, 0 captured, 1 in
	 * game *
	 *************************************/

	static long white1 = 0b101001011101001001101000110101000100101000010101000000L;
	static long white2 = 0b101010110101010100101010010101010000101001111101001101L;
	static long black1 = 0b100110010100110000100101111100101101100101011100101001L;
	static long black2 = 0b100111111100111101100111011100111001100110110100110100L;
	static Long[] state = { white1, white2, black1, black2 };

	static long ifMask = 0b100000000L;
	static long posXMask = 0b111L;
	static long posYMask = posXMask << 3;

	public static final String sep = " ";

	static char sqW = '\u2591';
	static char sqK = '\u2588';
	static char pwnW = 'O';
	static char pwnK = 'X';
	static char dameW = '\u019F';// '\u019F';
	static char dameK = '\u0416'; // 0416 04fe

// n będzie numerować pionki. 0..11 to dwanaście pionków białych,
//12..23 to dwanaście pionków czarnych

	static boolean isInGame(byte n) {
		long flag = -1L;
		long aMask = ifMask << (n % 6) * 9; // tworzę lokalną maskę, specyficzną dla piona n
		flag = state[n / 6] & aMask; // sprawdza, czy jest 1 na miejscu b8 piona n w longu stanu gry
		flag = flag >> (n % 6) * 9 + 8; // przesuwa bit na ostatnie miejsce po prawej
		return (flag == 1);
	}

	static boolean isDame(byte n) {
		long flag = -1;
		long aMask = ifMask >> 1;
		aMask = aMask << (n % 6) * 9;
		flag = state[n / 6] & aMask; // sprawdza, czy jest 1 na miejscu b8 piona n w longu stanu gry
		flag = flag >> (n % 6) * 9 + 7; // przesuwa bit na ostatnie miejsce po prawej
		return (flag == 1);
	}

	static void drawEmptyBoard() {
		System.out.print(sep + " ");
		for (byte x = 0; x < 8; x++)
			System.out.print(x + sep);
		System.out.println();
		for (byte y = 0; y < 8; y++) {
			System.out.print(y + sep);
			for (byte x = 0; x < 8; x++) {
				if ((x + y) % 2 == 0) {
					System.out.print(sqW + sep);
				} else {
					System.out.print(sqK + sep);
				}
			}
			System.out.println(y);
		}
		System.out.print(sep + " ");
		for (byte x = 0; x < 8; x++)
			System.out.print(x + sep);
		System.out.println();
	}

	static void drawBoard() {
		System.out.print(sep + " ");
		for (byte x = 0; x < 8; x++)
			System.out.print(x + sep);
		System.out.println();

		byte n = 0;
		for (byte y = 0; y < 8; y++) {
			System.out.print(y + sep);
			for (byte x = 0; x < 8; x++) {
				if ((x + y) % 2 == 0) {
					for (n = 0; n <= 23; n++) {
						if ((positionX(n) == x) && positionY(n) == y) {
							if (n / 12 == 0) {
								if (isDame(n)) {
									System.out.print(dameW + sep);
								} else {
									System.out.print(pwnW + sep);
								}
							} else {
								if (isDame(n)) {
									System.out.print(dameK + sep);
								} else {
									System.out.print(pwnK + sep);
								}
							}
							n += 50; // break for n
						}
					}
					if (n == 24)
						System.out.print(sqW + sep); // przeszedł po wszystkich n i nie znalazł pionka
				} else { // współrzędne w sumie nieparzyste, więc pole jest czarne
					System.out.print(sqK + sep);
				}
			}
			System.out.println(y);
		}

//Ostatni rząd z numerami kolumn
		System.out.print(sep + " ");
		for (byte x = 0; x < 8; x++)
			System.out.print(x + sep);
		System.out.println();
	}

	static long positionX(byte n) {
		long aMask = posXMask << (n % 6) * 9;
		long result = state[n / 6] & aMask;
		result = result >> (n % 6) * 9;
		return result;
	}

	static long positionY(byte n) {
		long aMask = posYMask << (n % 6) * 9;
		long result = state[n / 6] & aMask;
		result = result >> (n % 6) * 9 + 3;
		return result;
	}

	static long updatePositionX(long pwn, byte n, int c) {
		long aMask = posXMask << (n % 6) * 9;
		long result = state[n / 6] & aMask;
		result = result >> (n % 6) * 9;
		return result;
	}

	static long updatePpositionY(long pwn, byte n, int d) {
		long aMask = posYMask << (n % 6) * 9;
		long result = state[n / 6] & aMask;
		result = result >> (n % 6) * 9 + 3;
		return result;
	}

	public static void main(String[] args) throws InterruptedException {
		drawEmptyBoard();
		String nameW = "";
		String nameK = "";
		boolean game = true;
		boolean moves = true;

		BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Wpisz kto gra białymi:");
		try {
			nameW = sc.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Wpisz kto gra czarnymi:");
		try {
			nameK = sc.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		nameW = (nameW.charAt(0) + "").toUpperCase() + nameW.substring(1).toLowerCase();
		nameK = (nameK.charAt(0) + "").toUpperCase() + nameK.substring(1).toLowerCase();
		System.out.println(nameW + " gra " + pwnW + " i rozpoczyna rozgrywkę");
		System.out.println(nameK + " gra " + pwnK + "\nPowodzenia!");
		drawBoard();
		System.out.println(
				"Pozycję podawaj parami współrzędnych - z jakiej pozycji chcesz się ruszyć na jaką np. \"02 13\"");
		while (game) {
			if (moves) {
				System.out.print(nameW + " ruch: ");
				String move = "";
				try {
					move = sc.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (move.equalsIgnoreCase("koniec")) {
					System.out.println("Koniec gry, wygrały czarne");
					System.exit(0);
				}
				int a = Integer.parseInt("" + move.charAt(0));
				int b = Integer.parseInt("" + move.charAt(1));
				int c = Integer.parseInt("" + move.charAt(3));
				int d = Integer.parseInt("" + move.charAt(4));
				if (possible(a, b, c, d, moves)) {
					System.out.println("Ruch z: " + a + " " + b + " na: " + c + " " + d);
					moves = false;
				}
			} else {
				System.out.print(nameK + " ruch: ");
				String move = "";
				try {
					move = sc.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (move.equalsIgnoreCase("koniec")) {
					System.out.println("Koniec gry, wygrały białe");
					System.exit(0);
				}
				int a = Integer.parseInt("" + move.charAt(0));
				int b = Integer.parseInt("" + move.charAt(1));
				int c = Integer.parseInt("" + move.charAt(3));
				int d = Integer.parseInt("" + move.charAt(4));
				if (possible(a, b, c, d, moves)) {
					System.out.println("Ruch z: " + a + " " + b + " na: " + c + " " + d);
					moves = true;
				}
			}
		}
	}// end Main

	private static boolean possible(int a, int b, int c, int d, boolean moves) {
		boolean r = false;
		if (moves) {
			for (byte n = 0; n <= 11; n++) {
				if ((positionX(n) == a) && positionY(n) == b) {
					if (isDame(n)) {
						r = true;
					} else {
						System.out.println(n);
						if (d - b == 1 && (a - c == 1 || c - a == 1)) {
							if (n < 6) {
								updatePositionX(white1, n, c);
								updatePositionY(white1, n, d);
							} else {
								updatePositionX(white2, n, c);
								updatePositionY(white2, n, d);
								white2 = 0b101010110101010100101010010101011001101001111101001101L;
							}

							state = new Long[] { white1, white2, black1, black2 };
							drawBoard();
							r = true;
						}
					}
				}
			}
		} else {
			for (byte n = 12; n <= 23; n++) {
				if ((positionX(n) == a) && positionY(n) == b) {
					if (isDame(n)) {
						r = true;
					} else {
						System.out.println(n);
						if (b - d == 1 && (a - c == 1 || c - a == 1)) {
							if (n > 6) {
								updatePositionX(black1, n, c);
								updatePositionY(black1, n, d);
							} else {
								updatePositionX(black2, n, c);
								updatePositionY(black2, n, d);
							}
							state = new Long[] { white1, white2, black1, black2 };

							drawBoard();
							r = true;
						}
					}
				}
			}
		}
		return r;
	}

	private static void updatePositionY(long white12, byte n, int d) {
		// TODO Auto-generated method stub

	}
}// end Checkers
