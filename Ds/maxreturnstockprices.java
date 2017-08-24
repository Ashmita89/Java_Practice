
public class maxreturnstockprices {
public static void main(String args[]) throws Exception {
	int [] stock_prices_yesterday = new int[] {10, 7, 20, 8, 11, 9,19,39};
	int profit =get_max_profit(stock_prices_yesterday);
	System.out.println("Profit:"+profit);
}

private static int get_max_profit(int[] prices) throws Exception {
	if(prices.length < 2) {
		throw new Exception("Insufficient Data");
	}
	int min_price= prices[0];
	int max_profit= prices[1]-prices[0];
	for(int i=1;i< prices.length;i++) {
		int curr_price= prices[i];		
		max_profit = Math.max(max_profit, curr_price -min_price);
		min_price = Math.min(curr_price,min_price);		
	}
	return max_profit;
}
}
