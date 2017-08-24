
public class productofallintexceptindex {
public static void main(String args[]) throws Exception {
	int[] array = new int[] {2,1,5,8,10};
	int[] products = new int[array.length];
	if(array.length <2) {
		throw new Exception("Insufficient Data");
	}
	int producttillnow=1;
	for(int i=0;i<array.length;i++) {
		products[i]=producttillnow;
		producttillnow *= array[i];
	}
	producttillnow=1;
	for(int i=array.length-1;i>=0;i--) {
		products[i] *= producttillnow;
		producttillnow *= array[i];
	}
	for(int i=0;i<array.length;i++) {
		System.out.print(products[i]+",");
		
	}
	
}
}
