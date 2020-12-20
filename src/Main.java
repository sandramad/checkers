import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
	static String pionBialy = "\u2659 ";
	static String pionCzarny = "\u265f ";
	static String poleBialy = "\u2b1b ";
	static String poleCzarny = "\u2b1c ";
	static String krolBialy = "\u2655 ";
	static String krolCzarny = "\u265B ";
	static String pion = "";

	public static void main(String[] args) {
		boolean t = false;
		boolean kolejka = true;
		String ruch = "";
		int sumaBialy = 12;
		int sumaCzarny = 12;
		BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Wpisz nazwę pierwszego gracza: ");
		String nazwa = "";
		try {
			nazwa = scanner.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.print("Wpisz nazwę drugiego gracza: ");
		String oponent = "";
		try {
			oponent = scanner.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(nazwa + " poruszasz się " + pionBialy);
		System.out.println(oponent + " poruszasz się " + pionCzarny);
		String[][] plansza = { { poleCzarny, "0 ", "1 ", " 2 ", "3 ", "4 ", " 5 ", "6 ", " 7 ", poleCzarny },
				{ "0 ", poleBialy, pionCzarny, poleBialy, pionCzarny, poleBialy, pionCzarny, poleBialy, pionCzarny,
						"0" },
				{ "1 ", pionCzarny, poleBialy, pionCzarny, poleBialy, pionCzarny, poleBialy, pionCzarny, poleBialy,
						"1" },
				{ "2 ", poleBialy, pionCzarny, poleBialy, pionCzarny, poleBialy, pionCzarny, poleBialy, pionCzarny,
						"2" },
				{ "3 ", poleCzarny, poleBialy, poleCzarny, poleBialy, poleCzarny, poleBialy, poleCzarny, poleBialy,
						"3" },
				{ "4 ", poleBialy, poleCzarny, poleBialy, poleCzarny, poleBialy, poleCzarny, poleBialy, poleCzarny,
						"4" },
				{ "5 ", pionBialy, poleBialy, pionBialy, poleBialy, pionBialy, poleBialy, pionBialy, poleBialy, "5" },
				{ "6 ", poleBialy, pionBialy, poleBialy, pionBialy, poleBialy, pionBialy, poleBialy, pionBialy, "6" },
				{ "7 ", pionBialy, poleBialy, pionBialy, poleBialy, pionBialy, poleBialy, pionBialy, poleBialy, "7" },
				{ poleCzarny, "0 ", "1 ", " 2 ", "3 ", "4 ", " 5 ", "6 ", " 7 ", poleCzarny } };
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				System.out.print(plansza[x][y]);
			}
			System.out.println();
		}
		System.out.println("\nInstrukcja: należy wpisywać pary pozycji oddzielone spacją (np. 50 41)");
		for (;;) {
			String pionTmp = "";
			if (sumaBialy == 0) {
				System.out.println("Brawo, wygrały czarne");
				System.exit(0);
			}
			if (sumaCzarny == 0) {
				System.out.println("Brawo, wygrały białe");
				System.exit(0);
			}

			if (kolejka) {
				System.out.print(nazwa + " ruch: ");
				try {
					ruch = scanner.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (ruch.equalsIgnoreCase("koniec")) {
					System.out.println("Koniec gry, wygrały czarne");
					System.exit(0);
				}	
				int a = Integer.parseInt("" + ruch.charAt(0)) + 1;
				int b = Integer.parseInt("" + ruch.charAt(1)) + 1;
				int c = Integer.parseInt("" + ruch.charAt(3)) + 1;
				int d = Integer.parseInt("" + ruch.charAt(4)) + 1;
				System.out.println(c);
				if (plansza[a][b].equals(pionBialy))
					pionTmp = pionBialy;
				else if (plansza[a][b].equals(krolBialy))
					pionTmp = krolBialy;
				if (mozliwyRuch(pionTmp, a, c) == true) {
					if (plansza[a][b].equals(pionTmp) && plansza[c][d].equals(poleCzarny)
							&& ((a - c == 1) || a - c == -1) && (b - d == 1 || b - d == -1)) {
						plansza[a][b] = poleCzarny;
						if (c == 1)
							plansza[c][d] = krolBialy;
						else
							plansza[c][d] = pionTmp;
						t = false;
					} else if (plansza[a + 1][b + 1].equals(pionCzarny) && plansza[a][b].equals(pionTmp)
							&& plansza[c][d].equals(poleCzarny)) {
						plansza[a + 1][b + 1] = poleCzarny;
						sumaCzarny -= 1;
						plansza[a][b] = poleCzarny;
						if (c == 1)
							plansza[c][d] = krolBialy;
						else
							plansza[c][d] = pionTmp;
						t = false;
					} else if (plansza[a + 1][b - 1].equals(pionCzarny) && plansza[a][b].equals(pionTmp)
							&& plansza[c][d].equals(poleCzarny)) {
						sumaCzarny -= 1;
						plansza[a + 1][b - 1] = poleCzarny;
						plansza[a][b] = poleCzarny;
						if (c == 1)
							plansza[c][d] = krolBialy;
						else
							plansza[c][d] = pionTmp;
						t = false;
					} else if (plansza[a - 1][b + 1].equals(pionCzarny) && plansza[a][b].equals(pionTmp)
							&& plansza[c][d].equals(poleCzarny)) {
						sumaCzarny -= 1;
						plansza[a - 1][b + 1] = poleCzarny;
						plansza[a][b] = poleCzarny;
						if (c == 1)
							plansza[c][d] = krolBialy;
						else
							plansza[c][d] = pionTmp;
						t = false;
					} else if (plansza[a - 1][b - 1].equals(pionCzarny) && plansza[a][b].equals(pionTmp)
							&& plansza[c][d].equals(poleCzarny)) {
						sumaCzarny -= 1;
						plansza[a - 1][b - 1] = poleCzarny;
						plansza[a][b] = poleCzarny;
						if (c == 0)
							plansza[c][d] = krolBialy;
						else
							plansza[c][d] = pionTmp;
						t = false;
					} else {
						System.out.println("Nieprawidłowa wartość, spróbuj jeszcze raz!");
						t = true;

					}
				} else {
					System.out.println("Możesz poruszać sie tylko do przodu");
				}
				for (int x = 0; x < 10; x++) {
					for (int y = 0; y < 10; y++) {
						System.out.print(plansza[x][y]);
					}
					System.out.println();
				}
				kolejka = t;

			} else if (kolejka == false) {
				System.out.print(oponent + " ruch: ");
				try {
					ruch = scanner.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (ruch.equalsIgnoreCase("koniec")) {
					System.out.println("Koniec gry, wygrały białe");
					System.exit(0);
				}	
				int a = Integer.parseInt("" + ruch.charAt(0)) + 1;
				int b = Integer.parseInt("" + ruch.charAt(1)) + 1;
				int c = Integer.parseInt("" + ruch.charAt(3)) + 1;
				int d = Integer.parseInt("" + ruch.charAt(4)) + 1;
				if (plansza[a][b].equals(pionCzarny))
					pionTmp = pionCzarny;
				else if (plansza[a][b].equals(krolCzarny))
					pionTmp = krolCzarny;
				if (mozliwyRuch(pionTmp, a, c) == true) {
					if (plansza[a][b].equals(pionTmp) && plansza[c][d].equals(poleCzarny)
							&& ((a - c == 1) || a - c == -1) && (b - d == 1 || b - d == -1)) {
						plansza[a][b] = poleCzarny;
						sumaBialy -= 1;
						if (c == 8)
							plansza[c][d] = krolCzarny;
						else
							plansza[c][d] = pionTmp;
						t = true;
					} else if (plansza[a + 1][b + 1].equals(pionBialy) && plansza[a][b].equals(pionTmp)
							&& plansza[c][d].equals(poleCzarny)) {
						plansza[a + 1][b + 1] = poleCzarny;
						plansza[a][b] = poleCzarny;
						sumaBialy -= 1;
						if (c == 8)
							plansza[c][d] = krolCzarny;
						else
							plansza[c][d] = pionTmp;
						t = true;
					} else if (plansza[a + 1][b - 1].equals(pionBialy) && plansza[a][b].equals(pionTmp)
							&& plansza[c][d].equals(poleCzarny)) {
						plansza[a + 1][b - 1] = poleCzarny;
						plansza[a][b] = poleCzarny;
						sumaBialy -= 1;
						if (c == 8)
							plansza[c][d] = krolCzarny;
						else
							plansza[c][d] = pionTmp;
						t = true;
					} else if (plansza[a - 1][b + 1].equals(pionBialy) && plansza[a][b].equals(pionTmp)
							&& plansza[c][d].equals(poleCzarny)) {
						plansza[a - 1][b + 1] = poleCzarny;
						plansza[a][b] = poleCzarny;
						sumaBialy -= 1;
						if (c == 8)
							plansza[c][d] = krolCzarny;
						else
							plansza[c][d] = pionTmp;
						t = true;
					} else if (plansza[a - 1][b - 1].equals(pionBialy) && plansza[a][b].equals(pionTmp)
							&& plansza[c][d].equals(poleCzarny)) {
						plansza[a - 1][b - 1] = poleCzarny;
						plansza[a][b] = poleCzarny;
						sumaBialy -= 1;
						if (c == 8)
							plansza[c][d] = krolCzarny;
						else
							plansza[c][d] = pionTmp;
						t = true;
					} else {
						System.out.println("Nieprawidłowe pole, wpisz jeszcze raz");
						t = false;
					}
				} else {
					System.out.println("Możesz poruszać się tylko do przodu");
				}
				for (int x = 0; x < 10; x++) {
					for (int y = 0; y < 10; y++) {
						System.out.print(plansza[x][y]);
					}
					System.out.println();
				}
				kolejka = t;
			}

		}
	}

	private static boolean mozliwyRuch(String pion, int a, int c) {
		boolean r = false;
		if (pion.equals(pionBialy) || pion.equals(pionCzarny)) {
			if (pion.equals(pionBialy) && a - c > 0)
				r = true;
			else if (pion.equals(pionBialy))
				r = false;
			else if (pion.equals(pionCzarny) && c - a > 0)
				r = true;
			else if (pion.equals(pionCzarny))
				r = false;
		} else
			r = true;
		return r;
	}
}