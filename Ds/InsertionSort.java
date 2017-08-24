import java.util.Arrays;

public class InsertionSort {

	public static void main(String args[]) {
		int[] a =new int[] {6,3,4,1,2};
		int[] sortedA =insertionSort(a);
		for(int i = 0;i<sortedA.length;i++) {
			System.out.println(sortedA[i]);
		}
	}

	private static int[] insertionSort(int[] a) {
		for (int i=0;i<a.length;i++) {
			int numtocompare = a[i];
			int index=i;
			while(index>0 && a[index-1]>numtocompare) {
				a[index]=a[index-1];
				index--;
			}
			a[index]=numtocompare;
		}
		return a;
	}
}
