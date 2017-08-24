
public class SingleLinkedList {
private Node head;
public SingleLinkedList() { this.head = new Node("head"); } 

private static class Node<T> {
    private T data;
    private Node next;
    public Node(T data) {
    	this.data = data;
    }
    public void setData(T data) {
    	this.data=data;
    }
    public T getdata() {
    	return this.data;
    }
    public void setNext(Node n) {
    	this.next= n;
    }
    public Node next() {
    	return this.next;
    }
    
    @Override
    public String toString() {
    	return data.toString();
    }
}


public boolean isEmpty() {
	return length()==0;
}

public boolean isCyclic() {
	Node fast=head;
	Node slow=head;
	
	while(fast!=null && fast.next!=null) {
		fast=fast.next.next;
		slow=slow.next;
		if(fast==slow) {
			return true;
		}
	}
	return false;
}

public <T> T startofloop() {
	Node fast=head;
	Node slow=head;
	
	while(fast!=null && fast.next!=null) {
		fast=fast.next.next;
		T temp = (T) slow.data;
		slow=slow.next;
		
		if(fast==slow) {
			return (T) temp;
		}
	}
	return null;
	
}

private int length() {
	// TODO Auto-generated method stub
	int length=0;
	Node current = head;
	while(current != null) {
		length++;
		current=current.next;
	}
	return length;
}
/*
public <T> void append(T data) {
	if(head==null) {
		head= new Node(data);
		return;
	}
	tail().next= new Node(data);*/
public void append(Node node) {
	Node current=head;
	while(current.next() !=null) {
		current=current.next();
	}
	current.setNext(node);
}


/*private Node tail() {
	Node tail=head;
    while(tail.next !=null) {
    	tail=tail.next;
    }
    return tail;
}*/

@Override
public String toString() {
	StringBuilder sb= new StringBuilder();
	Node current =head;
	while(current != null) {
		sb.append(current.toString()+"-->");
		current=current.next;
	}
	sb.delete(sb.length()-3, sb.length());
	return sb.toString();
}

public static <T> void main(String args[]) {
	SingleLinkedList a= new SingleLinkedList();
	System.out.println(a.isEmpty());
	a.append(new SingleLinkedList.Node("hello"));
	a.append(new SingleLinkedList.Node("Ashmita"));
	System.out.println(a.isEmpty());
	System.out.println(a.length());
	//System.out.println(a.tail());
	System.out.println(a.toString());
	System.out.println(a.head.data);
	System.out.println(a.isCyclic());
	SingleLinkedList.Node<T> node1 = new SingleLinkedList.Node("1");
	a.append(node1);
	System.out.println(a.toString());
	System.out.println(a.isCyclic());
	
	a.append(new SingleLinkedList.Node("start1"));
	a.append(new SingleLinkedList.Node("start2"));
	a.append(new SingleLinkedList.Node("start3"));
	a.append(node1);
	//System.out.println(a.toString());
	System.out.println(a.isCyclic());
	if(a.isCyclic()) {
		System.out.println("The start of loop is :"+a.startofloop());
	}
}
}

