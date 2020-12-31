import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.IntStream;

public class Checkers {
	/*************************************
	 * b0 b1 b2 - X position, 0..7 b3 b4 b5 - Y position, 0..7 b6 - color, 0 black,
	 * 1 white b7 - piece, 0 pawn, 1 dame b8 - state, 0 captured, 1 in game
	 *************************************/

	static long white1 = 0b101001011101001001101000110101000100101000010101000000L;
	static long white2 = 0b101010110101010100101010010101010000101001111101001101L;
	static long black1 = 0b100110010100110000100101111100101101100101011100101001L;
	static long black2 = 0b100111111100111101100111011100111001100110110100110100L;
	static Long[] state = { white1, white2, black1, black2 };

	static long ifMask = 0b100000000L;
	static long posXMask = 0b111L;

	public static final String sep = " ";

	static char sqW = '\u2591';
	static char sqK = '\u2588';
	static char pwnW = 'O';
	static char pwnK = 'X';
	static char dameW = '\u019F';
	static char dameK = '\u0416';
	// n będzie numerować pionki. 0..11 to dwanaście pionków białych,
	// 12..23 to dwanaście pionków czarnych

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

	static byte getN(byte pos, boolean moves) {
		byte result = -1;
		if (moves == true)
			for (byte i = 0; i < 12; i++) {
				if (((pos / 10) == positionX(i)) && ((pos % 10) == positionY(i)) && isInGame(i)) {
					result = i;
					i = 24; // Break For
				}
			}
		else
			for (byte i = 12; i < 24; i++) {

				if (((pos / 10) == positionX(i)) && ((pos % 10) == positionY(i)) && isInGame(i)) {
					result = i;
					i = 24; // Break For
				}
			}
		return result;
		// rozdzielenie na kolory - ograniczy wykonywanie pętli
	}

	static byte getN(byte pos) {
		byte result = -1;
//			System.out.print("getN, isGame(i) \t");
		for (byte i = 0; i < 24; i++) {
//				System.out.print(isInGame(i) ? "1" : "0");
//				if (i % 4 == 3) System.out.print(" ");
			if (((pos / 10) == positionX(i)) && ((pos % 10) == positionY(i)) && isInGame(i)) {
				result = i;
				i = 24; // Break For
			}
		}
//			System.out.println();
		return result;
		// rozdzielenie na kolory - ograniczy wykonywanie pętli
	}

	static boolean validateMove(byte a, byte b, boolean moves) {
		boolean result = false;

		byte n = getN(a, moves);
		if ((n < 0) || (n >= 24)) {
			System.out.println("ERR: Pole startowe jest puste. \t n: " + n);
			result = false;
		} else if ((n / 12 == 0) ^ moves) {
			System.out.println("ERR: Pion złego koloru");
			result = false;
		}
		if (Math.abs((a % 10) - (b % 10)) == 2) {
			if (captured(a, b, moves) == true)
				result = true;
			else
				result = false;

		} else if (Math.abs((a % 10) - (b % 10)) != 1 && !isDame(n)) {
			System.out.println("ERR: Nie można się ruszać o więcej niż jedno pole");
			result = false;
		} else if ((a / 10) - (b / 10) != 1 && moves == true && !isDame(getN(a, moves))) {
			System.out.println("ERR: Nie można się ruszać o więcej niż jedno pole");
			result = false;
		} else if ((b / 10) - (a / 10) != 1 && moves == false && !isDame(getN(a, moves))) {
			System.out.println("ERR: Nie można się ruszać o więcej niż jedno pole");
			result = false;
		} else if (a / 10 < 0) {
			System.out.println("ERR: Pole startowe ma X < 0 \t X = " + (a / 10));
			result = false;
		} else if (a / 10 > 7) {
			System.out.println("ERR: Pole startowe ma X > 7 \t X = " + (a / 10));
			result = false;
		} else if (a % 10 > 7) {
			System.out.println("ERR: Pole startowe ma Y > 7 \t Y = " + (a % 10));
			result = false;
		} else if (((a / 10) + (a % 10)) % 2 == 1) {
			System.out.println("ERR: Pole startowe jest czarne \t " + a);
			result = false;
		} else if (b / 10 < 0) {
			System.out.println("ERR: Pole docelowe ma X < 0 \t X = " + (b / 10));
			result = false;
		} else if (b / 10 > 7) {
			System.out.println("ERR: Pole docelowe ma X > 7 \t X = " + (b / 10));
			result = false;
		} else if (b % 10 > 7) {
			System.out.println("ERR: Pole docelowe ma Y > 7 \t Y = " + (b % 10));
			result = false;
		} else if (((b / 10) + (b % 10)) % 2 == 1) {
			System.out.println("ERR: Pole docelowe jest czarne \t " + b);
			result = false;
		}

		n = getN(b, moves);
		if ((n >= 0) && (n < 24) && result == false) {
			System.out.println("ERR: Pole docelowe nie jest puste. \t n: " + n);
			result = false;
		} else {
			result = true;
		}

		if (b % 10 == 7 && moves == true) {
			updateDame(getN(a, moves));
			result = true;
		}
		if (b % 10 == 0 && moves == false) {
			updateDame(getN(a, moves));
			result = true;
		}
		return result;
	} // end validateMove

