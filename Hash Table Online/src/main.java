import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


class Report{
    //Before Deletion
    public double avgTimeChainingB;
    public double avgTimeLinearB;
    public double avgTimeQuadB;
    public double avgTimeDoubleB;

    public int probeLinearB;
    public int probeQuadB;
    public int probeDoubleB;

    //After Deletion
    public double avgTimeChainingA;
    public double avgTimeLinearA;
    public double avgTimeQuadA;
    public double avgTimeDoubleA;

    public int probeLinearA;
    public int probeQuadA;
    public int probeDoubleA;

    public double searchTimeOldTable;
    public double searchTimeNewTable;

    public Report(){

    }

}

public class main {
    static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    static  int tablesize;

    static boolean isPrime(long x){
        if(x == 2 || x == 3) return true;
        if(x == 1 || x %2 == 0) return false;
        for(int i=3; i*i <= x; i+=2){
            if(x%i==0) return false;
        }
        return true;
    }

    static  int nextPrime(int x){
        if(x%2==0)x++;
        else x+=2;
        while(!isPrime(x)){
            x+=2;
        }
        return x;
    }

    static int previousPrime(int x){
        if(x%2==0)x--;
        else x-=2;
        while(!isPrime(x)){
            x-=2;
        }
        return x;
    }

    static String[] generateWords(int sampleSize){
        String[] dataset;
        Set<String> set = new HashSet<String>();
        dataset = new String[sampleSize];
        StringBuilder string = new StringBuilder("abcdefg");
        int count = 0;
        Random rand = new Random();
        while(count < sampleSize){
            for(int i=0; i<7; i++){
                string.setCharAt(i, alphabet.charAt((rand.nextInt(100000))%alphabet.length()));
            }
            if(set.contains(string.toString())){
                continue;
            }
            else{
                set.add(string.toString());
                dataset[count] = string.toString();
                //System.out.println(dataset[count]);
                count++;
            }
        }
        return dataset;
    }

    static int testUniqueHash(String[] dataset){
        Set<Long> set = new HashSet<Long>();
        int count = 0;
        HashTable hashTable = new HashTable(tablesize);
        for(int i=0; i<dataset.length; i++){
            long hash = hashTable.H2(dataset[i]);
            hash = hash % tablesize;
            if(!set.contains(hash)){
                set.add(hash);
               // System.out.println(hash);
                count++;
            }
        }
        count = (int) Math.floor((count/(dataset.length * 1.0))*100);
        return count;
    }

    static String[] selectRandom(String[] dataset){
        int n = dataset.length;
        n = (int) (n*0.1);
        String[] tempDataset = new String[n];
        Random rand = new Random();
        Set<String> set = new HashSet<String>();
        int count = 0;
        while (count < n){
            String temp = dataset[rand.nextInt(100000)% dataset.length];
            if(!set.contains(temp)){
                tempDataset[count] = temp;
                set.add(temp);
                //System.out.println(temp);
                count++;
            }
        }
        return tempDataset;
    }

    static String[] selectRandom2(String[] dataset,String[] deletedDataset){
        int n = deletedDataset.length;
        n = (n/2);
        int total = n*2;
        String[] tempDataset = new String[total];
        Random rand = new Random();
        Set<String> set = new HashSet<String>();
        int count = 0;
        for(int i=0; i< deletedDataset.length; i++){
            if(count<n){
                tempDataset[i] = deletedDataset[i];
                count++;
            }
            set.add(deletedDataset[i]);
        }
        while (count < total){
            String temp = dataset[rand.nextInt(100000)% dataset.length];
            if(!set.contains(temp)){
                tempDataset[count] = temp;
                set.add(temp);
                //System.out.println(temp);
                count++;
            }
        }
        return tempDataset;
    }

