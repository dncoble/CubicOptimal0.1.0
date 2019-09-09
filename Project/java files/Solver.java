/* contains main, the place where we actually iterate through scrambles and
 * use the tables created in TableBuilder.
 */
import java.util.*;
import java.io.*;
import java.lang.*;
public class Solver {
	public static ArrayList<HashMap<Integer, Short>> rawTables;
	public static ArrayList<Integer> readRawTables;
	public static ArrayList<HashMap<Integer, Short>> symTables;
	public static ArrayList<Integer> readSymTables;
	public static ArrayList<ArrayList<Integer>> rtsTables;
	
	public static void main (String [] args) throws FileNotFoundException, IOException {
//		HashMap<IntScramble, IntScramble> testMap = new HashMap<IntScramble, IntScramble>();
//		testMap.put(new IntScramble(new int[] {0}), new IntScramble(new int[] {1}));
//		System.out.println(testMap.get(new IntScramble(new int[] {0})).getIntArray()[0]);
//		Scramble testScramble = new Scramble("U");
//		int[] test = testScramble.toInt();
//		System.out.println(testScramble.toSingleInt());
//		testScramble = new Scramble("U F");
//		test[test.length - 1] = ((test[test.length - 1] + Integer.MIN_VALUE) * 15) + Integer.MIN_VALUE;
//		System.out.println(testScramble.toSingleInt());
//		System.out.println(test[0]);
//		TableBuilder.makeRawToSym(4);
//		ArrayList<Integer> reo = (ArrayList<Integer>) TableBuilder.readMapFromFile(TableBuilder.getRawToSymFile(5));
//		System.out.println(reo.size());
		rawTables = new ArrayList<HashMap<Integer, Short>>();
		readRawTables = new ArrayList<Integer>();
		symTables = new ArrayList<HashMap<Integer, Short>>();
		readSymTables = new ArrayList<Integer>();
		rtsTables = new ArrayList<ArrayList<Integer>>();
		/* asking for scramble(s), asking for tables, then making tables if they have
		 * not been made already.*/
		Scanner scan = new Scanner(System.in);
		System.out.println("Print all scrambles you would like to solve. If there are multiple, \n"
				+ "separate them by commas.");
		System.out.println("Scrambles: ");
		String rawScrambles = scan.nextLine();
		String[] rawListScrambles = rawScrambles.split(",");
		System.out.println("Enter what tables you wish to use. \n"
				+ "		Raw:	Sym: \n"
				+ "CO		1		7 \n"
				+ "CP		2		8 \n"
				+ "EO		3		9 \n"
				+ "EP		4		10 \n"
				+ "RCO		5		11 \n"
				+ "REO		6 		12 \n"
				+ "DONE:	0");
		System.out.println("Table: ");
		ArrayList<Integer> tablesUsed = new ArrayList<Integer>();
		String currentTable = scan.nextLine();
		while(!currentTable.equals("0")) {
			try {
				int nextTable = Integer.parseInt(currentTable);
				if(nextTable < 0 || nextTable > 12) {throw new NumberFormatException();}
				tablesUsed.add(nextTable);
				System.out.println("Enter what tables you wish to use. \n"
						+ "		Raw:	Sym: \n"
						+ "CO		1		7 \n"
						+ "CP		2		8 \n"
						+ "EO		3		9 \n"
						+ "EP		4		10 \n"
						+ "RCO		5		11 \n"
						+ "REO		6 		12 \n"
						+ "DONE:	0");
				System.out.println("Table: ");
				currentTable = scan.nextLine();
			}
			catch(NumberFormatException e) {
				System.out.println("Invalid number entered \n"
						+ "		Raw:	Sym: \n"
						+ "CO		1		7 \n"
						+ "CP		2		8 \n"
						+ "EO		3		9 \n"
						+ "EP		4		10 \n"
						+ "RCO		5		11 \n"
						+ "REO		6 		12 \n"
						+ "DONE:	0");
				System.out.println("Table: ");
				currentTable = scan.nextLine();
			}
		}
        File[] filesList = new File(".").listFiles();
        ArrayList<Integer> makingTables = new ArrayList<Integer>();
		for(int i : tablesUsed) {
			if(i < 7) {
				String tableFile = TableBuilder.getFile(i - 1);
				boolean contains = false;
				for(File j : filesList) {
					if(j.getName().equals(tableFile)){
						contains = true;
					}
				}
				if(!contains) {makingTables.add(i);}
			}
			else {
				String tableFile = TableBuilder.getSymFile(i - 7);
				boolean contains = false;
				for(File j : filesList) {
					if(j.getName().equals(tableFile)){
						contains = true;
					}
				}
				if(!contains) {makingTables.add(i);}
				tableFile = TableBuilder.getRawToSymFile(i - 7);
				contains = false;
				for(File j : filesList) {
					if(j.getName().equals(tableFile)){
						contains = true;
					}
				}
				if(!contains) {makingTables.add(i + 6);}
			}
		}
		if(makingTables.size() > 0) {
			System.out.println("Some of those tables have not been made. They will be made now. \n"
					+ "This may take a while.");
			for(int k : makingTables) {
				if(k < 7) {TableBuilder.makeTable(k - 1);}
				else if(k < 13) {TableBuilder.makeSymTable(k - 7);}
				else {TableBuilder.makeRawToSym(k - 13);}
			}
		}
		for(int l : tablesUsed) {
			if(l < 7) {
				rawTables.add((HashMap<Integer, Short>) TableBuilder.readMapFromFile(TableBuilder.getFile(l - 1)));
				readRawTables.add(l - 1);
			}
			else {
				symTables.add((HashMap<Integer, Short>) TableBuilder.readMapFromFile(TableBuilder.getSymFile(l - 7)));
				rtsTables.add((ArrayList<Integer>) TableBuilder.readMapFromFile(TableBuilder.getRawToSymFile(l - 7)));
				readSymTables.add(l - 7);
			}
		}
		/* this is where the searching happens.
		 * currently the pruneMaps are implemented as an ArrayList of HashMaps
		 * and the pruning tables as an ArrayList of HashMaps
		 * 21 hashMaps, for lengths 0-21. there very well may be a better way. */
		ArrayList<Scramble> solvedScrambles = new ArrayList<Scramble>();
		for(String j : rawListScrambles) {
			complete:
			while(true) { // i don't think this while(true) is required.
				Scramble identityScr = new Scramble(j);
				Scramble scr = new Scramble();
				Cube scrambled = new Cube(identityScr);
				Cube cube = scrambled.clone();
				Cube solved = new Cube();
				if(cube.equals(solved)) {solvedScrambles.add(scr); break complete;}
				ArrayList<HashMap<IntScramble, IntScramble>> pruneMaps = new ArrayList<HashMap<IntScramble, IntScramble>>();
				for(int o = 0; o < 21; o ++) {pruneMaps.add(new HashMap<IntScramble, IntScramble>());}
				int length = 1;
				IntScramble current = new IntScramble(new int[]{Integer.MIN_VALUE});
				int[][] pruneFirst = new int[21][];
				int[][] pruneSecond = new int[21][];
				for(int q = 0; q < 9; q ++) {
					pruneFirst[q] = new int[] {Integer.MIN_VALUE};
					pruneSecond[q] =  new int[] {Integer.MIN_VALUE};
				}
				for(int r = 0; r < 17; r ++) {
					pruneFirst[r] = new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE};
					pruneSecond[r] =  new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE};
				}
				for(int s = 0; s < 17; s ++) {
					pruneFirst[s] = new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
					pruneSecond[s] = new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
				}
				while(true) {
					boolean finishedLength = false;
					scr = new Scramble(length);
					cube.move(scr);
					while(!finishedLength) {
						short pruningDepth = getPruningDepth(cube);
						if(pruningDepth == 0 && cube.equals(solved)) {solvedScrambles.add(scr); break complete;}
						for(int i = 1; i < pruningDepth; i ++) {
							/* I couldn't figure out tying, which if the pruneMap contains 
							 * two items <a, b> and <b, c>, merges
							 * them into <a, c>.  pruneMaps are exclusive on the larger number,
							 * meaning they map to the first scramble which was not included*/
							int[] first = current.getIntArray().clone();
							IntScramble secondScr = current.clone();
							secondScr.iterate(length);
							int[] second = secondScr.getIntArray();
							for(int k = 0; k < i; k ++) {
								if(length + i - 1 % 8 == 0) {
									int[] newFirst = new int[first.length + 1];
									for(int l : first) {newFirst[l] = l;}
									first = newFirst;
									first[first.length - 1] = Integer.MIN_VALUE;
								}
								else {
									first[first.length - 1] = ((first[first.length - 1] + Integer.MIN_VALUE) * 15) + Integer.MIN_VALUE;
								}
								if(length + i - 1 % 8 == 0) {
									int[] newSecond = new int[second.length + 1];
									for(int l : second) {newSecond[l] = l;}
									second = newSecond;
									second[second.length - 1] = Integer.MIN_VALUE;
								}
								else {
									second[second.length - 1] = ((second[second.length - 1] + Integer.MIN_VALUE) * 15) + Integer.MIN_VALUE;
								}
							}
							if(Arrays.equals(first, pruneSecond[length + i])) {
								pruneSecond[length + i] = first;
							}
							else {
								if(!pruneMaps.get(length + i).containsKey(new IntScramble(pruneFirst[length + i]))) {//this if may or may not be necessary. i'm including it for now.
									pruneMaps.get(length + i).put(new IntScramble(pruneFirst[length + i]), new IntScramble(pruneSecond[length + i]));
							}
								pruneFirst[length + i] = first;
								pruneSecond[length + i] = second;
							}
//							if(!pruneMaps.get(length + i).containsKey(new IntScramble(first))) {
//								pruneMaps.get(length + i).put(new IntScramble(first), new IntScramble(second));
//							}
						}
						if(pruneMaps.get(length).containsKey(current)) {
//							if(length == 6) {
//								System.out.println("getting here");
//							}
//							System.out.println(pruneMaps.get(length).get(current).getIntArray()[0] - current.getIntArray()[0]);
							current = pruneMaps.get(length).get(current).clone();
							cube = scrambled.clone();
							scr = new Scramble(current.getIntArray(), length);
							cube.move(scr);
							if(current.atMax(length)) {finishedLength = true;}
						}
						else {
							cube.move(scr.iterate());
							if(current.iterate(length)) {finishedLength = true;}
						}
//						if(length == 6) {
//							System.out.println(-2133814898 - current.getIntArray()[0]);
//						}
					}
					pruneMaps.get(length).clear();
					length ++;
					System.out.println("Current depth: " + length);
					int[] intScr = new int[(length - 1) / 8 + 1];
					for(int i = 0; i < intScr.length; i ++) {intScr[i] = Integer.MIN_VALUE;}
					current = new IntScramble(intScr);
					scr = new Scramble(Integer.MIN_VALUE, length);
					cube = new Cube(identityScr);
				}
			}
		}
		System.out.println();
		System.out.println("Scramble(s) solved! : ");
		for(int m = 0; m < solvedScrambles.size(); m ++) {System.out.println("Scramble " + (m + 1) + ": " + solvedScrambles.get(m));}
		scan.close();
	}
	public static short getPruningDepth(Cube cube) {
		short depth = 0;
		for(int i = 0; i < rawTables.size(); i ++) {
			short current = rawTables.get(i).get(getCoord(cube, readRawTables.get(i)));
			if(current > depth) {depth = current;}
		}
		for(int j = 0; j < symTables.size(); j ++) {
			short current = symTables.get(j).get(getSymCoord(cube, readRawTables.get(j), rtsTables.get(j)));
			if(current > depth) {depth = current;}
		}
		return depth;
	}
	public static int getCoord(Cube cube, int type) {
		if(type == 0) {return cube.coToInt();}
		else if(type == 1) {return cube.cpToInt();}
		else if(type == 2) {return cube.eoToInt();}
		else if(type == 3) {return cube.epToInt();}
		else if(type == 4) {return cube.rcoToInt();}
		else {return cube.reoToInt();}
	}
	/* code for this could be potentially optimized when considering the size of the
	 * RTS Table, if it is better to constantly check or made identity coord then find
	 * where it contains.*/
	public static int getSymCoord(Cube cube, int type, ArrayList<Integer> rawToSymTable) {
		Cube testerCube = cube.clone();
		int sym = getCoord(testerCube, type);
		for(int i = 1; i < 48; i ++) {
			rotateCoord(testerCube, type, i);
			int coord = getCoord(testerCube, type);
			if(coord < sym) {
				sym = coord;
			}
			testerCube = cube.clone();
		}
		return rawToSymTable.indexOf(sym);
	}
	public static void rotateCoord(Cube cube, int type, int rotation) {
		if(type == 0) {cube.rotateCO(rotation);}
		else if(type == 1) {cube.rotateCP(rotation);}
		else if(type == 2) {cube.rotateEO(rotation);}
		else if(type == 3) {cube.rotateEP(rotation);}
		else if(type == 4) {cube.rotateCO(rotation); cube.rotateCP(rotation);}
		else {cube.rotateEO(rotation); cube.rotateEP(rotation);}
	}
}