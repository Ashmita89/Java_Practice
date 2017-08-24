/**
 * 
 */
package csefsck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Ashmita
 *
 */
class Tupletwo<S1, S2> {
	public String blockNumber;
	public String blockType;

	public Tupletwo(String blockNumber, String blockType) {
		this.blockNumber = blockNumber;
		this.blockType = blockType;
	}
}

public class csefsck {

	/**
	 * @param args
	 */
	private HashMap<String, String> superBlockMap;
	private HashMap<String, String> fileSystem;
	private HashMap<String, String> currentBlockContext;
	private Map<String, Tupletwo<String, String>> file_to_inode_dir;

	public csefsck() {
		superBlockMap = new HashMap<String, String>();
		fileSystem = new HashMap<String, String>();
		fileSystem.put("presentDirectory", "");
		fileSystem.put("lastAccessedDirectory", "");
		fileSystem.put("currentBlock", "");
		fileSystem.put("currentBlockType", "");
		fileSystem.put("usedFileList", "");
		currentBlockContext = new HashMap<String, String>();
		currentBlockContext.put("size", "");
		currentBlockContext.put("uid", "");
		currentBlockContext.put("gid", "");
		currentBlockContext.put("mode", "");
		currentBlockContext.put("atime", "");
		currentBlockContext.put("ctime", "");
		currentBlockContext.put("mtime", "");
		currentBlockContext.put("linkCount", "");
		currentBlockContext.put("idir_or_ifile", "");
		currentBlockContext.put("filename_to_inode_dict", "");
		currentBlockContext.put("indirect","");
		currentBlockContext.put("location","");
		file_to_inode_dir= new HashMap<String, Tupletwo<String, String>>();

	}
	private void clearCurrentBlockContext(){
		currentBlockContext.put("size", "");
		currentBlockContext.put("uid", "");
		currentBlockContext.put("gid", "");
		currentBlockContext.put("mode", "");
		currentBlockContext.put("atime", "");
		currentBlockContext.put("ctime", "");
		currentBlockContext.put("mtime", "");
		currentBlockContext.put("linkCount", "");
		currentBlockContext.put("idir_or_ifile", "");
		currentBlockContext.put("filename_to_inode_dict", "");
		currentBlockContext.put("indirect","");
		currentBlockContext.put("location","");	
	}
	private void clearFile_to_inode_dir(){
		file_to_inode_dir= new HashMap<String, Tupletwo<String, String>>();
	}
	public static void main(String[] args) {
		// Getting user input about the file system directory
		csefsck fileSystemChecker = new csefsck();
		System.out.println("Welcome to File System Checker");
		System.out.println("Request you to place the file system to be verified"
				+ "in the following directory path C:\\ModernOS\\csefsck\\FS");
		System.out.println("Please press Y after successfully manually moving the File System to"
				+ "be verified to the specified path ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String sFileSystemRootPath = new String("C:\\ModernOS\\csefsck\\FS");
		String userInput = null;
		try {
			userInput = reader.readLine();
			if (userInput.equalsIgnoreCase("Y")) {
				System.out.println(sFileSystemRootPath + " is being accessed to detect file system errors");
				/* Add relevant code to continue process */
				// Calling DeviceId verifier
				boolean flagDeviceId = fileSystemChecker.DeviceIdVerifier();
				System.out.println("**********************************************Please find below the file system checker findings.********************************************************************************");
				System.out.println("Note:The fileSystemChecker does not create any changes to the existing file system.It only detects file system errors");
				System.out.println("which can be used for correction of file systems");
				if (flagDeviceId) {
					System.out.println("The superblock has the correct Device Id .");
					// Starting the file system verifier
					fileSystemChecker.verifier();
				}
			} else {
				System.out.println("Program will exit now .Thank you");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void verifier() {
		boolean verifiedused=false;
		if (superBlockMap != null) {
			if ((superBlockMap.containsKey("freeStart")) && (superBlockMap.containsKey("freeEnd"))
					&& (superBlockMap.containsKey("maxBlocks")) && (superBlockMap.containsKey("root"))) {
				// check for creation time
				if (superBlockMap.containsKey("creationTime") && (superBlockMap.get("creationTime") != null)) {
					boolean cr_Time_chk = false;
					cr_Time_chk = TimeVerifier(superBlockMap.get("creationTime"));
					if (!cr_Time_chk) {
						// Reset superBlockCreationTime to present if it is in
						// future
						System.out.println("Please set Super block creation time to current time");
						superBlockMap.replace("creationTime", superBlockMap.get("creationTime"),
								Long.toString((System.currentTimeMillis()) / 1000));
					}
				} else {
					System.out.println("SuperBlock doesn't have creation time parameter");
					System.out.println("Please set Super block creation time to current time");
					superBlockMap.put("creationTime", Long.toString((System.currentTimeMillis()) / 1000));
				}
				if (superBlockMap.get("root") != null) {
					verifiedused=updateContext(superBlockMap.get("root"), "root","d");
					System.out.println("Total used block number files 'X' in the file system of fusedata.X which can be navigated from root Directory specified in SuperBlock");
					System.out.println(fileSystem.get("usedFileList"));
					int maxBlocks= Integer.parseInt(superBlockMap.get("maxBlocks"));
					//In case all the blocks in the file system are free,so maximum size is equal to maxBlocks.
					//It is known that at max 400 pointers could be in one block.
					//Hence reading the freeBlockList from freeStart to freeEnd to validate the free Block List against used block list identified 
					//In case some free blocks pointers are missing in the freeBlocks or a used block is identified in freeBlocks -It will be notified
					int freeStart=Integer.parseInt(superBlockMap.get("freeStart"));
					int freeEnd=Integer.parseInt(superBlockMap.get("freeEnd"));
					String concatenatedLine= new String();
					for(int index=freeStart;index <=freeEnd;index++){
						String currentBlockPath = "C:\\ModernOS\\csefsck\\FS\\fusedata." + index;
						File currentBlock = new File(currentBlockPath);
						int indexsp=0;
						if (currentBlock.exists()) {
							try (BufferedReader reader = Files.newBufferedReader(currentBlock.toPath())) {
								String line = null;
								while ((line = reader.readLine()) != null) {	
									concatenatedLine=concatenatedLine+","+line;	
								}

							} catch (IOException e) {
								System.err.format("IOException: %s%n", e);
							}}
					}


					concatenatedLine=concatenatedLine.substring(1);
					String[] freeBlockListPointer = concatenatedLine.split(",");
					if(freeBlockListPointer.length > maxBlocks){
						System.out.println("Free Block Pointer exceed the max blocks");
					}
					if(freeBlockListPointer.length < maxBlocks){
						String[] usedpointerlist =fileSystem.get("usedFileList").split(";");
						for(int indexup=0;indexup<usedpointerlist.length;indexup++){
							for(int indexfree=0;indexfree<freeBlockListPointer.length;indexfree++){
								if(usedpointerlist[indexup].equalsIgnoreCase(freeBlockListPointer[indexfree].trim())){
									System.out.println(usedpointerlist[indexup]+" is used and should not be in freeBlock List Pointer");
									break;
								}
							}
						}
						String uF=fileSystem.get("usedFileList").replaceAll(";",",");
						//adding freeStart to freeEnd Pointers

						String totalpointers=concatenatedLine+","+uF+",1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25";
						totalpointers=totalpointers.substring(1);
						String[] totalpointerlist =totalpointers.split(",");

						boolean flag=false;
						for(int indexmax=1;indexmax<maxBlocks;indexmax++){
							flag=false;
							for(int indexTotal=0;indexTotal<totalpointerlist.length;indexTotal++){
								if(indexmax==(Integer.parseInt(totalpointerlist[indexTotal].trim()))){									 
									flag=true;
									break; 
								}

							}
							if(flag==false){
								System.out.println("Pointer "+indexmax+" is missing in the file system or is not accounted for in the free block list");
							}
						}
					}
				}
			}





		}else {
			System.out.println(
					"SuperBlock doesn't have freeEnd or freeStart or maxBlocks or root parameter specified");
			System.out.println("File System verification can't continue as SuperBlock is corrupt");
		}
	}


	private boolean updateContext(String fileBlockNumber,String fileName, String fileType) {

		boolean upfsc_chk, ucfc_chk;
		if (fileName.equalsIgnoreCase("root") == true) {
			//as for root . and .. is again root
			fileSystem.replace("presentDirectory", "", fileBlockNumber);
			fileSystem.replace("lastAccessedDirectory", "", "fileBlockNumber");
		}
		if((fileName.equalsIgnoreCase(".") == false) && (fileName.equalsIgnoreCase("..") == false) ){
			upfsc_chk = updatefileSystemContext(fileBlockNumber,fileName,fileType);
			if (upfsc_chk) {
				ucfc_chk = updatecurrentBlockContext(fileBlockNumber);
				if(ucfc_chk){
					return true;
				}
			}
			else{
				System.out.println("Block-" + fileSystem.get("currentBlock") + "is not found");
			}
		} else {

			if((fileName.equalsIgnoreCase(".") == true)){
				if(fileSystem.get("presentDirectory").equalsIgnoreCase(fileBlockNumber) == false){
					System.out.println(". value is wrong in Directory inode for-"+fileSystem.get("presentDirectory"));
					return false;
				}
				else{
					return true;
				}
			}else if (fileName.equalsIgnoreCase("..") == true){
				if(fileSystem.get("lastAccessedDirectory").equalsIgnoreCase(fileBlockNumber) == false){
					System.out.println(".. value is wrong in Directory inode for -"+fileSystem.get("presentDirectory"));
					return false;
				}
				else{
					return true;
				}
			}

		}
		return false;

	}

	private boolean updatecurrentBlockContext(String fileBlockNumber) {
		String currentBlockPath = "C:\\ModernOS\\csefsck\\FS\\fusedata." + fileBlockNumber;
		File currentBlock = new File(currentBlockPath);
		if (currentBlock.exists()) {
			try (BufferedReader reader = Files.newBufferedReader(currentBlock.toPath())) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] sParameter = line.split(",");
					int index,indexSP, sPLength = sParameter.length;
					if (sPLength > 0) {
						if(fileSystem.get("currentBlockType").equalsIgnoreCase("f") == true){
							clearFile_to_inode_dir();
							currentBlockContext.replace("dir_or_file",currentBlockContext.get("idir_or_ifile"),"iFile");
							for (index = 0; index < sPLength; index++) {
								if(sParameter[index].contains("indirect") != true){
									String[] sMapValues = sParameter[index].split(":");
									sMapValues[0] = sMapValues[0].replaceAll("[{}]", "");
									sMapValues[1] = sMapValues[1].replaceAll("[{}]", "");
									if (sMapValues[0] != null && sMapValues[1] != null) {										
										currentBlockContext.put(sMapValues[0].trim(), sMapValues[1].trim());
									}
								}
								else{
									sParameter[index]=sParameter[index].trim();
									sParameter[index]=sParameter[index].replaceAll("[{}]", "");
									String[] sSubParameters = sParameter[index].split(" ");
									int sSPLength=sSubParameters.length;
									for (indexSP = 0; indexSP < sSPLength; indexSP++) {
										String[] sMapValues = sSubParameters[indexSP].split(":");
										sMapValues[0] = sMapValues[0].replaceAll("[{}]", "");
										sMapValues[1] = sMapValues[1].replaceAll("[{}]", "");
										if (sMapValues[0] != null && sMapValues[1] != null) {										
											currentBlockContext.put(sMapValues[0].trim(), sMapValues[1].trim());
										}

									}

								}
							}
							//verify all time are in past-start
							boolean ctime_chk=false,atime_chk=false,mtime_chk=false;
							if(currentBlockContext.containsKey("ctime") == true){
								if(currentBlockContext.get("ctime")!=null){
									ctime_chk=TimeVerifier(currentBlockContext.get("ctime"));
									if(!ctime_chk){
										System.out.println("Please set creation time to current time for fusedata."+fileBlockNumber);
										currentBlockContext.replace("ctime", currentBlockContext.get("ctime"),
												Long.toString((System.currentTimeMillis()) / 1000));
									}
								}
								else{
									System.out.println("inode doesn't have ctime parameter");
									System.out.println("Please set ctime to current time for fusedata."+fileBlockNumber);
									currentBlockContext.put("ctime", Long.toString((System.currentTimeMillis()) / 1000));
								}
							}
							else
							{
								System.out.println("inode doesn't have ctime parameter");
								System.out.println("Please set ctime to current time");
								currentBlockContext.put("ctime", Long.toString((System.currentTimeMillis()) / 1000));
							}
							if(currentBlockContext.containsKey("atime") == true){
								if(currentBlockContext.get("atime")!=null){
									atime_chk=TimeVerifier(currentBlockContext.get("atime"));	
									if(!atime_chk){
										System.out.println("Please set atime to current time for fusedata."+fileBlockNumber);
										currentBlockContext.replace("atime", currentBlockContext.get("atime"),
												Long.toString((System.currentTimeMillis()) / 1000));
									}
								}
								else{
									System.out.println("inode doesn't have atime parameter");
									System.out.println("Please set atime to current time for fusedata."+fileBlockNumber);
									currentBlockContext.put("atime", Long.toString((System.currentTimeMillis()) / 1000));
								}
							}
							else
							{
								System.out.println("inode doesn't have atime parameter");
								System.out.println("Please set atime to current time for fusedata."+fileBlockNumber);
								currentBlockContext.put("atime", Long.toString((System.currentTimeMillis()) / 1000));
							}
							if(currentBlockContext.containsKey("mtime") == true){
								if(currentBlockContext.get("mtime")!=null){
									mtime_chk=TimeVerifier(currentBlockContext.get("mtime"));
									if(!mtime_chk){
										System.out.println("Please set mtime to current time for fusedata."+fileBlockNumber);
										currentBlockContext.replace("mtime", currentBlockContext.get("mtime"),
												Long.toString((System.currentTimeMillis()) / 1000));
									}
								}
								else{
									System.out.println("inode doesn't have mtime parameter");
									System.out.println("Please set mtime to to current time for fusedata."+fileBlockNumber);
									currentBlockContext.put("mtime", Long.toString((System.currentTimeMillis()) / 1000));
								}
							}
							else{
								System.out.println("inode doesn't have mtime parameter");
								System.out.println("Please set mtime to to current time for fusedata."+fileBlockNumber);
								currentBlockContext.put("mtime", Long.toString((System.currentTimeMillis()) / 1000));
							}

							//verify all time are in past-end
							//verify size and location array-start
							boolean chk_loc_array = false;
							if(currentBlockContext.containsKey("size")){
								if(currentBlockContext.get("size")!= null){
									int size= Integer.parseInt(currentBlockContext.get("size"))	;			
									//Default size of a block is considered 4096 bytes
									int defaultSize =4096;
									if(size > 0){
										int correct_length_location_array=(int) Math.ceil(size/defaultSize);
										int correct_indirect_value = 0;
										if(correct_length_location_array > 1){
											correct_indirect_value = 1;
											if((correct_indirect_value)-(Integer.parseInt(currentBlockContext.get("indirect"))) != 0){
												System.out.println("Indirect parameter is set in-correctly in file inode -fusedata."+fileSystem.get("currentBlock")); 
												currentBlockContext.replace("indirect",currentBlockContext.get("indirect"),Integer.toString(correct_indirect_value));
											}

											if(currentBlockContext.get("location") != null){
												chk_loc_array=validate_size_location_array(size,currentBlockContext.get("location"),correct_length_location_array);
												if(chk_loc_array){
													return true;
												}
											}
											else
											{
												System.out.println("Location Parameter not specified,so can't validate file inode-fusedata."+fileSystem.get("currentBlock"));	
											}
										}

										else{
											correct_indirect_value = 0;
											if((correct_indirect_value) != (Integer.parseInt(currentBlockContext.get("indirect")))){
												System.out.println("Indirect parameter is set in-correctly in file inode -fusedata."+fileSystem.get("currentBlock"));
												currentBlockContext.replace("indirect",currentBlockContext.get("indirect"),Integer.toString(correct_indirect_value));
											}
											if(currentBlockContext.get("location") != null){
												chk_loc_array=validate_size_location_array(size,currentBlockContext.get("location"),correct_length_location_array);
												if(chk_loc_array){
													return true;
												}
											}
											else
											{
												System.out.println("Location Parameter not specified,so can't validate file inode-fusedata."+fileSystem.get("currentBlock"));	
											}
										}
									}
								}else{
									System.out.println("Invalid size as size is less than zero");
								}
							}
							else{
								System.out.println("Insufficient parameters specified,so can't validate file");
							}
							//verify size and location array-end
						}
						else if(fileSystem.get("currentBlockType").equalsIgnoreCase("d") == true){
							clearFile_to_inode_dir();
							currentBlockContext.replace("dir_or_file",currentBlockContext.get("idir_or_ifile"),"iDir");
							for (index = 0; index < sPLength; index++) {
								if(sParameter[index].contains("size") ||sParameter[index].contains("uid")||sParameter[index].contains("gid") ||sParameter[index].contains("mode")|| sParameter[index].contains("atime")||sParameter[index].contains("ctime") ||sParameter[index].contains("mtime") ||sParameter[index].contains("linkcount")){
									String[] sMapValues = sParameter[index].split(":");
									sMapValues[0] = sMapValues[0].replaceAll("[{}]", "");
									sMapValues[1] = sMapValues[1].replaceAll("[{}]", "");
									if (sMapValues[0] != null && sMapValues[1] != null) {										
										currentBlockContext.put(sMapValues[0].trim(), sMapValues[1].trim());
									}
								}
							}
							String fd_para=line.substring(line.indexOf("filename_to_inode_dict"));
							String fd_para_value= fd_para.substring(fd_para.indexOf(":")+1);
							fd_para_value=fd_para_value.replaceAll("[{}]", "");
							currentBlockContext.put("filename_to_inode_dict",fd_para_value.trim());
							//verify all time are in past-start
							boolean ctime_chk=false,atime_chk=false,mtime_chk=false;
							if(currentBlockContext.containsKey("ctime") == true){
								if(currentBlockContext.get("ctime")!=null){
									ctime_chk=TimeVerifier(currentBlockContext.get("ctime"));
									if(!ctime_chk){
										System.out.println("Please set creation time to current time");
										currentBlockContext.replace("ctime", currentBlockContext.get("ctime"),
												Long.toString((System.currentTimeMillis()) / 1000));
									}
								}
								else{
									System.out.println("inode doesn't have ctime parameter");
									System.out.println("Please set ctime to current time for fusedata."+fileBlockNumber);
									currentBlockContext.put("ctime", Long.toString((System.currentTimeMillis()) / 1000));
								}
							}
							else
							{
								System.out.println("inode doesn't have ctime parameter");
								System.out.println("Please set ctime to current time for fusedata."+fileBlockNumber);
								currentBlockContext.put("ctime", Long.toString((System.currentTimeMillis()) / 1000));
							}
							if(currentBlockContext.containsKey("atime") == true){
								if(currentBlockContext.get("atime")!=null){
									atime_chk=TimeVerifier(currentBlockContext.get("atime"));	
									if(!atime_chk){
										System.out.println("Please set atime to current time for fusedata."+fileBlockNumber);
										currentBlockContext.replace("atime", currentBlockContext.get("atime"),
												Long.toString((System.currentTimeMillis()) / 1000));
									}
								}
								else{
									System.out.println("inode doesn't have atime parameter");
									System.out.println("Please set atime to current time for fusedata."+fileBlockNumber);
									currentBlockContext.put("atime", Long.toString((System.currentTimeMillis()) / 1000));
								}
							}
							else
							{
								System.out.println("inode doesn't have atime parameter");
								System.out.println("Please set atime to current time for fusedata."+fileBlockNumber);
								currentBlockContext.put("atime", Long.toString((System.currentTimeMillis()) / 1000));
							}
							if(currentBlockContext.containsKey("mtime") == true){
								if(currentBlockContext.get("mtime")!=null){
									mtime_chk=TimeVerifier(currentBlockContext.get("mtime"));
									if(!mtime_chk){
										System.out.println("Please set mtime to current time for fusedata."+fileBlockNumber);
										currentBlockContext.replace("mtime", currentBlockContext.get("mtime"),
												Long.toString((System.currentTimeMillis()) / 1000));
									}
								}
								else{
									System.out.println("inode doesn't have mtime parameter");
									System.out.println("Please set mtime to current time for fusedata."+fileBlockNumber);
									currentBlockContext.put("mtime", Long.toString((System.currentTimeMillis()) / 1000));
								}
							}
							else{
								System.out.println("inode doesn't have mtime parameter");
								System.out.println("Please set mtime to current time for fusedata."+fileBlockNumber);
								currentBlockContext.put("mtime", Long.toString((System.currentTimeMillis()) / 1000));
							}

							//verify all time are in past-end
							if(currentBlockContext.containsKey("filename_to_inode_dict") == true){
								if(currentBlockContext.get("filename_to_inode_dict")!=null){
									if(currentBlockContext.get("filename_to_inode_dict").contains(",") == true){
										String[] sfileInodeDictParaOver = currentBlockContext.get("filename_to_inode_dict").split(",");
										int indexDict,sfIDP_Length=sfileInodeDictParaOver.length;
										if((currentBlockContext.get("linkcount")).equalsIgnoreCase(Integer.toString(sfIDP_Length)) == false){
											System.out.println("Link count of Directory file is incorrect .Please set it to number of links to filename_to_inode_dict");
											currentBlockContext.replace("linkcount", currentBlockContext.get("linkcount"), Integer.toString(sfIDP_Length));
										}
										for (indexDict = 0; indexDict < sfIDP_Length; indexDict++) {
											String[] sfileInodeDictPara = sfileInodeDictParaOver[indexDict].split(":");
											clearFile_to_inode_dir();
											if(sfileInodeDictPara.length == 3){
												sfileInodeDictPara[0] = sfileInodeDictPara[0].replaceAll("[{}]", "");
												sfileInodeDictPara[1] = sfileInodeDictPara[1].replaceAll("[{}]", "");
												sfileInodeDictPara[2] = sfileInodeDictPara[2].replaceAll("[{}]", "");
												Tupletwo ttobj=new Tupletwo(sfileInodeDictPara[2].trim(),sfileInodeDictPara[0].trim());
												if (sfileInodeDictPara[0] != null && sfileInodeDictPara[1] != null && sfileInodeDictPara[2] != null) {										
													file_to_inode_dir.put(sfileInodeDictPara[1].trim(),ttobj);
												}
												else{
													System.out.println("Insufficient parameters specified can't traverse directory or file-"+currentBlockContext.get("filename_to_inode_dict"));
												}
											}
											if(file_to_inode_dir != null){

												Iterator<String> keySetIterator = file_to_inode_dir.keySet().iterator();
												while (keySetIterator.hasNext()) {
													Tupletwo<String,String> tt= new Tupletwo<String,String>("","");
													String key = keySetIterator.next();
													tt=file_to_inode_dir.get(key);
													if(tt != null){
														if((tt.blockNumber != null) && (tt.blockType != null)){
															updateContext(tt.blockNumber,key,tt.blockType);
														}
													}

												}

											}

											else{
												System.out.println("Insufficient parameters specified can't traverse directory or file-"+currentBlockContext.get("filename_to_inode_dict"));
											}
										}
									}
									else{
										if((currentBlockContext.get("linkcount")).equalsIgnoreCase("1") == false){
											System.out.println("Link count of Directory file is incorrect .Please set it to number of links to filename_to_inode_dict");
											currentBlockContext.replace("linkcount", currentBlockContext.get("linkcount"), "1");
										}
										if(currentBlockContext.get("filename_to_inode_dict").contains(":") == true){
											String[] sfileInodeDictPara=currentBlockContext.get("filename_to_inode_dict").split(":");
											if(sfileInodeDictPara.length == 3){
												sfileInodeDictPara[0] = sfileInodeDictPara[0].replaceAll("[{}]", "");
												sfileInodeDictPara[1] = sfileInodeDictPara[1].replaceAll("[{}]", "");
												sfileInodeDictPara[2] = sfileInodeDictPara[2].replaceAll("[{}]", "");
												if (sfileInodeDictPara[0] != null && sfileInodeDictPara[1] != null && sfileInodeDictPara[2] != null) {
													file_to_inode_dir.put(sfileInodeDictPara[1].trim(),new Tupletwo<String, String>(new String(sfileInodeDictPara[0].trim()),new String(sfileInodeDictPara[2].trim())));
												}
												else{
													System.out.println("Insufficient parameters specified can't traverse directory or file-"+currentBlockContext.get("filename_to_inode_dict"));
												}
											}
											else{
												System.out.println("Insufficient parameters specified can't traverse directory or file-"+currentBlockContext.get("filename_to_inode_dict"));
											}
										}
										else
										{
											System.out.println("Insufficient parameters specified can't traverse current directory");
										}
									}
								}
								else
								{
									System.out.println("The Parameter filename_to_inode_dict not specified ,Hence can't traverse the directory further");
								}

							}

						}

					}
				}



			} catch (IOException x) {
				System.err.format("IOException: %s%n", x);
				return false;
			}
		}
		return false;

	}

	private boolean validate_size_location_array(int size, String locationBlockNumber, int correct_length_location_array) {
		String currentLocationArrayPath = "C:\\ModernOS\\csefsck\\FS\\fusedata." + locationBlockNumber;
		File currentLocationArray = new File(currentLocationArrayPath);
		String usedFileList = new String();
		String usedFileListMod = new String();
		int length_data_pointers=0;
		if(size > 4096){
			//If size is greater than the default block size=4096 bytes then we need to match the correct_length_location_array 
			//with the actual number of pointers available in block number of location array.If they are not equal,file can't be validated fully
			//Used file list includes the block numbers specified in location index block for size > 4096 
			//Reading the location array size
			try (BufferedReader reader = Files.newBufferedReader(currentLocationArray.toPath())) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
					if(line.contains(",")== true){
						String[] dataPointers=line.split(",");
						length_data_pointers=dataPointers.length;

						//accounting used file list
						if(length_data_pointers <= correct_length_location_array){
							for(int index=0;index<length_data_pointers;index++){
								usedFileList = fileSystem.get("usedFileList");
								if (usedFileList.isEmpty()) {
									fileSystem.replace("usedFileList", usedFileList, dataPointers[index]);
								} else {
									usedFileListMod = usedFileList + ";" + dataPointers[index];
									fileSystem.replace("usedFileList", usedFileList, usedFileListMod);
								}	
							}
						}

						if((size > (4096 *(length_data_pointers-1))) && (size < (4096 *(length_data_pointers)))){
							System.out.println("Size is valid for fusedata."+fileSystem.get("currentBlock"));
							return true;
						}
						else{
							System.out.println("Location index block is missing information/in consistent in information related to size for fusedata."+fileSystem.get("currentBlock"));
							return false;
						}
					}
					else{
						System.out.println("Location index block is missing information for fusedata."+fileSystem.get("currentBlock"));
						return false;
					}
				}
			}
			catch (IOException x) {
				System.err.format("IOException: %s%n", x);
				return false;
			}
			return false;
		}

