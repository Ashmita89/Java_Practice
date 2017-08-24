import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */

/**
 * @author Ashmita
 *
 */
public class pffvsws {


	/**
	 * @param args
	 * @throws IOException
	 */
	static Map pffpageMapTable;
	static Map vswsPageMapTable;
	static int noOfPPages;
	static int noOfVSWSPages;
	static int pffInterPageFaultCounter;
	static int vswsInterSIPageFaultCounter;
	static int vswsSamplingIntervalCounter;
	static int pff_F = 7;
	static int vsws_M=60;
	static int vsws_L=80;
	static int vsws_Q=70;
	static String vswsSamplingIntervalString = new String();
	static String vswsInterSIPageFaultString = new String();
	static String pffWorkingSetString = new String();
	static String vswsWorkingSetString = new String();
	static int[] PFF_FArray ;
	static String maxPFFMemUsageString = new String();
	static String numOfPageFaultsString_PFF = new String();
	static void intializePFFPageMap(){
		pffpageMapTable = new HashMap();
		int index;
		for(index=1;index<=noOfPPages;index++){
			pffpageMapTable.put(index, "");
		}
	}
	static void intializeVSWSPageMap(){
		vswsPageMapTable = new HashMap();
		int index;
		for(index=1;index<=noOfVSWSPages;index++){
			vswsPageMapTable.put(index, "");
		}
	}
	static void pffWorkingSetUpdater(){
		int index;
		pffWorkingSetString=""; 
		for (index=1;index<=noOfPPages;index++){
			if(pffpageMapTable.get(index).toString().equals("1")||pffpageMapTable.get(index).toString().equals("0")){
				if(pffWorkingSetString.isEmpty()){
					pffWorkingSetString=Integer.toString(index);
				}
				else
				{
					pffWorkingSetString=pffWorkingSetString+","+Integer.toString(index); 
				}
			}
		}

	}
	static void vswsWorkingSetUpdater(){
		int index;
		vswsWorkingSetString=""; 
		for (index=1;index<=noOfVSWSPages;index++){
			if(vswsPageMapTable.get(index).toString().equals("1")||vswsPageMapTable.get(index).toString().equals("0")){
				if(vswsWorkingSetString.isEmpty()){
					vswsWorkingSetString=Integer.toString(index);
				}
				else
				{
					vswsWorkingSetString=vswsWorkingSetString+","+Integer.toString(index); 
				}
			}
		}

	}

