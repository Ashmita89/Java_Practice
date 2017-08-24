/*
A time series is a series of data points indexed in time order. They are commonly used in the financial world, especially in stock markets.

In this challenge you are working with a time series of stock prices. You are given  historical records  where the stock at time  had a price . You have to answer  types of  queries of the form :

For type , given a value , when was the first time that the price of the stock was at least ?
For type , given a value , what's the maximum price of the stock at a time greater or equal to ?
If for any of these queries the answer is not defined, i.e. there are no historical records that match the query, the answer is .

Input Format

In the first line, there are two space-separated integers  and  denoting the number of historical records and the number of queries, respectively. 
The second line contains  space-separated integers denoting the time-stamps . 
The next line contains  space-separated integers denoting the price of stock , where  value corresponds to the  time-stamp. 
Next,  lines follow and each of them describes a single query. Each query is given as two space-separated integers. The first of them is either  or  and denotes the type of the query followed by a single integer  denoting the value to be queried.

Constraints

 for 
Output Format

For each of the  queries, print the answer on a new line. If the answer is not defined, print .

Sample Input 0

5 5
1 2 4 8 10
5 3 12 1 10
1 10
1 4
2 8
2 3
1 13
Sample Output 0

4
1
10
12
-1
Explanation 0

In the sample, there are  data records and  queries to answer. At time  the price was , at time  the price was , at time  the price was , at time  the price was , and finally, at time  the price was .

In the first query, we are asked for the minimum time at which the price was at least . The answer is  because at this time the price was  and there is no earlier time with a price at least .

In the second query, we are asked for the minimum time at which the price was at least . The answer is  because the price at this time was  which is greater than .

In the third query, we are asked for the maximum price at time  or greater. The answer is  because there are two data records with time at least  and the highest price among them is .

In the fourth query, we are asked for the maximum price at time  or greater. The answer here is .

In the last query, we are asked for the minimum time at which the price was at least . Since there is no data record with price  or greater, the answer is .

*/
import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

class Node{
    int time_key;
    int value;
    Node left;
    Node right;
    Node(int time_key,int value){
        this.time_key=time_key;
        this.value=value;
    }
}

class BST{
    Node root;
    BST(){
        root = null;
    }
    public void add(int time_key,int value){
         root= addNode(root,time_key,value);
    }
    public Node addNode(Node root,int time_key,int value){
        if(root == null){
            root= new Node(time_key,value);
            return root;
        }
        if (value < root.value){
            root.left = addNode(root.left,time_key,value);}
        else if (value > root.value){
            root.right = addNode(root.right,time_key,value);}
        
        return root;
        
    }
    public int search(int value){
        return searchNodes(root,value);
    }
    public int searchNodes(Node node, int value){
            // Base Cases: root is null 
            if (node==null)
                return -1;
            // val is greater than root's val   
             if(node.value>=value)
                return node.time_key;

            // val is less than root's key
            return searchNodes(node.right, value);
    }
}

public class TimeSeriesGS {
	 public static int findMax(int time_key,int n,int[] p,int[] t){
	        int i=0;
	        int max= Integer.MIN_VALUE;
	        SortedSet<Integer> timeSet = new TreeSet<>();
	        for(int in=0;in<n;in++) {
	        	timeSet.add(t[in]);
	        }
	        
	        if( time_key > timeSet.last()){
	            return -1;
	        }
	        
	        while(i<n){
	        	if(t[i]>time_key) {
	        		max= Math.max(max,p[i]);	        		 
	        	}
	        	i++; 
	        }
	        return max;
	    }
	    public static void main(String[] args) {
	        Scanner in = new Scanner(System.in);
	        int n = in.nextInt();
	        int q = in.nextInt();
	        int[] t = new int[n];
	        for(int t_i = 0; t_i < n; t_i++){
	            t[t_i] = in.nextInt();
	        }
	        int[] p = new int[n];
	        for(int p_i = 0; p_i < n; p_i++){
	            p[p_i] = in.nextInt();
	        }
	        BST bst= new BST();
	        for(int i = 0; i < n; i++){
	            bst.add(t[i],p[i]);
	        }
	        for(int a0 = 0; a0 < q; a0++){
	            int _type = in.nextInt();
	            int v = in.nextInt();
	             if(_type == 1){
	               System.out.println(bst.search(v)) ;
	            }
	            else if(_type == 2){
	               System.out.println(findMax(v,n,p,t)) ;
	            }
	        }
	        in.close();           
	        }

}
