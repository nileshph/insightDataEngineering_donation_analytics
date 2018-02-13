package com.insight;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class Solution {

	public static void main(String[] args) throws ParseException, FileNotFoundException {

		String inputFile = args[0];
		String percentileFile = args[1];
		String outputFile = args[2];

		PrintWriter writer = new PrintWriter(new File(outputFile));

		/*
		 * HashMap to hold data
		 * HashMap<Zip code, HashMap<Name, HashMap<Year, HashMap<CMTE_ID, ArrayList<Amount>>>>>
		 */
		HashMap<String, HashMap<String, HashMap<Integer, HashMap<String, ArrayList<Integer>>>>> data = new HashMap<>();

		double percentileValue = 0;
		Scanner sc = null;

		try {
			sc = new Scanner(new File(percentileFile));
			percentileValue = sc.nextDouble();

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		finally {
			sc.close();
		}

		try {
			sc = new Scanner(new File(inputFile));
			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				String[] lineData = line.split("\\|");

				String cmteId = lineData[0];
				String name = lineData[7];
				String zipCode = lineData[10];
				String date = lineData[13];
				String amount = lineData[14];
				String otherId = lineData[15];
				SimpleDateFormat dateFormatter =  new SimpleDateFormat("MMddyyyy");

				//consider record only if it passes sanity tests
				if(isValidData(cmteId,name,zipCode,
						date,amount,otherId))
				{	
					zipCode = zipCode.substring(0, 5);
					@SuppressWarnings("deprecation")
					int txnYear = dateFormatter.parse(date).getYear() + 1900;

					if(data.containsKey(zipCode))
					{
						if(data.get(zipCode).containsKey(name))
						{
							if(data.get(zipCode).get(name).containsKey(txnYear))
							{
								if(data.get(zipCode).get(name).get(txnYear).containsKey(cmteId))
									data.get(zipCode).get(name).get(txnYear).get(cmteId).add(Integer.parseInt(amount));
								else
								{
									ArrayList<Integer> list = new ArrayList<>();
									list.add(Integer.parseInt(amount));
									data.get(zipCode).get(name).get(txnYear).put(cmteId, list);
								}
							}
							else
							{
								//year data is not available
								ArrayList<Integer> list = new ArrayList<>();
								list.add(Integer.parseInt(amount));

								HashMap<String, ArrayList<Integer>> receipient_map = new HashMap<>();
								receipient_map.put(cmteId, list);
								data.get(zipCode).get(name).put(txnYear, receipient_map);				
							}

							int repeat_donor_cnt=0;
							int total_amt_donated = 0;

							/*
							 * if for current donor(name,zip code) we get more than one year's
							 * data the it means that donor is a repeated donor
							 */
							if(data.get(zipCode).get(name).keySet().size() > 1)
							{
								HashMap<String, HashMap<Integer, HashMap<String, ArrayList<Integer>>>> name_map = data.get(zipCode);
								ArrayList<Integer> repeatAmountList = new ArrayList<>();

								for(String nm: name_map.keySet())
								{
									if(name_map.get(nm).get(txnYear)!=null &&
											name_map.get(nm).get(txnYear).get(cmteId)!=null)
									{
										ArrayList<Integer> amountsList = name_map.get(nm).get(txnYear).get(cmteId);

										for (int amt: amountsList) {
											repeatAmountList.add(repeat_donor_cnt,amt);
											repeat_donor_cnt ++;
											total_amt_donated+=amt;
										}
									}
								}

								int rank = (int) Math.ceil((percentileValue/100*repeat_donor_cnt));
								/*
								 * sort the donation amounts list for percentile calculation
								 */
								Collections.sort(repeatAmountList);
								writer.println(cmteId + "|" + zipCode + "|" + txnYear + "|" + repeatAmountList.get(rank-1) + "|" + total_amt_donated + "|" + repeat_donor_cnt);
							}
						}
						else
						{
							/*
							 * for given zip code, name data is not available
							 * i.e. donor is new donor
							 * 
							 */
							ArrayList<Integer> amountsList = new ArrayList<>();
							amountsList.add(Integer.parseInt(amount));

							HashMap<String, ArrayList<Integer>> receipientMap = new HashMap<>();
							receipientMap.put(cmteId, amountsList);

							HashMap<Integer, HashMap<String, ArrayList<Integer>>> yearMap = new HashMap<>();
							yearMap.put(txnYear, receipientMap);

							data.get(zipCode).put(name, yearMap);
						}
					}
					else
					{
						/*
						 * There is no data for the given zip code
						 */
						ArrayList<Integer> list = new ArrayList<>();
						list.add(Integer.parseInt(amount));

						HashMap<String, ArrayList<Integer>> receipient_map = new HashMap<>();
						receipient_map.put(cmteId, list);

						HashMap<Integer, HashMap<String, ArrayList<Integer>>> year_map = new HashMap<>();
						year_map.put(txnYear, receipient_map);

						HashMap<String, HashMap<Integer, HashMap<String, ArrayList<Integer>>>> name_map = new HashMap<>();
						name_map.put(name, year_map);

						data.put(zipCode, name_map);	
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			if(sc!=null)
				sc.close();

			if(writer!=null)
				writer.close();
		}
	}

	private static boolean isValidData(String cmte_id, String name, String zip_code, String date, String amount,
			String other_id) {

		/*
		 * Various tests to check if current record need to be used
		 * for processing
		 */
		if(other_id.length() > 0)
			return false;

		if(zip_code.length() < 5)
			return false;

		if(name.length() ==0)
			return false;

		if(cmte_id.length()==0)
			return false;

		if(amount.length()==0)
			return false;

		if(Integer.parseInt(amount) < 0)
			return false;
		
		SimpleDateFormat formatter =  new SimpleDateFormat("MMDDYYYY");

		try {
			Date txnDate = formatter.parse(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
}
