/*
 * class which stores a scramble object. a scramble is stored as an LinkedList of ints
 * 0-17, where each move is represented its index in the below list:
 * F F2 F' U U2 U' R R2 R' B B2 B' D D2 D' L L2 L'
 */
import java.util.*;
public class Scramble {
	public LinkedList<Integer> scramble;
	public Scramble (String strScramble) {
		scramble = new LinkedList<Integer>();
		fromString(strScramble);
	}
	public Scramble (int intScramble, int length) {
		scramble = new LinkedList<Integer>();
		fromInt(intScramble, length);
	}
	public Scramble(int[] intScramble, int length) {
		scramble = new LinkedList<Integer>();
		fromInt(intScramble, length);
	}
	public Scramble(int length) {
		scramble = new LinkedList<Integer>();
		boolean zeroOrThree = true;
		for(int i = 0; i < length; i ++) {
			if(zeroOrThree) {scramble.add(0);}
			else {scramble.add(3);}
			zeroOrThree = !zeroOrThree;
		}
	}
	public Scramble() {scramble = new LinkedList<Integer>();}
	public LinkedList<Integer> getScramble() {return scramble;}
	//converts the string provided by the constructor to declare the scramble instance variable
	public void fromString (String strScramble) {
		String[] strScr = strScramble.split(" ");
		for(String move : strScr) {scramble.addLast(convertMove(move));}
	}
	// a helper method of convertString, may be optimizable
	public int convertMove (String move) {
		int rtnInt = 3 * "FURBDL".indexOf(move.charAt(0));
		if(move.length() == 1) {return rtnInt;}
		else if(move.charAt(1) == '2') {return rtnInt + 1;}
		else {return rtnInt + 2;}
	}
	/* a scramble can also be represented as a single integer, so as methods exist for converting
	 * between this integer and the list interpretation of a scramble. the int representation is made
	 * by interpreting the list scramble as a number where the first digit is base 18 and all subsequent
	 * digits are base 15. when the scramble is > 8 in size, it can no longer fit in a single java int,
	 * which is 2 ^ 32 in size. then it is represented as an int[], with each int representing up to 8 moves
	 * to double the size one int can store, we add Integer.MIN_VALUE, giving us access to the negative numbers
	 * this code may want to be updated in the future to use IntScramble objects, but it's fine for now.*/
	public void fromInt (int[] intScramble, int length) {
		for(int i = 0; i < intScramble.length; i ++) {
			Scramble part = new Scramble();
			int partLength = length;
			if(partLength > 8) {partLength = 8;}
			part.fromInt(intScramble[i], partLength);
			ListIterator<Integer> partIter = part.getIterator();
			while(partIter.hasNext()) {scramble.addLast(partIter.next());}
			length -= 8;
		}
	}
	/* helper method for fromInt which only deals with scrambles < 8 in size, so only
	 * int intScrambles. since the method returns void it must be used with a new Scramble
	 * object and .getScramble()*/
	public void fromInt(int intScramble, int length) {
		long longScramble = (long) intScramble + (long) Integer.MAX_VALUE + 1L;
		LinkedList<Integer> toList = new LinkedList<Integer>();
		for(int i = 0; i < length - 1; i++) {
			toList.addFirst((int) (longScramble % 15));
			longScramble /= 15;
		}
		toList.addFirst((int) longScramble);
		ListIterator<Integer> toListIterator = toList.listIterator();
		scramble.add(toListIterator.next());
		while(toListIterator.hasNext()) {
			int next = toListIterator.next();
			if(next / 3 < scramble.getLast() / 3) {
				scramble.addLast(next);
			}
			else {
				scramble.addLast(next + 3);
			}
		}
	}
	/* DEPRICATED */
	public void fromFifteenInt(int intScramble, int length, int prev) {
		long longScramble = (long) intScramble + (long) Integer.MAX_VALUE + 1L;
		LinkedList<Integer> toList = new LinkedList<Integer>();
		for(int i = 0; i < length - 1; i++) {
			toList.addFirst((int) (longScramble % 15));
			longScramble /= 15;
		}
		if(prev / 3 < (int) longScramble / 3) {toList.addFirst((int) longScramble - 3);} 
		else {toList.addFirst((int) longScramble);}
		ListIterator<Integer> toListIterator = toList.listIterator();
		scramble.add(toListIterator.next());
		while(toListIterator.hasNext()) {
			int next = toListIterator.next();
			if(next / 3 < scramble.getLast() / 3) {
				scramble.addLast(next);
			}
			else {
				scramble.addLast(next + 3);
			}
		}
	}
	//@Override
	public String toString() {
		String rtrnStr = "";
		ListIterator<Integer> iter = scramble.listIterator();
		while(iter.hasNext()) {
			rtrnStr += toMove(iter.next());
			rtrnStr += " ";
		}
		rtrnStr = rtrnStr.substring(0, rtrnStr.length() - 1);
		return rtrnStr;
	}
	//make better with direct comparison of LinkedLists. 
	public boolean equals(String other) {
		if(other.toString().equals(toString())) {
			return true;
		}
		return false;
	}
	/* a helper method for toString, it may be basically the inverse of convertMove
	 * using charAt and casting to string may be suboptimal */
	public String toMove(int move) {
		if(move % 3 == 0) {return Character.toString("FURBDL".charAt(move / 3));}
		else if(move % 3 == 1) {return Character.toString("FURBDL".charAt(move / 3)) + "2";}
		else {return Character.toString("FURBDL".charAt(move / 3)) + "'";}
	}
	public void addFirst(int move) {scramble.addFirst(move);}
	public void addLast(int move) {scramble.addLast(move);}
	//returns the list iterator of the linked list
	public ListIterator<Integer> getIterator() {return scramble.listIterator();}
	public ListIterator<Integer> getIterator(int index) {return scramble.listIterator(index);}
	/* this is effectively the backwards of fromInt(), in meaning and code*/
	public int[] toInt() {
		int[] rtrn;
		int rtrnSize = ((getLength() - 1) / 8) + 1;
		rtrn = new int[rtrnSize];
		ListIterator<Integer> iter = getIterator();
		int count = 0;
		Scramble subScram = new Scramble();
		for(int i = 0; i < rtrnSize; i ++) {
			while(count < 8 && iter.hasNext()) {
				subScram.addLast(iter.next());
				count ++;
			}
			rtrn[i] = subScram.toSingleInt();
			subScram = new Scramble();
			count = 0;
		}
		return rtrn;
	}
	/* a helper method for toInt which only deals with scrambles < 8 in size. 
	 * a new Scramble object must be created which contains only the < 8 moves
	 * to use this. DEFUNCT -- NO LONGER IN USE, but it's public so it can still
	 * be directly called*/
	public int toSingleInt() {
		int rtrnInt = 0;
		LinkedList<Integer> toList = new LinkedList<Integer>();
		ListIterator<Integer> iter = scramble.listIterator();
		int prev = iter.next();
		toList.add(prev);
		while(iter.hasNext()) {
			int next = iter.next();
			if(prev / 3 <= next / 3) { // / 3 might be unnecessary
				toList.addLast(next - 3);
			}
			else {
				toList.addLast(next);
			}
			prev = next;
		}
		ListIterator<Integer> toListIterator = toList.listIterator();
		rtrnInt += toListIterator.next();
		if(toListIterator.hasNext()) {
			rtrnInt *= 15;
			rtrnInt += toListIterator.next();
		}
		while(toListIterator.hasNext()) {
			rtrnInt *= 15;
			rtrnInt += toListIterator.next();
		}
		return rtrnInt + Integer.MIN_VALUE;
	}
	/* this method iterates the Scramble according to the rules of toInt
	 * and fromInt, and also returns a Scramble which would transform a Cube
	 * from the previous Scramble to the current. this method will be used in
	 * iterating through possible scrambles in the solver. 
	 * this might not work over changes in Scramble size. */
	public Scramble iterate() {
		Scramble rtrnScramble = new Scramble();
		ListIterator<Integer> iter = scramble.listIterator(scramble.size());
		int current = iter.previous();
		boolean toNext = false;
		int prev = 18;
		while(!toNext) {
			if(iter.hasPrevious()) {
				prev = iter.previous();
				if((prev < 17 && current == 17) || (prev > 14 && current == 14)) {
					rtrnScramble.addLast(moveInverse(current));
					current = prev;
				}
				else {toNext = true; iter.next();}
			}
			else {toNext = true;}
		}
		boolean hasPrev = iter.hasPrevious();
		iter.next();
		int newCurrent;
		if((current + 1) / 3 == prev / 3 && hasPrev) {newCurrent = current + 4;}
		else {newCurrent = current + 1;}
		iter.set(newCurrent);
		if(current / 3 == newCurrent / 3) {
			rtrnScramble.addLast((current / 3) * 3);
		}
		else {
			rtrnScramble.addLast(moveInverse(current));
			rtrnScramble.addLast(newCurrent);
		}
		prev = newCurrent;
		while(iter.hasNext()) {
			iter.next();
			if(prev < 3) {
				iter.set(3);
				rtrnScramble.addLast(3);
				current = 3;
			}
			else {
				iter.set(0);
				rtrnScramble.addLast(0);
				current = 0;
			}
			prev = current;
		}
		return rtrnScramble;
	}
	//passed a single base 18 move, it returns its int inverse
	public int moveInverse(int move) {
		int type = move / 3;
		int dir = move % 3;
		return type * 3 + 2 - dir;
	}
	//returns the Scramble inverse of the Scramble
	public Scramble inverse() {
		Scramble inv = new Scramble();
		ListIterator<Integer> iter = getIterator();
		while(iter.hasNext()) {
			inv.addFirst(moveInverse(iter.next()));
		}
		return inv;
	}
	public int getLength() {return scramble.size();}
	/* changes the Scramble by a rotation / symmetry. a rotation is saved as
	 * an int 0-47. 6 possible rotations to place the correct U face, 4 to place
	 * the F face, doubled for mirror along R axis.*/
	public void rotate(int rotation) {
		
	}
	/* the following code is used in the solver class to return the toInt
	 * value of the smallest and largest and is very inelligant*/
}