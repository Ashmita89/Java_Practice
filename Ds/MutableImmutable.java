
public class MutableImmutable {
public static void main(String args[]) {
	Mutable m=new Mutable(5);
	System.out.println("Mutable value :"+m.getValue());
	m.setValue(6);
	System.out.println("Mutable value :"+m.getValue());
	Immutable im= new Immutable(5);
	System.out.println("Immutable value:"+im.getValue());
	}
}
class Mutable{
	  private int value;

	  public Mutable(int value) {
	     this.value = value;
	  }

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	  
	}

	class Immutable {
	  private final int value;

	  public Immutable(int value) {
	     this.value = value;
	  }

	public int getValue() {
		return value;
	}

	  
	}