    static Report generateReport(double load){
        int datasetSize = (int) (tablesize*load*1.0);
        String[] dataset = generateWords(datasetSize);
        Report report = new Report();
        HashTable seperateChaining = new HashTable(tablesize);
        HashTable seperateChaining2 = null;
        Boolean flag = false;
        int maxChainLength = -1;
        //Inserting
        for(int i=0; i<datasetSize; i++){
            seperateChaining.insertChainHash(dataset[i],i);
            if((i+1)%100==0){
                maxChainLength = seperateChaining.getMaxChainLength();
                //System.out.println(maxChainLength);
                if(maxChainLength > 10 && flag==false){
                    int newSize = (int) (tablesize*1.2);
                    newSize = nextPrime(newSize);
                    seperateChaining2 = new HashTable(newSize);
                    flag = true;
                }
            }
        }

        if(flag){
            for(int i=0; i<datasetSize;i++){
                seperateChaining2.insertChainHash(dataset[i],i);
            }
        }

        String[] searchDataset = selectRandom(dataset);
        long chainTime1=0,chainTime2=0,linearTime=0,quadTime=0,doubleTime=0;


        for(int i=0; i<searchDataset.length; i++){
            long start,end;
            start = System.currentTimeMillis();
            seperateChaining.searchChainHash(searchDataset[i]);
            end = System.currentTimeMillis();
            chainTime1 += (end - start);

            if(seperateChaining2 != null) {
                start = System.currentTimeMillis();
                seperateChaining2.searchChainHash(searchDataset[i]);
                end = System.currentTimeMillis();
                chainTime2 += (end - start);
            }

        }
        System.out.println(chainTime1);
        report.searchTimeOldTable = chainTime1/(searchDataset.length*1.0);
        report.searchTimeNewTable = chainTime2/(searchDataset.length*1.0);
        System.out.println("Before rehashing: "+report.searchTimeOldTable);
        System.out.println("After rehashing: "+report.searchTimeNewTable);
        System.out.println("Before rehashing: "+seperateChaining.getMaxChainLength());
        if(seperateChaining2!=null){
            System.out.println("Before rehashing: "+seperateChaining2.getMaxChainLength());
        }

        HashTable seperateChaining3=null;
        Boolean flag2 = false;
        String[] deleteDataset = selectRandom(dataset);
        //Deleting
        for(int i=0; i< deleteDataset.length; i++){
            seperateChaining.deleteChainHash(deleteDataset[i]);
            if((i+1)%100==0){
                maxChainLength = seperateChaining.getMaxChainLength();
                if(maxChainLength < 3 && flag2==false){
                    int newSize = (int) (tablesize*0.8);
                    newSize = previousPrime(newSize);
                    seperateChaining3 = new HashTable(newSize);
                    flag2 = true;
                }
            }
        }

        if(flag2){
            for(int i=0; i<datasetSize;i++){
                seperateChaining3.insertChainHash(dataset[i],i);
            }
            for(int i=0; i< deleteDataset.length; i++) {
                seperateChaining3.deleteChainHash(deleteDataset[i]);
            }
        }

        String[] searchDataset2 = selectRandom2(dataset,deleteDataset);

        chainTime1 = linearTime = doubleTime = quadTime = chainTime2 = 0;

        //Setting ProbeCount To 0


        for(int i=0; i<searchDataset2.length; i++){
            long start,end;
            start = System.currentTimeMillis();
            seperateChaining.searchChainHash(searchDataset2[i]);
            end = System.currentTimeMillis();
            chainTime1 += end - start;

            if(seperateChaining3 != null){
                start = System.currentTimeMillis();
                seperateChaining.searchChainHash(searchDataset2[i]);
                end = System.currentTimeMillis();
                chainTime2 += end - start;
            }
        }

        System.out.println(chainTime1);
        System.out.println("Search Time Time Before Hashing: " + (double)chainTime1/(searchDataset2.length*1.0));
        System.out.println("Search Time Time After Hashing: " + (double)chainTime2/(searchDataset2.length*1.0));




       /* System.out.println("After Deletion.....");
        System.out.println(chainTime);
        System.out.println(linearTime);
        System.out.println(quadTime);
        System.out.println(doubleTime);*/
        return report;

    }

    public static void main(String[] args) {
        System.out.print("Enter Table Size: ");
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt();
        tablesize = N;
        System.out.print("Enter Load Factor: ");
        Double L = sc.nextDouble();
        generateReport(L);
    }
}
