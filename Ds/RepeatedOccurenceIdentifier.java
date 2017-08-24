import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author ag508
 * Given a string "abcdaghj". Process this char by char and 
 * print a msg "already processed" 
 * when its not the first time you find this char.  
 *
 */
public class RepeatedOccurenceIdentifier {
public static void main(String args[]) {
	System.out.println("Please enter a the string"); 
	String input = new Scanner(System.in).next();
	ConcurrentHashMap<Character,Integer> map = new ConcurrentHashMap<Character,Integer>();
	for(int ch=0;ch<input.length();ch++) {
		if(map.isEmpty()) {
			map.put(input.charAt(ch), 1);
		}
		else {
			if(map.containsKey(input.charAt(ch))) {
				int value=map.get(input.charAt(ch));
				map.replace(input.charAt(ch), value+1);
				System.out.println("The element "+input.charAt(ch)+" has already been processed");
				break;
			}
			else {
				map.put(input.charAt(ch), 1);
			}
		}
	}
}
}
