/* this is basically just a wrapper class for int[], which
 * the compressed Scrambles are implemented as. the problem was
 * in the direct implementation of hashcodes for int[]. when using
 * .contains on a hashmap, it first generates a hashcode and sees if
 * something is there, then it sees if that object is identical using
 * the .equals of that object. however, both .hashcode and .equals of
 * int[] rely on its location in memory, which won't be usable when trying
 * to access that value using a different but identical int[]. */
import java.util.Arrays;
public class IntScramble {
	//Places for this to be implemented:
	//solver, with implementation of jumping, and .iterate
	//solver, hashmaps of IntScrambles
	int[] intScr;
	public IntScramble(int[] intScr) {this.intScr = intScr;}
	@Override
	public boolean equals(Object other) {
		return Arrays.equals(intScr, ((IntScramble) other).getIntArray());
	}
	//may be a poor choice of hashCode; i was never educated in the subject.
	@Override
	public int hashCode() {
		int rtrn = 0;
		for(int i : intScr) {rtrn += i;}
		return rtrn;
	}
	
	public int[] getIntArray() {return intScr;}
	/* this class will also contain a .iterate method because the same 
	 * code appears in a lot of places. it 
	 * length of the scramble must be provided and it also returns if the
	 * length has been finished. finishedLength is exclusively defined.*/
	public boolean iterate(int length) {
		boolean finishedLength = false;
		int index = intScr.length - 1;
		int indexMax;
		if(length < 9) {indexMax = (int) ((long) Integer.MIN_VALUE + 18 * (long) Math.pow(15, length - 1)) - 1;}
		else {indexMax = (int)(3075468749L + (long) Integer.MIN_VALUE);}
		while(index != -1 && intScr[index] == indexMax) {
			index --;
			if(index == 0) {indexMax = (int)(3075468749L + (long) Integer.MIN_VALUE);}
			else {indexMax =(int)(2562890624L + (long) Integer.MIN_VALUE);}
			if(index == -1) {finishedLength = true; index = 0;}
			else {intScr[index + 1] = Integer.MIN_VALUE;}
		}
		if(index != 0) {
			int size;
			if(index == 1 && intScr.length == 3) {size = 8;}
			else {size = length % 8;}
			int b = intScr[index];
			int count = 0;
			while(b % 15 == 14 && count < size - 1) {
				b /= 15;
				count ++;
			}
			if(count == size - 1) {
				/* this will only occurs when iteration will change the first move.
				 * in this case iteration may be incorrect because it might involve a
				 * 18 - 15 conversion not seen by the local 8 move scramble. */
				//THIS IS THE EASY WAY OUT -- I'M SURE IT CAN BE OPTIMIZED IF I WAS SMARTER
				Scramble scr = new Scramble(intScr, length);
				scr.iterate();
				intScr = scr.toInt();
			}
			else {intScr[index] ++;}
		}
		else {intScr[index] ++;}
		return finishedLength;
	}
	/* does the same thing as iterate(), but doesn't change any values, just seeing if it is at max
	 * exclusively defined -- returns true at the first impossible scramble*/
	public boolean atMax(int length) {
		boolean finishedLength = false;
		int index = intScr.length - 1;
		int expectedValue;
		if(length < 9) {expectedValue = (int) ((long) Integer.MIN_VALUE + 18 * (long) Math.pow(15, length - 1));}
		else {expectedValue = Integer.MIN_VALUE;}
		while(index != -1 && intScr[index] == expectedValue) {
			index --;
			if(index == 0) {expectedValue = (int)(3075468749L + (long) Integer.MIN_VALUE);}
			else {expectedValue =Integer.MIN_VALUE;}
			if(index == -1) {finishedLength = true; index = 0;}
		}
		return finishedLength;
	}
	
	public IntScramble clone() {return new IntScramble(intScr.clone());}
}
