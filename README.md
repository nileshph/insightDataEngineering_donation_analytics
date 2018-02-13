Donation Analytics Challenge

Approach:

0. Only those records which follow rules given in the challenge are considered for processing.
    I also observed some records which transaction amount as negative amount, considering those records
    as invalid, I have skipped them from processing.
    
1. As we need to check if the donor is a repeated donor or not based on already streamed data, all the 	streamed data need to be kept in memory and we need to allow faster access to that data
2. I used HashMap inside HashMap to do that. Reason to use HashMap was to get faster access to previously streamed data. Below is the structure of complex HashMap used along with its key value correspondence with the actual data dictionary.

HashMap<Zip code, HashMap<Name, HashMap<Year, HashMap<CMTE_ID, ArrayList<Amount>>>>>

3. As every valid record comes in, we need to check if the donor in the current record is repeated donor or not, we use zip code and donor's name to identify donor uniquely.

4. If the that donor has more than one year’s data, it means he has more than one donation which is done in separate years and this marks that donor as repeated donor.

5. Using current records zip code, year of transaction and CMTE ID, we try to find out all the transactions for the that zip code, year of transaction and CMTE ID. All these are the repeated transactions. We store those transaction amounts in the list for percentile calculation. We also keep track of total number of transactions and its accumulated sum.

6. As we have list of all transaction amounts, we use nearest rank method to calculate percentile. For that reason, we sort the list of all transaction amounts from repeated donors and then calculate ordinal rank using 
Ceil of (percentile value from file /100 * total number of items in list), then we consider value at this rank/index from the list.

7. All this data is written into output file

How to build and run the code:

Run the run.sh script which will create a class file in /src directory and then it will run that class file with input file, percentile file and output file names as an argument.

Dependencies:
This program does not use any other libraries or dependencies other that Java 1.8
