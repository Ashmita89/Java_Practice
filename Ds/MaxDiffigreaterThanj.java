
public class MaxDiffigreaterThanj {
public static void main(String args[]) {
	int[] input=new int[]{6,0,4,3,2,1};
	int maxdiff=input[1]-input[0];
	int minI=0;
	for(int i=0;i<input.length;i++) {
		if(input[i]-input[minI]>maxdiff) {
			maxdiff=input[i]-input[minI];
		}
		if(input[i]<input[minI]) {
			minI=i;
		}
	}
	System.out.println("maxdiff"+maxdiff+"minI"+minI);
}
}