	/*
	 * when a page reference is requested,then current page and pageFaultOccured or not is supplied .
	 * -case1:Page reference is in working set ,then no change needed to working set .
	 *        Used bit of the current page has to be updated.
	 *        As it is a no page fault case,then only interpagefaultcounter is only updated.
	 * -case2:Page reference is not in working set,then First we need to report that a page fault has occured
	 *        and update the numOfFaults.
	 *        After that compare the interpagefaultcounter with the Threshold F,
	 *        Based on the above comparison :
	 *        Case a.interpagefaultcounter is less than Threshold :
	 *               Reset the pffInterPageFaultCounter as it is has to again record the difference between successive PageFaults
	 *               Used bit of all the bit in current working set is set to 0.
	 *               Used bit of the current page has to be updated to 1.
	 *               pffWorkingSetString must be modified with addition of the CurrentPage to it .
	 *               The length of the new working set needs to be calculated for the total number of Frames used .
	 *        Case b.interpagefaultcounter is greater than Threshold     
	 *               Reset the pffInterPageFaultCounter as it is has to again record the difference between successive PageFaults
	 *               Used bit of all the bit in current working set is checked :
	 *               -all used bit 1 are changed to 0
	 *               -all used bit 0 are removed by marking ""
	 *               Used bit of the current page has to be updated to 1.
	 *               pffWorkingSetString must be reset based on examining the used bit in the PageMapTable
	 *               The length of the new working set needs to be calculated for the total number of Frames used .
	 *               
	 *               
	 */
	static void pffPageMapTableCPUpdater(String currentPage,Boolean pageFaultOccurred){
		int index;
		if(pageFaultOccurred){
			if(pffInterPageFaultCounter <  pff_F){
				pffInterPageFaultCounter=0;
				for (index=1;index<=noOfPPages;index++){
					if(pffpageMapTable.get(index).toString().equals("1")){
						pffpageMapTable.replace(index, pffpageMapTable.get(index), "0");
					}
				}
				for (index=1;index<=noOfPPages;index++){
					if(Integer.parseInt(currentPage)== index){
						pffpageMapTable.replace(index, pffpageMapTable.get(index), "1");
						break;
					}
				}
				pffWorkingSetUpdater();
			}
			else{
				pffInterPageFaultCounter=0;
				for (index=1;index<=noOfPPages;index++){
					if(pffpageMapTable.get(index).toString().equals("0")){
						pffpageMapTable.replace(index, pffpageMapTable.get(index), "");
					}
					else if(pffpageMapTable.get(index).toString().equals("1")){
						pffpageMapTable.replace(index, pffpageMapTable.get(index), "0");
					}
				}
				for (index=1;index<=noOfPPages;index++){
					if(Integer.parseInt(currentPage)== index){
						pffpageMapTable.replace(index, pffpageMapTable.get(index), "1");
						break;
					}
				}
				pffWorkingSetUpdater();
			}

		}else{
			for (index=1;index<=noOfPPages;index++){
				if(Integer.parseInt(currentPage)== index){
					if(pffpageMapTable.get(index).toString().equals("1")){
						pffInterPageFaultCounter=pffInterPageFaultCounter+1;
					}
					else{
						pffpageMapTable.replace(index, pffpageMapTable.get(index), "1");
						pffInterPageFaultCounter=pffInterPageFaultCounter+1;
					}
				}
			}

		}
	}
	private static void vswsPageMapTableCPUpdater(String currentPage, Boolean pageFaultIndicator ) {
		int index;
		if(pageFaultIndicator){
			//Page Fault detected	
			vswsInterSIPageFaultCounter=vswsInterSIPageFaultCounter+1;
		}
		if(vswsSamplingIntervalCounter < vsws_L){
			//L not reached
			if(vswsInterSIPageFaultCounter < vsws_Q){
				//Q not reached
				for (index=1;index<=noOfVSWSPages;index++){
					if(Integer.parseInt(currentPage)== index){
						vswsPageMapTable.replace(index, vswsPageMapTable.get(index), "1");
						break;
					}
				}
				vswsWorkingSetUpdater();
				vswsSamplingIntervalCounter=vswsSamplingIntervalCounter+1;
			}
			else
			{
				//Q reached
				if(vswsSamplingIntervalCounter < vsws_M){
					//M not reached
					for (index=1;index<=noOfVSWSPages;index++){
						if(Integer.parseInt(currentPage)== index){
							vswsPageMapTable.replace(index, vswsPageMapTable.get(index), "1");
							break;
						}
					}
					vswsSamplingIntervalCounter=vswsSamplingIntervalCounter+1;
					vswsWorkingSetUpdater();

				}
				else{
					//M reached
					for (index=1;index<=noOfVSWSPages;index++){
						if(vswsPageMapTable.get(index).toString().equals("0")){
							vswsPageMapTable.replace(index, vswsPageMapTable.get(index), "");
						}
						else if(vswsPageMapTable.get(index).toString().equals("1")){
							vswsPageMapTable.replace(index, vswsPageMapTable.get(index), "0");
						}
					}
					for (index=1;index<=noOfVSWSPages;index++){
						if(Integer.parseInt(currentPage)== index){
							vswsPageMapTable.replace(index, vswsPageMapTable.get(index), "1");
							break;
						}
					}
					vswsWorkingSetUpdater();
					//VSWS Sampler reset
					if(vswsInterSIPageFaultString.isEmpty()){
						vswsInterSIPageFaultString=Integer.toString(vswsInterSIPageFaultCounter);   
					}
					else{
						vswsInterSIPageFaultString=vswsInterSIPageFaultString+","+Integer.toString(vswsInterSIPageFaultCounter);
					}
					if(vswsSamplingIntervalString.isEmpty()){
						vswsSamplingIntervalString=Integer.toString(vswsSamplingIntervalCounter);   
					}
					else{
						vswsSamplingIntervalString=vswsSamplingIntervalString+","+Integer.toString(vswsSamplingIntervalCounter);
					}
					vswsSamplingIntervalCounter=0;
					vswsInterSIPageFaultCounter=0;

				}
			}
		}
		else{
			//L reached
			for (index=1;index<=noOfVSWSPages;index++){
				if(vswsPageMapTable.get(index).toString().equals("0")){
					vswsPageMapTable.replace(index, vswsPageMapTable.get(index), "");
				}
				else if(vswsPageMapTable.get(index).toString().equals("1")){
					vswsPageMapTable.replace(index, vswsPageMapTable.get(index), "0");
				}
			}
			for (index=1;index<=noOfVSWSPages;index++){
				if(Integer.parseInt(currentPage)== index){
					vswsPageMapTable.replace(index, vswsPageMapTable.get(index), "1");
					break;
				}
			}
			vswsWorkingSetUpdater();
			//VSWS Sampler reset
			if(vswsInterSIPageFaultString.isEmpty()){
				vswsInterSIPageFaultString=Integer.toString(vswsInterSIPageFaultCounter);   
			}
			else{
				vswsInterSIPageFaultString=vswsInterSIPageFaultString+","+Integer.toString(vswsInterSIPageFaultCounter);
			}
			if(vswsSamplingIntervalString.isEmpty()){
				vswsSamplingIntervalString=Integer.toString(vswsSamplingIntervalCounter);   
			}
			else{
				vswsSamplingIntervalString=vswsSamplingIntervalString+","+Integer.toString(vswsSamplingIntervalCounter);
			}
			vswsSamplingIntervalCounter=0;
			vswsInterSIPageFaultCounter=0;

		}

	}

