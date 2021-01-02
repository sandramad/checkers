import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Checkers {
	/*************************************
	 * b0 b1 b2 - X position, 0..7 b3 b4 b5 - Y position, 0..7 b6 - color, 0 black,
	 * 1 white b7 - piece, 0 pawn, 1 dame b8 - state, 0 captured, 1 in game
	 *************************************/
	// ułożenie startowe
	static long white1 = 0b101001011101001001101000110101000100101000010101000000L;
	static long white2 = 0b101010110101010100101010010101010000101001111101001101L;
	static long black1 = 0b100110010100110000100101111100101101100101011100101001L;
	static long black2 = 0b100111111100111101100111011100111001100110110100110100L;

	// ułożenie testujące wielobicia i konwersję na królówkę
//	static long white1 = 0b101001011101001001101001101101000100101000010101000000L;
//	static long white2 = 0b001011101101010100101010010001101011101001111001101101L;
//	static long black1 = 0b000010110100110000100101111000100110000100010100100000L;
//	static long black2 = 0b100111111100111101100111011100111001100110110100100110L;

	// ułożenie testujące zachowania królówki czarnych
//	static long white1 = 0b101001011101001001001001101101000100101000010101000000L;
//	static long white2 = 0b001011101001011101101010010001101011101001111001101101L;
//	static long black1 = 0b000010110100110000100101111000100110000100010100100000L;
//	static long black2 = 0b100111111100111101100111011100111001100110110110000110L;

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

	private static boolean captured(byte a, byte b, boolean moves) {
		byte n = getN(a, moves);
		boolean result = false;
		byte avg = (byte) ((a + b) / 2);
		if (getN(avg, !moves) > 0 && updateCaptured(getN(avg, !moves))) {
			updatePosition(n, b);
			result = true;
		} else {
			result = false;
		}
		return result;
	} // end captured

	static void drawBoard() {
		// do debugingu - sprawdzamy zapis bitowy gry
		// (można wykorzystać to tworzenia punktów startowych gry)
//		System.out.println("static long white1 =\t0b" + printBits(state[0]) + "L;");
//		System.out.println("static long white2 =\t0b" + printBits(state[1]) + "L;");
//		System.out.println("static long black1 =\t0b" + printBits(state[2]) + "L;");
//		System.out.println("static long black2 =\t0b" + printBits(state[3]) + "L;");
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
				} else
					System.out.print(sqK + sep);
			}
			System.out.println(y);
		}

		// Ostatni rząd z numerami kolumn
		System.out.print(sep + " ");
		for (byte x = 0; x < 8; x++)
			System.out.print(x + sep);
		System.out.println();
	}

	static String isCapture(boolean moves) {
		String results = "";
		if (moves)
			for (byte i = 0; i < 12; i++) // białe
			{
				if (isInGame(i)) {
					if (isDame(i)) {
						byte x = (byte) positionX(i);
						byte y = (byte) positionY(i);
						// to do
					} else {
						if ((positionX(i) - 2 >= 0) && (positionY(i) - 2 >= 0)) {
							if (getN((byte) (position(i) - 11)) >= 12 && isEmpty((byte) (position(i) - 22)))
								results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
						}
						if ((positionX(i) + 2 <= 7) && (positionY(i) - 2 >= 0)) {
							if (getN((byte) (position(i) + 9)) >= 12 && isEmpty((byte) (position(i) + 18)))
								results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
						}
						if ((positionX(i) - 2 >= 0) && (positionY(i) + 2 <= 7)) {
							if (getN((byte) (position(i) - 9)) >= 12 && isEmpty((byte) (position(i) - 18)))
								results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
						}
						if ((positionX(i) + 2 <= 7) && (positionY(i) + 2 <= 7)) {
							if (getN((byte) (position(i) + 11)) >= 12 && isEmpty((byte) (position(i) + 22)))
								results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
						}
					} // end notDame
				} // end if isInGame
			} // end for
		else
			for (byte i = 12; i < 24; i++) { // czarne
				if (isInGame(i)) {
					if ((positionX(i) - 2 >= 0) && (positionY(i) - 2 >= 0)) {
						if (getN((byte) (position(i) - 11)) >= 0 && getN((byte) (position(i) - 11)) < 12
								&& isEmpty((byte) (position(i) - 22)))
							results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";

					}
					if ((positionX(i) + 2 <= 7) && (positionY(i) - 2 >= 0)) {
						if (getN((byte) (position(i) + 9)) >= 0 && getN((byte) (position(i) + 9)) < 12
								&& isEmpty((byte) (position(i) + 18)))
							results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
					}
					if ((positionX(i) - 2 >= 0) && (positionY(i) + 2 <= 7)) {
						if (getN((byte) (position(i) - 9)) >= 0 && getN((byte) (position(i) - 9)) < 12
								&& isEmpty((byte) (position(i) - 18)))
							results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
					}
					if ((positionX(i) + 2 <= 7) && (positionY(i) + 2 <= 7)) {
						if (getN((byte) (position(i) + 11)) >= 0 && getN((byte) (position(i) + 11)) < 12
								&& isEmpty((byte) (position(i) + 22)))
							results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
					}
				} // end isInGame
			} // end for
		return results;
	} // end isCapture

	static String isCapture(byte b, boolean moves) { // sprawdzamy czy jest bicie dla pionka, którym właśnie zbiliśmy
		String results = "";
		byte i = getN(b, moves);
		if (moves) { // białe
			if ((positionX(i) - 2 >= 0) && (positionY(i) - 2 >= 0)) {
				if (getN((byte) (position(i) - 11)) >= 12 && isEmpty((byte) (position(i) - 22)))
					results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
			}
			if ((positionX(i) + 2 <= 7) && (positionY(i) - 2 >= 0)) {
				if (getN((byte) (position(i) + 9)) >= 12 && isEmpty((byte) (position(i) + 18)))
					results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
			}
			if ((positionX(i) - 2 >= 0) && (positionY(i) + 2 <= 7)) {
				if (getN((byte) (position(i) - 9)) >= 12 && isEmpty((byte) (position(i) - 18)))
					results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
			}
			if ((positionX(i) + 2 <= 7) && (positionY(i) + 2 <= 7)) {
				if (getN((byte) (position(i) + 11)) >= 12 && isEmpty((byte) (position(i) + 22)))
					results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
			}
		} else { // czarne
			if ((positionX(i) - 2 >= 0) && (positionY(i) - 2 >= 0)) {
				if (getN((byte) (position(i) - 11)) >= 0 && getN((byte) (position(i) - 11)) < 12
						&& isEmpty((byte) (position(i) - 22)))
					results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
			}
			if ((positionX(i) + 2 <= 7) && (positionY(i) - 2 >= 0)) {
				if (getN((byte) (position(i) + 9)) >= 0 && getN((byte) (position(i) + 9)) < 12
						&& isEmpty((byte) (position(i) + 18)))
					results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
			}
			if ((positionX(i) - 2 >= 0) && (positionY(i) + 2 <= 7)) {
				if (getN((byte) (position(i) - 9)) >= 0 && getN((byte) (position(i) - 9)) < 12
						&& isEmpty((byte) (position(i) - 18)))
					results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
			}
			if ((positionX(i) + 2 <= 7) && (positionY(i) + 2 <= 7)) {
				if (getN((byte) (position(i) + 11)) >= 0 && getN((byte) (position(i) + 11)) < 12
						&& isEmpty((byte) (position(i) + 22)))
					results += (position(i) < 10) ? "0" + position(i) + " " : position(i) + " ";
			}
		}
		return results;
	}

	static boolean isDame(byte n) {
		long flag = -1;
		long aMask = ifMask >> 1; // tworzę maskę dla damki
		aMask = aMask << (n % 6) * 9; // tworzę lokalną maskę, specyficzną dla piona n
		flag = state[n / 6] & aMask; // sprawdza, czy jest 1 na miejscu b7 piona n w longu stanu gry
		flag = flag >> (n % 6) * 9 + 7; // przesuwa bit na ostatnie miejsce po prawej
		return (flag == 1);
	}

	static boolean isEmpty(byte pos) {
		byte x = (byte) (pos / 10);
		byte y = (byte) (pos % 10);
		for (byte i = 0; i < 24; i++)
			if (isInGame(i) && x == positionX(i) && y == positionY(i))
				return false;
		return true;
	} // end isEmpty

	static boolean isInGame(byte n) {
		long flag = -1L;
		long aMask = ifMask << (n % 6) * 9; // tworzę lokalną maskę, specyficzną dla piona n
		flag = state[n / 6] & aMask; // sprawdza, czy jest 1 na miejscu b8 piona n w longu stanu gry
		flag = flag >> (n % 6) * 9 + 8; // przesuwa bit na ostatnie miejsce po prawej
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
		for (byte i = 0; i < 24; i++) {
			if (((pos / 10) == positionX(i)) && ((pos % 10) == positionY(i)) && isInGame(i)) {
				result = i;
				i = 24; // Break For
			}
		}
		return result;
	} // do wykorzystania gdy musimy sprawdzić całą tablicę

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

	public static String printBits(long value) { // funkcja do debugingu, bez znaczenia dla gry
		StringBuffer sb = new StringBuffer();
		for (int shift = 53; shift >= 0; shift--) {
			if (shift % 9 == 8)
				sb.append(" "); // z podziałem na pionki
			sb.append((((value >>> shift) & 01) != 0) ? "1" : "0");
		}
		return sb.toString();
	} // end printBits

	static boolean validateMoveDame(byte a, byte b, boolean moves) {
		boolean result;
		boolean err = false;
		byte x = (byte) (a / 10);
		byte y = (byte) (a % 10);
		byte xEnd = (byte) (b / 10);
		byte yEnd = (byte) (b % 10);
		byte change = (byte) (x - xEnd); // sprawdzamy czy ruszamy się w prawo czy w lewo
		byte xEmpty = 0;
		byte yEmpty = 0;
		if (change < 0) {
			for (byte i = (byte) (x + 1); i <= xEnd; i++) {
				y = (byte) ((y - yEnd < 0) ? y + 1 : y - 1);
				byte iTmp = Byte.parseByte(i + "" + y);
				if ((getN(iTmp) / 12 == 0) ^ moves) {
					yEmpty = (byte) ((y - yEnd < 0) ? y + 1 : y - 1);
					xEmpty = (byte) (i + 1);
					if (isEmpty(Byte.parseByte(xEnd + "" + yEnd))) {
						if (b == (byte) (xEmpty * 10 + yEmpty))
							updateCaptured(getN(iTmp));
						else {
							System.out.println("Możliwe jest bicie na polu " + iTmp + " więc musisz stanąć na polu "
									+ xEmpty + "" + yEmpty);
							err = true;
							i = (byte) (xEnd + 1); // nie sprawdzamy dalej
						}
					} else {
						System.out.println("ERR: niedozwolone bicie");
						i = (byte) (xEnd + 1);
						err = true;
					}
				} else if (getN(iTmp) > 0) {
					System.out.println("ERR: Na polu " + iTmp + " stoi pionek");
					err = true;
				}
			}
		}
		if (change > 0) {
			for (byte i = (byte) (x - 1); i >= xEnd; i--) {
				y = (byte) ((y - yEnd < 0) ? y + 1 : y - 1);
				byte iTmp = Byte.parseByte(i + "" + y);
				if (getN(iTmp) > 0 && ((getN(iTmp) / 12 == 0) ^ moves)) {
					yEmpty = (byte) ((y - yEnd < 0) ? y + 1 : y - 1);
					xEmpty = (byte) (i - 1);
					if (isEmpty((byte) (xEmpty * 10 + yEmpty))) {
						if (b == (byte) (xEmpty * 10 + yEmpty))
							updateCaptured(getN(iTmp));
						else {
							System.out.println("Możliwe jest bicie na polu  " + iTmp + " więc musisz stanąć na polu "
									+ xEmpty + "" + yEmpty);
							err = true;
							i = (byte) (xEnd - 1); // nie sprawdzamy dalej
						}
					} else {
						System.out.println("ERR: niedozwolone bicie");
						i = (byte) (xEnd - 1);
						err = true;

					}
				} else if (getN(iTmp) > 0) {
					System.out.println("ERR: Na polu " + iTmp + " stoi pionek");
					err = true;
				}
			}
		}
		if (err == true)
			result = false;
		else
			result = true;
		return result;
	}

	static boolean validateMove(byte a, byte b, boolean moves) {
		boolean result = false;
		boolean err = false;
		boolean block = false;

		byte n = getN(a);
		if ((n < 0) || (n > 24)) {
			System.out.println("ERR: Pole startowe jest puste. \t a: " + a);
			err = true;
		}
		if ((n / 12 == 0) ^ moves) {
			System.out.println("ERR: Pion złego koloru");
			err = true;
		}
		n = getN(a, moves);
		if (isDame(n) && Math.abs((a % 10) - (b % 10)) > 1) {
			err = !validateMoveDame(a, b, moves);
			block = true;
		} else {
			if (Math.abs((a % 10) - (b % 10)) == 2 && Math.abs((a / 10) - (b / 10)) == 2
					&& isCapture(moves).length() > 1) {
				if (captured(a, b, moves) == true) {
					block = true; // jeśli doszło do bicia niedopuszczamy do sprawdzania czy ruch jest +1
					if (isCapture(b, moves).length() > 2) {
						drawBoard();
						System.out.println("Masz kolejne bicie na polu " + b);
						err = true; // nie oddajemy kolejki przeciwnikowi
					}
				} else {
					System.out.println("ERR: Nieudane bicie");
					err = true; // zły ruch
				}
			}
			if (Math.abs((a % 10) - (b % 10)) != 1 && !isDame(n) && block == false) {
				System.out.println("ERR: Nie można się ruszać o więcej niż jedno pole");
				err = true;
			} else if ((b % 10) - (a % 10) != 1 && moves == true && !isDame(getN(a, moves)) && block == false) {
				System.out.println("ERR: Nie można się ruszać o więcej niż jedno pole");
				err = true;
			} else if ((a % 10) - (b % 10) != 1 && moves == false && !isDame(getN(a, moves)) && block == false) {
				System.out.println("ERR: Nie można się ruszać o więcej niż jedno pole");
				err = true;
			}
			if (a / 10 < 0) {
				System.out.println("ERR: Pole startowe ma X < 0 \t X = " + (a / 10));
				err = true;
			} else if (a / 10 > 7) {
				System.out.println("ERR: Pole startowe ma X > 7 \t X = " + (a / 10));
				err = true;
			}
			if (a % 10 < 0) {
				System.out.println("ERR: Pole startowe ma Y < 0 \t Y = " + (a % 10));
				err = true;
			} else if (a % 10 > 7) {
				System.out.println("ERR: Pole startowe ma Y > 7 \t Y = " + (a % 10));
				err = true;
			}
			if (((a / 10) + (a % 10)) % 2 == 1) {
				System.out.println("ERR: Pole startowe jest czarne \t " + a);
				err = true;
			}
			if (b / 10 < 0) {
				System.out.println("ERR: Pole docelowe ma X < 0 \t X = " + (b / 10));
				err = true;
			} else if (b / 10 > 7) {
				System.out.println("ERR: Pole docelowe ma X > 7 \t X = " + (b / 10));
				err = true;
			}
			if (b % 10 < 0) {
				System.out.println("ERR: Pole docelowe ma Y < 0 \t Y = " + (b % 10));
				err = true;
			} else if (b % 10 > 7) {
				System.out.println("ERR: Pole docelowe ma Y > 7 \t Y = " + (b % 10));
				err = true;
			}
			if (((b / 10) + (b % 10)) % 2 == 1) {
				System.out.println("ERR: Pole docelowe jest czarne \t " + b);
				err = true;
			}

			n = getN(b);
			if ((n >= 0) && (n < 24) && err == false && block == false) {
				System.out.println("ERR: Pole docelowe nie jest puste. \t n: " + n);
				err = true;
			}
		}
		if (err == true)
			result = false;
		else
			result = true;

		return result;
	}// end validateMove

	static boolean updateCaptured(byte n) {
		boolean result = false;
		long aposs = (ifMask << ((n % 6) * 9));
		if (aposs > 128) {
			state[n / 6] = state[n / 6] - aposs;
			result = true;
		}
		return result;

	} // end updateCaptured

	static void updateDame(byte n) {
		long aposs = ((ifMask >> 1) << ((n % 6) * 9));
		state[n / 6] = state[n / 6] + aposs;

	} // end updateDame

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

	public static void main(String[] args) throws InterruptedException {
		String nameW = "";
		String nameK = "";
		boolean game = true;
		boolean moves = true;
		boolean captured = true;
		String color = "";

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
				"Pozycję podawaj parami współrzędnych - z jakiej pozycji chcesz się ruszyć na jaką np. \"02-13\"");
		while (game) {
			if (moves) {
				System.out.print("Ruch " + nameW);
				color = "białych";
			} else {
				System.out.print("Ruch " + nameK);
				color = "czarnych";
			}
			System.out.print(", wpisz parę: ");
			String ab = null;
			try {
				ab = sc.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (ab.equalsIgnoreCase("koniec") || ab.length() == 0) {
				if (!moves)
					color = "białe";
				else
					color = "czarne";
				System.out.println("Dziękujemy za grę, wygrały " + color);
				System.exit(0);
			}
			byte b = 0;
			ab = ab.replaceAll("d", "");
			ab = ab.replaceAll("D", "");
			if (ab.length() == 4)
				b = Byte.parseByte(ab.substring(2, 4));
			else
				b = Byte.parseByte(ab.substring(3, 5));
			byte a = Byte.parseByte(ab.substring(0, 2));

			if (isCapture(moves).length() > 2) {
				captured = false;
				String[] must = isCapture(moves).substring(0, (isCapture(moves).length() - 1)).split(" ");
				for (byte i = 0; i < must.length && captured == false; i++)
					if (a == Byte.parseByte(must[i]) && Math.abs(a - b) > 16)
						captured = true;
			}
			if (validateMove(a, b, moves) && captured == true) {
				System.out.print("Ruch " + color + " z pola X: " + (a / 10) + "  Y: " + (a % 10));
				System.out.println("\tna pole X: " + (b / 10) + "  Y: " + (b % 10));

				updatePosition(getN(a, moves), b);
				if ((moves && (b % 10 == 7)) || (!moves && (b % 10 == 0)))
					updateDame(getN(b, moves));
				moves = !moves;
				drawBoard();
				if (isCapture(moves).length() > 3)
					System.out.println("Możliwe bicia na polach: " + isCapture(moves));
				if (isCapture(moves).length() == 3)
					System.out.println("Możliwe bicie na polu: " + isCapture(moves));
			} else if (isCapture(moves).length() > 2)
				System.out.println("Bicia są obowiązkowe");
			isCapture(moves);
		} // end while
	}// end Main
} // end Checkers