		else {
			//If Size is less than default size ,indirect should be 0 and location should indicate the file data
			if (currentLocationArray.exists()) {
				//Accounting used file list
				usedFileList = fileSystem.get("usedFileList");
				if (usedFileList.isEmpty()) {
					fileSystem.replace("usedFileList", usedFileList, locationBlockNumber);
				} else {
					usedFileListMod = usedFileList + ";" + locationBlockNumber;
					fileSystem.replace("usedFileList", usedFileList, usedFileListMod);
				}	
			}

			return true;
		}
	}
	private boolean updatefileSystemContext(String fileBlockNumber,String fileName,String fileType) {
		String currentBlockPath = "C:\\ModernOS\\csefsck\\FS\\fusedata." + fileBlockNumber;
		File currentBlock = new File(currentBlockPath);
		String usedFileList = new String();
		String usedFileListMod = new String();
		if (currentBlock.exists()) {
			if (fileSystem.get("lastAccessedDirectory") != null) {
				// when current block is root
				if (fileName.equalsIgnoreCase("root")) {
					fileSystem.replace("currentBlock", fileSystem.get("currentBlock"), fileBlockNumber);
					fileSystem.replace("currentBlockType", fileSystem.get("currentBlockType"), "d");
				} else {
					// when current block is not root
					fileSystem.replace("currentBlock", fileSystem.get("currentBlock"), fileBlockNumber);
					fileSystem.replace("currentBlockType", fileSystem.get("currentBlockType"), fileType);
				}
				if(fileType.equalsIgnoreCase("d")){
					if((fileName.equalsIgnoreCase(".") == false) && (fileName.equalsIgnoreCase("..") == false) ){
						fileSystem.replace("presentDirectory", fileSystem.get("presentDirectory"), fileBlockNumber);
						fileSystem.replace("lastAccessedDirectory", fileSystem.get("lastAccessedDirectory"), fileSystem.get("presentDirectory"));
					}
					else
					{
						if((fileName.equalsIgnoreCase(".") == true)){
							if(fileSystem.get("presentDirectory").equalsIgnoreCase(fileBlockNumber) == false){
								System.out.println(". value is wrong in Directory inode for-"+fileSystem.get("presentDirectory"));
							}
						}else if (fileName.equalsIgnoreCase("..") == true){
							if(fileSystem.get("lastAccessedDirectory").equalsIgnoreCase(fileBlockNumber) == false){
								System.out.println(".. value is wrong in Directory inode for -"+fileSystem.get("presentDirectory"));
							}
						}
					}
				}

				if((fileName.equalsIgnoreCase(".") == false) && (fileName.equalsIgnoreCase("..") == false) ){
					usedFileList = fileSystem.get("usedFileList");
					if (usedFileList.isEmpty()) {
						fileSystem.replace("usedFileList", usedFileList, fileBlockNumber);
					} else {
						usedFileListMod = usedFileList + ";" + fileBlockNumber;
						fileSystem.replace("usedFileList", usedFileList, usedFileListMod);
					}
				}
				return true;
			}
			else {
				System.out.println("File system root directory is corrupt");
				System.out.println("File System verification can't continue as Root Directory is corrupt");
				return false;
			}
		} else {
			System.out.println("File system root directory is corrupt");
			System.out.println("File System verification can't continue as Root Directory is corrupt");
			return false;
		}

	}

	private boolean TimeVerifier(String epochTime) {
		/* Checks all the files have time in fast and nothing in future */
		int unixTime = Integer.parseInt(epochTime);
		long timeStamp = (long) unixTime * 1000;
		Date epoch_dateTime = new Date(timeStamp);
		Date current_time = new Date();
		if (epoch_dateTime.before(current_time)) {
			if ((fileSystem != null)) {
				if ((fileSystem.containsKey("currentBlock")) && (fileSystem.get("currentBlock") != null)) {
					return true;
				}
			}
		} else {
			return false;
		}
		return false;

	}

	private boolean DeviceIdVerifier() {
		/**
		 * Accessing the superblock of the FileSystem to verify its device Id
		 * System specification:devId is always maintained 20 for this
		 * particular use case.
		 */
		int iDeviceId_Std = 20;
		File superblock = new File("C:\\ModernOS\\csefsck\\FS\\fusedata.0");
		// Making superBlockMap a private variable of class to have the super
		// block entry available as context information
		// HashMap<String,String> superBlockMap= new HashMap<String,String>();
		if (superblock.exists()) {
			try (BufferedReader reader = Files.newBufferedReader(superblock.toPath())) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					boolean devIdexists = line.contains("devId");
					if (devIdexists) {
						String[] sParameter = line.split(",");
						int index, sPLength = sParameter.length;
						if (sPLength > 0) {
							for (index = 0; index < sPLength; index++) {
								String[] sMapValues = sParameter[index].split(":");
								sMapValues[0] = sMapValues[0].replaceAll("[{}]", "");
								sMapValues[1] = sMapValues[1].replaceAll("[{}]", "");
								if (sMapValues[0] != null && sMapValues[1] != null) {
									superBlockMap.put(sMapValues[0].trim(), sMapValues[1].trim());
								}
							}
						}
						// Verify correct devId is loaded
						if (superBlockMap != null) {
							if (superBlockMap.containsKey("devId")) {
								String sDeviceId_SB = superBlockMap.get("devId");
								// Considering Device Id=20 as a standard
								String sDeviceId_Standard = new String("20");
								if (sDeviceId_SB.trim().equalsIgnoreCase(sDeviceId_Standard)) {
									fileSystem.replace("currentBlock", fileSystem.get("0"),
											superblock.getName());
									fileSystem.replace("currentBlockType", fileSystem.get("currentBlockType"),
											"SuperBlock");
									Iterator<String> keySetIterator = fileSystem.keySet().iterator();
									while (keySetIterator.hasNext()) {
										String key = keySetIterator.next();
									}
									return true;
								} else {
									System.out.println("Wrong Device ID specified ");
									superBlockMap.replace("devId", sDeviceId_SB, sDeviceId_Standard);
									System.out.println("Device Id autocorrected to default 20");
									System.out.println("Please find below -current SuperBlock Specifications");
									Iterator<String> keySetIterator = superBlockMap.keySet().iterator();
									while (keySetIterator.hasNext()) {
										String key = keySetIterator.next();
										System.out.println(key + "-" + superBlockMap.get(key));
									}
									return false;
								}
							}

						}

					} else {
						System.out.println("Dev Id is not present as a parameter in SuperBlock");
						System.out.println("File System verification can't continue as SuperBlock is corrupt");
						return false;
					}
				}
			} catch (IOException x) {
				System.err.format("IOException: %s%n", x);
				return false;
			}
		} else {
			System.out.println("File System verification can't continue as SuperBlock is not present");
			return false;
		}

		return false;
	}

}