	public static void main(String[] args) throws IOException {
		int count;
		count = args.length;
		if (count < 0) {
			System.out.println("No dataset to process");
		} else {
			String file = args[0];

			FileInputStream fstream = new FileInputStream(file);
			// Get the object of DataInputStreamfile
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine,pageRefString= new String();
			Integer pages= new Integer(0); 
			// Read File Line By Line
			pages=Integer.parseInt(br.readLine());
			noOfPPages=pages;
			noOfVSWSPages=pages;
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				if(pageRefString.isEmpty()){
					pageRefString=strLine.toString();
				}
				else{
					pageRefString=pageRefString+","+strLine.toString();
				}
			}
			// Close the input stream
			in.close();
			System.out.println(pageRefString);
			String[] pageRefArray=null;
			if(pageRefString.contains(",")){
				pageRefArray=pageRefString.split(",");
			}
			System.out.println("*****Page Reference Array Read*****");
			/***********************************************************************************************************************
			 * Implementing PFF Algorithm:
			 * Based on 'The page fault frequency replacement algorithm' by  WESLEY W. CHU and HOLGER OPDERBECK 
			 * and 'Program Behavior and the Page-Fault-Frequency Replacement Algorithm' by WESLEY W. CHU and HOLGER OPDERBECK ,
			 * Following defines the Page Fault Frequency Algorithm 
			 * 
			 * -The algorithm requires a use bit to be associated with each page in memory.
			 * -Each activated process has its own USE-BIT table to record the used page of that process during an interpage fault time
			 * -The bit is set to 1 when that page is accessed.
			 * -When a page fault occurs, the operating system notes the virtual time since the last page fault for that process;
			 * this could be done by maintaining a counter of page references.A threshold F is defined.
			 * -If the amount of time since the last page fault is less than F,then a page is added to the resident set of the process.
			 * Otherwise,discard all pages with a use bit of zero,and shrink the resident set accordingly.
			 * -At the same time,reset the use bit on the remaining pages of the process to zero 
			 * -However, if there are no page frames available, then the supervisor according to the de-activation rule determines 
			 *  which process to deactivate. Again, by examining the USE-BITS of all the active processes as well as the page status list, 
			 *  the program determines the set of pages of the deactivated process that can be released from the main memory and
			 *  then resets the USE-BITS of that process. 
			 *************************************************************************************************************************/
			int index,pageRefArrLength,numOfPageFault=0,lengthOfWorkingSet = 0;
			String lowsArrayString = new String();
			String currentPage;
			String[] workingSet; 
			/**
			 * Finding reasonable F.
			 * PFF algorithm is run for a set of F parameter which are based on the length of pages in the process .
			 * At the end of the PFF algorithm for each of F parameters ,the peak of the memory usage is noted and
			 * this peak should not reach the total number of pages in the process.
			 * Reasonable F is the one which has less total number of Page Faults and memory peak usage is also 
			 * better than other cases.
			 */
			//initialize PFF_F
			initializePFF_F();
			int count1;
			int lengthc=PFF_FArray.length;
			for (count1=0;count1<lengthc;count1++){
				pff_F=PFF_FArray[count1];
				pageRefArrLength=pageRefArray.length;
				intializePFFPageMap();
				numOfPageFault=0;
				lengthOfWorkingSet=0;
				pffWorkingSetString="";
				lowsArrayString="";
				for(index=0;index<pageRefArrLength;index++){
					//Consider the main memory frames allocated are free.
					currentPage=pageRefArray[index];

					if(pffWorkingSetString.isEmpty()){
						pffWorkingSetString=currentPage;
						numOfPageFault=1;
						lengthOfWorkingSet=1;
						lowsArrayString=Integer.toString(lengthOfWorkingSet);
						pffPageMapTableCPUpdater(currentPage,Boolean.TRUE);
					}
					else
					{
						if(pffWorkingSetString.contains(",")){
							workingSet=pffWorkingSetString.split(",");
							int wscount, wSLength=workingSet.length;
							boolean flag=Boolean.FALSE;
							for(wscount=0;wscount<wSLength;wscount++){
								if(currentPage.equals(workingSet[wscount])){
									/*continue with the flow*/
									flag=Boolean.TRUE; 
									break; 
								}
								else{
									/*pagefault detected*/
									flag=Boolean.FALSE; 
								}
							}
							if(flag){
								pffPageMapTableCPUpdater(currentPage,Boolean.FALSE); 
							}
							else{
								numOfPageFault=numOfPageFault+1;
								pffPageMapTableCPUpdater(currentPage,Boolean.TRUE);
							}

						}
						else{
							if(pffWorkingSetString.equals(currentPage)){
								/*continue with the flow*/
								pffPageMapTableCPUpdater(currentPage,Boolean.FALSE);  
								lengthOfWorkingSet=1;
								if(lowsArrayString.isEmpty()){
									lowsArrayString=Integer.toString(lengthOfWorkingSet);   
								}
								else{
									lowsArrayString=lowsArrayString+","+Integer.toString(lengthOfWorkingSet);
								}
							}
							else{
								/*pagefault detected*/
								numOfPageFault=numOfPageFault+1;
								pffPageMapTableCPUpdater(currentPage,Boolean.TRUE);
							} 
						}
					}
					
					if(pffWorkingSetString.contains(",")){
						workingSet=pffWorkingSetString.split(",");
						lengthOfWorkingSet=workingSet.length;
						if(lowsArrayString.isEmpty()){
							lowsArrayString=Integer.toString(lengthOfWorkingSet);   
						}
						else{
							lowsArrayString=lowsArrayString+","+Integer.toString(lengthOfWorkingSet);
						}
					}
				}
				System.out.println("PFF Length of Working Sets:"+lowsArrayString);
				System.out.println("PFF Number of Page Faults:"+numOfPageFault);
				String[] workingSetArray=lowsArrayString.split(",");
				int countws;
				int maxMemUsage=Integer.parseInt(workingSetArray[0]);
				for(countws=0;countws<workingSetArray.length;countws++){
					if(Integer.parseInt(workingSetArray[countws]) > maxMemUsage){
						maxMemUsage = Integer.parseInt(workingSetArray[countws]);
					}
				}
				
				
				if(maxPFFMemUsageString.isEmpty()){
					maxPFFMemUsageString=Integer.toString(maxMemUsage);   
				}
				else{
					maxPFFMemUsageString=maxPFFMemUsageString+","+Integer.toString(maxMemUsage);
				}
				if(numOfPageFaultsString_PFF.isEmpty()){
					numOfPageFaultsString_PFF=Integer.toString(numOfPageFault);   
				}
				else{
					numOfPageFaultsString_PFF=numOfPageFaultsString_PFF+","+Integer.toString(numOfPageFault);
				}
				System.out.println("PFF Max Mem Usage:"+maxMemUsage);
				System.out.println("PFF Page Fault Rate:"+new Double(1/pff_F));
				System.out.println("PFF F Parameter"+pff_F);
			}
            /**
             * Based on observations for different process with different number of pages  and 
             * F affects the number of page faults and memory utilization .
             * If F is too high ,the memory utilization can reach max quickly 
             * and page faults are reduced very significantly as all the pages of process are in the memory.
             * If F is too low, the memory utilization may not reach peak but page faults spike up. 
             * For choice of reasonable value of F --At certain parameter F1, the number of Page Faults is 
             * entirely low but the memory utilization is peaked,and at certain parameter F2,
             *  the number of Page Faults is slightly higher but the memory utilization is quite reduced.
             *  We can determine the most reasonable value of F as a value in the range of F2<F<F1
             *  Based on present observations of random samples of input data tested 
             *  --considering reasonable F is between 0.2*(number of pages in the process) 
             *  to 0.3*(number of pages in the process)  
             */
			/**
			 * Implementing VSWS Algorithm:
			 * The VSWS policy is driven by three parameters:
			 *  M: The minimum duration of the sampling interval
			 *  L: The maximum duration of the sampling interval
			 *  Q: The number of page faults that are allowed to occur between sampling instances
			 *  1. If the virtual time since the last sampling instance reaches L,then suspend the process and scan the use bits.
			 *  2. If,prior to an elapsed virtual time of L,Qpage faults occur,
			 *   a. If the virtual time since the last sampling instance is less than M, 
			 *   then wait until the elapsed virtual time reaches M to suspend the process and scan the use bits.
			 *   b. If the virtual time since the last sampling instance is greater than or equal to M,
			 *   suspend the process and scan the use bits. 
			 */

			int vindex,vpageRefArrLength,vnumOfPageFaultTotal=0,vlengthOfWorkingSet = 0;
			String vlowsArrayString = new String();
			String vcurrentPage;
			String[] vworkingSet; 

			vpageRefArrLength=pageRefArray.length;
			intializeVSWSPageMap();
			/**
			 * For VSWS Algorithm,Based on observation using process of different process with different 
			 * number of pages ,it is found that M,L,Q parameter are very important in explaining the 
			 * efficiency of VSWS algorithm,
			 * 
			 * case a:if range between M and L is big and Q allows Page faults nearly equal to 25% of the range between M and L
			 * --page fault values are high ,memory utilization is considerably less than 80%
			 *  of the peak case (all pages are put in main memory)
			 * case b:if range between M and L is big and Q allows Page faults nearly equal to 50% of the range between M and L
			 * --page fault values are lowered significantly,memory utilization is increased than the case a
			 * case c:if range between M and L is less and M,L both are in range above 60% of no of pages and Q allows Page faults that lies between difference between M and L
			 * --page fault values are high ,memory utilization is considerably less than 80%
			 *  of the peak case (all pages are put in main memory)
			 * case d:if range between M and L is less and M,L both are in range above 50% of number of pages and Q allows Page faults above the difference between M and L
			 * --page fault values are lowered ,memory utilization is considerably around 85%
			 *  of the peak case (all pages are put in main memory)
			 * case e:if range between M and L is less and M,L both are in range below 20% of number of pages and Q allows Page faults above the difference between M and L
			 * --page fault values are very high,memory utilization is considerably less than 50%
			 *  of the peak case (all pages are put in main memory)
			 *  
			 *  Based on different cases ,a reasonable M,L,Q is the one in which memory utilization is less than maximum peak case
			 *  and page fault is considerable 
			 */
			initializeVSWS_MLQ();
			
			for(index=0;index<vpageRefArrLength;index++){
				//Consider the main memory frames allocated are free.
				vcurrentPage=pageRefArray[index];

				if(vswsWorkingSetString.isEmpty()){
					vswsWorkingSetString=vcurrentPage;
					vnumOfPageFaultTotal=1;
					vlengthOfWorkingSet=1;
					vlowsArrayString=Integer.toString(vlengthOfWorkingSet);
					vswsPageMapTableCPUpdater(vcurrentPage,Boolean.TRUE);
				}
				else
				{
					if(vswsWorkingSetString.contains(",")){
						vworkingSet=vswsWorkingSetString.split(",");
						int wscount, wSLength=vworkingSet.length;
						boolean flag=Boolean.FALSE;
						for(wscount=0;wscount<wSLength;wscount++){
							if(vcurrentPage.equals(vworkingSet[wscount])){
								/*continue with the flow*/
								flag=Boolean.TRUE; 
								break; 
							}
							else{
								/*pagefault detected*/
								flag=Boolean.FALSE; 
							}
						}
						if(flag){
							vswsPageMapTableCPUpdater(vcurrentPage,Boolean.FALSE); 
						}
						else{
							vnumOfPageFaultTotal=vnumOfPageFaultTotal+1;
							vswsPageMapTableCPUpdater(vcurrentPage,Boolean.TRUE);
						}

					}
					else{
						if(vswsWorkingSetString.equals(vcurrentPage)){
							/*continue with the flow*/
							vswsPageMapTableCPUpdater(vcurrentPage,Boolean.FALSE);  
							vlengthOfWorkingSet=1;
							if(vlowsArrayString.isEmpty()){
								vlowsArrayString=Integer.toString(vlengthOfWorkingSet);   
							}
							else{
								vlowsArrayString=vlowsArrayString+","+Integer.toString(vlengthOfWorkingSet);
							}
						}
						else{
							/*pagefault detected*/
							vnumOfPageFaultTotal=vnumOfPageFaultTotal+1;
							vswsPageMapTableCPUpdater(vcurrentPage,Boolean.TRUE);
						} 
					}
				}
				if(vswsWorkingSetString.contains(",")){
					vworkingSet=vswsWorkingSetString.split(",");
					vlengthOfWorkingSet=vworkingSet.length;
					if(vlowsArrayString.isEmpty()){
						vlowsArrayString=Integer.toString(vlengthOfWorkingSet);   
					}
					else{
						vlowsArrayString=vlowsArrayString+","+Integer.toString(vlengthOfWorkingSet);
					}
				}
			}
			System.out.println("************************************VSWS Algorithm***************************************");
			System.out.println("Length of Working Sets:"+vlowsArrayString);
			System.out.println("Number of Page Faults:"+vnumOfPageFaultTotal);
			System.out.println("Length of Sampling Sets:"+vswsSamplingIntervalString);
			System.out.println("Number of Page Fault Sampling:"+vswsInterSIPageFaultString);
			String[] vWorkingSetArray=vlowsArrayString.split(",");
			int vCountws;
			int vmaxMemUsage=Integer.parseInt(vWorkingSetArray[0]);
			for(vCountws=0;vCountws<vWorkingSetArray.length;vCountws++){
				if(Integer.parseInt(vWorkingSetArray[vCountws]) > vmaxMemUsage){
					vmaxMemUsage = Integer.parseInt(vWorkingSetArray[vCountws]);
				}
			}
			System.out.println("VSWS Max Mem Usage:"+vmaxMemUsage);
			System.out.println("VSWS M Parameter"+vsws_M);
			System.out.println("VSWS L Parameter"+vsws_L);
			System.out.println("VSWS Q Parameter"+vsws_Q);
			System.out.println("*************************************Performance Comparison*********************************");
			/*Taking into consideration that F value between (0.2 -0.3)*number of Pages in process gives a better performance for PFF algorithm
			 *However case to case basis there could be better performance observed when pff parameter is varied a little.
			 */
			//if memory utilization and number of page faults are used as performance parameters to evaluate performance of PFF algorithm and VSWS algorithm 
			String[] pPFFMaxMemoryutilizationArray=maxPFFMemUsageString.split(",");
			String[] pPFFNumOfPageFaultsArray=numOfPageFaultsString_PFF.split(",");
			int temp,pffBest=0,index1,pffBestIndex=0;
			for(index1=3;index1<6;index1++){
				if(Integer.parseInt(pPFFMaxMemoryutilizationArray[index1])< noOfPPages){
				pffBest=Integer.parseInt(pPFFMaxMemoryutilizationArray[index1])+Integer.parseInt(pPFFNumOfPageFaultsArray[index1]);
				pffBestIndex=index1;
				break;
				}
			}
			for(int index2=index1;index2<6;index2++){
			temp=Integer.parseInt(pPFFMaxMemoryutilizationArray[index2])+Integer.parseInt(pPFFNumOfPageFaultsArray[index2]);
				if((temp< pffBest)&& (Integer.parseInt(pPFFMaxMemoryutilizationArray[index2])< noOfPPages)){
					pffBest = temp;pffBestIndex=index2;
					
				}	
			}
			System.out.println("PFF Max memory utilization:"+pPFFMaxMemoryutilizationArray[pffBestIndex]);
			System.out.println("PFF Number of Page Faults:"+pPFFNumOfPageFaultsArray[pffBestIndex]);
			System.out.println("VSWS Max Mem Usage:"+vmaxMemUsage);
			System.out.println("VSWS Number of Page Faults:"+vnumOfPageFaultTotal);
			System.out.println("Based on the different cases tested,performance of VSWS is better than PFF algorithm");

		}

	}
	private static void initializePFF_F() {
		PFF_FArray = new int[6];
		PFF_FArray[0]=((Math.floorDiv(noOfPPages, 2))>0)?(Math.floorDiv(noOfPPages, 2)):1;//0.5 
		PFF_FArray[1]=((Math.floorDiv(2*noOfPPages, 5))>0)?(Math.floorDiv(2*noOfPPages, 5)):1;//0.4
		PFF_FArray[2]=((Math.floorDiv(3*noOfPPages, 10))>0)?(Math.floorDiv(3*noOfPPages, 10)):1;;//0.3
		PFF_FArray[3]=((Math.floorDiv(noOfPPages, 4))>0)?(Math.floorDiv(noOfPPages, 4)):1;//0.25
		PFF_FArray[4]=((Math.floorDiv(noOfPPages, 5))>0)?(Math.floorDiv(noOfPPages, 5)):1;//0.2
		PFF_FArray[5]=((Math.floorDiv(noOfPPages, 10))>0)?(Math.floorDiv(noOfPPages, 10)):1;;//0.1
	}
	private static void initializeVSWS_MLQ() {
		vsws_M=((Math.floorDiv(3*noOfVSWSPages, 10))>0)?(Math.floorDiv(3*noOfVSWSPages, 10)):1;;//0.3
		vsws_L=((Math.floorDiv(4*noOfVSWSPages, 5))>0)?(Math.floorDiv(4*noOfVSWSPages, 5)):1;;//0.8
		vsws_Q=((Math.floorDiv(6*noOfVSWSPages, 10))>0)?(Math.floorDiv(6*noOfVSWSPages, 10)):1;;//0.6
	}

}


