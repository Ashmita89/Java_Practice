
public class FibonacciSeries {
	static int n1=0,n2=1,n3=0;
	public static void main(String args[]) {
		int limit= Integer.parseInt(args[0]);
		System.out.println("n is: "+limit);
		int index=0;
		int sum =0;
		System.out.print(n1+","+n2+",");
		for(index=0;index<limit;index++) {
			sum = n1+n2;
			n1=n2;
			n2=sum;
			System.out.print(sum+",");
		}
	}
}