	private static boolean captured(byte a, byte b, boolean moves) {
		byte n = getN(a, moves);
		boolean result = false;
		byte avg = (byte) ((a + b) / 2);
		System.out.println("captured, kto ruch? \t" + moves);
		System.out.println("captured, pole bite: \t" + avg);
		System.out.println("captured, nr bitego: \t" + getN(avg, !moves));
		updateCaptured(getN(avg, !moves));
		if (getN(avg, !moves) > 0) {
			updatePosition(n, b);
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	static void updatePosition(byte n, byte pos) {
		long apos = positionX(n);
		apos = apos << (n % 6) * 9;
		state[n / 6] = state[n / 6] - apos;

		apos = pos / 10;
		apos = apos << (n % 6) * 9;
		state[n / 6] = state[n / 6] + apos;

		apos = positionY(n);
		apos = apos << ((n % 6) * 9 + 3);
		state[n / 6] = state[n / 6] - apos;

		apos = pos % 10;
		apos = apos << ((n % 6) * 9 + 3);
		state[n / 6] = state[n / 6] + apos;
	} // end updatePosition

	static void updateDame(byte n) {
		long apos = 1 << (n % 6) * 9 + 7;
		state[n / 6] = state[n / 6] + apos;

	}

	static void updateCaptured(byte n) {
		System.out.println("updateCaptured, n:\t" + n);
		long shift = (n % 6) * 9;
		System.out.println("updateCaptured, shift:\t" + shift);
		long aposs = (ifMask << ((n % 6) * 9));
		System.out.println(
				"updateCaptured, state:\t" + printBits(state[n / 6]) + "\nupdateCaptured, aposs:\t" + printBits(aposs));
		state[n / 6] = state[n / 6] - aposs;
		System.out.println("updateCaptured, stat':\t" + printBits(state[n / 6]));
		aposs = 0;
	}

	public static String printBits(long value) {
		StringBuffer sb = new StringBuffer();
		for (int shift = 63; shift >= 0; shift--) {
			if (shift % 9 == 8)
				sb.append(" ");
			sb.append((((value >>> shift) & 01) != 0) ? "1" : "0");
		}
		return sb.toString();
	}

	static boolean isEmpty(byte pos) {
		byte x = (byte) (pos / 10);
		byte y = (byte) (pos % 10);
		for (byte i = 0; i < 24; i++) {
			if (isInGame(i) && x == positionX(i) && y == positionY(i))
				return false;
		}
		return true;
	}

	static void isCapture(boolean moves) {
		// boolean result;
		if (moves)
			for (byte i = 0; i < 12; i++) // white
			{
				if (isInGame(i)) {
					if ((positionX(i) - 2 >= 0) && (positionY(i) - 2 >= 0)) {
						if (getN((byte) (position(i) - 11)) >= 12 && isEmpty((byte) (position(i) - 22))) {
							System.out.println("UpL Możliwe jest bicie z pola " + position(i));
						}
					}
					if ((positionX(i) + 2 <= 7) && (positionY(i) - 2 >= 0)) {
						if (getN((byte) (position(i) + 9)) >= 12 && isEmpty((byte) (position(i) + 18))) {
							System.out.println("UpR Możliwe jest bicie z pola " + position(i));
						}
					}
					if ((positionX(i) - 2 >= 0) && (positionY(i) + 2 <= 7)) {
						if (getN((byte) (position(i) - 9)) >= 12 && isEmpty((byte) (position(i) - 18))) {
							System.out.println("DnL Możliwe jest bicie z pola " + position(i));
						}
					}
					if ((positionX(i) + 2 <= 7) && (positionY(i) + 2 <= 7)) {
						if (getN((byte) (position(i) + 11)) >= 12 && isEmpty((byte) (position(i) + 22))) {
							System.out.println("DnR Możliwe jest bicie z pola " + position(i));
						}
					}
				}
			} // end for
		else {
			// black
			for (byte i = 12; i < 24; i++) {
				if (isInGame(i)) {
					if ((positionX(i) - 2 >= 0) && (positionY(i) - 2 >= 0)) {
						if (getN((byte) (position(i) - 11)) >= 0 && getN((byte) (position(i) - 11)) < 12
								&& isEmpty((byte) (position(i) - 22))) {
							System.out.println("UpL Możliwe jest bicie z pola " + position(i));
						}
					}
					if ((positionX(i) + 2 <= 7) && (positionY(i) - 2 >= 0)) {
						if (getN((byte) (position(i) + 9)) >= 0 && getN((byte) (position(i) + 9)) < 12
								&& isEmpty((byte) (position(i) + 18))) {
							System.out.println("UpR Możliwe jest bicie z pola " + position(i));
						}
					}
					if ((positionX(i) - 2 >= 0) && (positionY(i) + 2 <= 7)) {
						if (getN((byte) (position(i) - 9)) >= 0 && getN((byte) (position(i) - 9)) < 12
								&& isEmpty((byte) (position(i) - 18))) {
							System.out.println("DnL Możliwe jest bicie z pola " + position(i));
						}
					}
					if ((positionX(i) + 2 <= 7) && (positionY(i) + 2 <= 7)) {
						if (getN((byte) (position(i) + 11)) >= 0 && getN((byte) (position(i) + 11)) < 12
								&& isEmpty((byte) (position(i) + 22))) {
							System.out.println("DnR Możliwe jest bicie z pola " + position(i));
						}
					}
				}
			} // end for
		}
	}

	static void drawBoard() {
		System.out.println("białe 1:  \t" + printBits(state[0]));
		System.out.println("białe 2:  \t" + printBits(state[1]));
		System.out.println("czarne 1: \t" + printBits(state[2]));
		System.out.println("czarne 2: \t" + printBits(state[3]));
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
						if ((positionX(n) == x) && positionY(n) == y && isInGame(n)) {
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

		// Ostatni rząd z numerami kolumn
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
		long aMask = (posXMask << 3) << (n % 6) * 9;
		long result = state[n / 6] & aMask;
		result = result >> (n % 6) * 9 + 3;
		return result;
	}

	static byte position(byte n) {
		byte result = (byte) (10 * positionX(n) + positionY(n));
		return result;
	}
//		static long updatePposition(byte n, byte b) {
//			white2 = 0b101010110101010100101010010101011001101001111101001101L;
//			return white2;
//		}

	public static void main(String[] args) throws InterruptedException {
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
			if (moves)
				System.out.print("Ruch " + nameW);
			else
				System.out.print("Ruch " + nameK);
			System.out.print(", wpisz parę: ");
			String ab = "";
			try {
				ab = sc.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte a = Byte.parseByte(ab.substring(0, 2));
			byte b = Byte.parseByte(ab.substring(3));
			byte n = 0;
			int tmp = 0;
			byte[] capture = new byte[12];
			if (validateMove(a, b, moves)) {
				System.out.print("Ruch z pola \t X " + (a / 10) + "  Y " + (a % 10));
				System.out.print("\t na pole \t X " + (b / 10) + "  Y " + (b % 10) + "\n");
				updatePosition(getN(a, moves), b);
				moves = !moves;
			}
			drawBoard();
			isCapture(moves);
		}
	}// end Main
} // end Checkers