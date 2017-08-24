import java.util.LinkedList;
import java.util.Stack;

public class Stack_Override {
Stack<Integer> st= new Stack<Integer>();
LinkedList<Integer> ll = new LinkedList<Integer>();
public void push(Integer item) {
	if(ll.isEmpty()) {
		ll.addFirst(item);
	}
	else {
		int currMax= (int) ll.peek();
		if(item >= currMax) {
			ll.addFirst(item); 			
		}
	}	
	st.push(item);
}
public Integer pop() throws Exception {
	Integer value=null;
	if(ll.isEmpty()) {
		value = st.pop();
		if(value == null) {
			throw new Exception("Stack is Empty");
		}
		return value;
	}
	else {
		int curr_max = ll.peek();
		int item = st.peek();
		if(curr_max == item) {
		   ll.removeFirst();	
		   value=st.pop();
		   return value;
		}
		else {
			value=st.pop();
			return value;
		}
	}
}

public Integer peek() {
	Integer value=null;
	return st.peek();
}

public Integer max() {
	return ll.peekFirst();
}

public static void main(String args[]) throws Exception {
Stack_Override stack = new Stack_Override();
//stack.pop();
stack.push(6);
System.out.println(stack.peek());
stack.push(8);
System.out.println(stack.max());
stack.push(7);
stack.max();
stack.push(8);
stack.pop();
stack.peek();
System.out.println(stack.max());
}
}
