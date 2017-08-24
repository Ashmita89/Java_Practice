import java.util.Scanner;

public class ArmstrongNumber {

	public static void main(String args[]) {
		System.out.println("Please enter a 3 digit number to find if its an Armstrong number:"); 
		int number = new Scanner(System.in).nextInt();
		if(checkArmStrongNumber(number)) {
			System.out.println("The number is a armstrong number");
		}
		else {
			System.out.println("The number is not a armstrong number");
		}
	}

	private static boolean checkArmStrongNumber(int number) {
		int sum =0;
		int index=0,quotient=number;
		while( quotient > 0) {
		int remainder = quotient% 10;
		quotient = quotient /10; 
		sum = sum+remainder*remainder*remainder;
		}
		if(sum == number) {
			return true;
		}
		else {
		return false;
		}
	}
}
