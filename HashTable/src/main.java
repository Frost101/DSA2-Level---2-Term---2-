import java.io.FileWriter;
import java.io.IOException;
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

    public Report(){

    }

}

public class main {
    static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    static  int tablesize;

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
        HashTable linearProbing = new HashTable(tablesize);
        HashTable quadraticProbing = new HashTable(tablesize);
        HashTable doubleHashing = new HashTable(tablesize);

        //Inserting
        for(int i=0; i<datasetSize; i++){
            seperateChaining.insertChainHash(dataset[i],i);
            linearProbing.insertLinearProbingHash(dataset[i],i);
            quadraticProbing.insertQuadraticProbingHash(dataset[i],i);
            doubleHashing.insertDoubleHash(dataset[i],i);
        }

        String[] searchDataset = selectRandom(dataset);
        long chainTime=0,linearTime=0,quadTime=0,doubleTime=0;

        //Setting ProbeCount To 0
        linearProbing.setProbeCount(0);
        quadraticProbing.setProbeCount(0);
        doubleHashing.setProbeCount(0);

        for(int i=0; i<searchDataset.length; i++){
            long start,end;
            start = System.currentTimeMillis();
            seperateChaining.searchChainHash(searchDataset[i]);
            end = System.currentTimeMillis();
            chainTime += end - start;

            start = System.currentTimeMillis();
            linearProbing.searchLinearProbingHash(searchDataset[i]);
            end = System.currentTimeMillis();
            linearTime += end - start;

            start = System.currentTimeMillis();
            quadraticProbing.searchQuadraticProbingHash(searchDataset[i]);
            end = System.currentTimeMillis();
            quadTime += end - start;

            start = System.currentTimeMillis();
            doubleHashing.searchDoubleHash(searchDataset[i]);
            end = System.currentTimeMillis();
            doubleTime += end - start;
        }
        /*
        System.out.println("Before Deletion.....");
        System.out.println(chainTime);
        System.out.println(linearTime);
        System.out.println(quadTime);
        System.out.println(doubleTime);*/

        report.avgTimeChainingB = chainTime/(searchDataset.length * 1.0);
        report.avgTimeLinearB = linearTime/(searchDataset.length * 1.0);
        report.avgTimeQuadB = quadTime/(searchDataset.length * 1.0);
        report.avgTimeDoubleB = doubleTime/(searchDataset.length * 1.0);

        report.probeLinearB = linearProbing.getProbeCount()/ searchDataset.length;
        report.probeQuadB = quadraticProbing.getProbeCount()/ searchDataset.length;
        report.probeDoubleB = doubleHashing.getProbeCount()/ searchDataset.length;

        String[] deleteDataset = selectRandom(dataset);
        //Deleting
        for(int i=0; i< deleteDataset.length; i++){
            seperateChaining.deleteChainHash(deleteDataset[i]);
            linearProbing.deleteLinearProbingHash(deleteDataset[i]);
            quadraticProbing.deleteQuadraticProbingHash(deleteDataset[i]);
            doubleHashing.deleteDoubleHash(deleteDataset[i]);
        }

        String[] searchDataset2 = selectRandom2(dataset,deleteDataset);

        chainTime = linearTime = doubleTime = quadTime = 0;

        //Setting ProbeCount To 0
        linearProbing.setProbeCount(0);
        quadraticProbing.setProbeCount(0);
        doubleHashing.setProbeCount(0);

        for(int i=0; i<searchDataset2.length; i++){
            long start,end;
            start = System.currentTimeMillis();
            seperateChaining.searchChainHash(searchDataset2[i]);
            end = System.currentTimeMillis();
            chainTime += end - start;

            start = System.currentTimeMillis();
            linearProbing.searchLinearProbingHash(searchDataset2[i]);
            end = System.currentTimeMillis();
            linearTime += end - start;

            start = System.currentTimeMillis();
            quadraticProbing.searchQuadraticProbingHash(searchDataset2[i]);
            end = System.currentTimeMillis();
            quadTime += end - start;

            start = System.currentTimeMillis();
            doubleHashing.searchDoubleHash(searchDataset2[i]);
            end = System.currentTimeMillis();
            doubleTime += end - start;
        }

        report.avgTimeChainingA = chainTime/(searchDataset2.length * 1.0);
        report.avgTimeLinearA = linearTime/(searchDataset2.length * 1.0);
        report.avgTimeQuadA = quadTime/(searchDataset2.length * 1.0);
        report.avgTimeDoubleA = doubleTime/(searchDataset2.length * 1.0);

        report.probeLinearA = linearProbing.getProbeCount()/ searchDataset2.length;
        report.probeQuadA = quadraticProbing.getProbeCount()/ searchDataset2.length;
        report.probeDoubleA = doubleHashing.getProbeCount()/ searchDataset2.length;

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
        System.out.println();
        generateReport(0.9);
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("output.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Report reportLoad4 = generateReport(0.4);
        Report reportLoad5 = generateReport(0.5);
        Report reportLoad6 = generateReport(0.6);
        Report reportLoad7 = generateReport(0.7);
        Report reportLoad8 = generateReport(0.8);
        Report reportLoad9 = generateReport(0.9);

        try {
            myWriter.write(",Seperate Chaining"+"\n");
            myWriter.write(",Before Deletion,After Deletion"+"\n");
            myWriter.write("Load Factor,Avg search time,Avg Search Time"+"\n");
            myWriter.write("0.4,"+reportLoad4.avgTimeChainingB+","+reportLoad4.avgTimeChainingA+"\n");
            myWriter.write("0.5,"+reportLoad5.avgTimeChainingB+","+reportLoad5.avgTimeChainingA+"\n");
            myWriter.write("0.6,"+reportLoad6.avgTimeChainingB+","+reportLoad6.avgTimeChainingA+"\n");
            myWriter.write("0.7,"+reportLoad7.avgTimeChainingB+","+reportLoad7.avgTimeChainingA+"\n");
            myWriter.write("0.8,"+reportLoad8.avgTimeChainingB+","+reportLoad8.avgTimeChainingA+"\n");
            myWriter.write("0.9,"+reportLoad9.avgTimeChainingB+","+reportLoad9.avgTimeChainingA+"\n");
            myWriter.write("\n\n");


            myWriter.write(",Linear Probing"+"\n");
            myWriter.write(",Before Deletion,,After Deletion,"+"\n");
            myWriter.write("Load Factor,Avg search time,Avg Number of probes,Avg Search Time,Avg number of probes"+"\n");
            myWriter.write("0.4,"+reportLoad4.avgTimeLinearB+","+reportLoad4.probeLinearB+","+reportLoad4.avgTimeLinearA+","+reportLoad4.probeLinearA+"\n");
            myWriter.write("0.5,"+reportLoad5.avgTimeLinearB+","+reportLoad5.probeLinearB+","+reportLoad5.avgTimeLinearA+","+reportLoad5.probeLinearA+"\n");
            myWriter.write("0.6,"+reportLoad6.avgTimeLinearB+","+reportLoad6.probeLinearB+","+reportLoad6.avgTimeLinearA+","+reportLoad6.probeLinearA+"\n");
            myWriter.write("0.7,"+reportLoad7.avgTimeLinearB+","+reportLoad7.probeLinearB+","+reportLoad7.avgTimeLinearA+","+reportLoad7.probeLinearA+"\n");
            myWriter.write("0.8,"+reportLoad8.avgTimeLinearB+","+reportLoad8.probeLinearB+","+reportLoad8.avgTimeLinearA+","+reportLoad8.probeLinearA+"\n");
            myWriter.write("0.9,"+reportLoad9.avgTimeLinearB+","+reportLoad9.probeLinearB+","+reportLoad9.avgTimeLinearA+","+reportLoad9.probeLinearA+"\n");
            myWriter.write("\n\n");

            myWriter.write(",Quadratic Probing"+"\n");
            myWriter.write(",Before Deletion,,After Deletion,"+"\n");
            myWriter.write("Load Factor,Avg search time,Avg Number of probes,Avg Search Time,Avg number of probes"+"\n");
            myWriter.write("0.4,"+reportLoad4.avgTimeQuadB+","+reportLoad4.probeQuadB+","+reportLoad4.avgTimeQuadA+","+reportLoad4.probeQuadA+"\n");
            myWriter.write("0.5,"+reportLoad5.avgTimeQuadB+","+reportLoad5.probeQuadB+","+reportLoad5.avgTimeQuadA+","+reportLoad5.probeQuadA+"\n");
            myWriter.write("0.6,"+reportLoad6.avgTimeQuadB+","+reportLoad6.probeQuadB+","+reportLoad6.avgTimeQuadA+","+reportLoad6.probeQuadA+"\n");
            myWriter.write("0.7,"+reportLoad7.avgTimeQuadB+","+reportLoad7.probeQuadB+","+reportLoad7.avgTimeQuadA+","+reportLoad7.probeQuadA+"\n");
            myWriter.write("0.8,"+reportLoad8.avgTimeQuadB+","+reportLoad8.probeQuadB+","+reportLoad8.avgTimeQuadA+","+reportLoad8.probeQuadA+"\n");
            myWriter.write("0.9,"+reportLoad9.avgTimeQuadB+","+reportLoad9.probeQuadB+","+reportLoad9.avgTimeQuadA+","+reportLoad9.probeQuadA+"\n");
            myWriter.write("\n\n");

            myWriter.write(",Double Hashing"+"\n");
            myWriter.write(",Before Deletion,,After Deletion,"+"\n");
            myWriter.write("Load Factor,Avg search time,Avg Number of probes,Avg Search Time,Avg number of probes"+"\n");
            myWriter.write("0.4,"+reportLoad4.avgTimeDoubleB+","+reportLoad4.probeDoubleB+","+reportLoad4.avgTimeDoubleA+","+reportLoad4.probeDoubleA+"\n");
            myWriter.write("0.5,"+reportLoad5.avgTimeDoubleB+","+reportLoad5.probeDoubleB+","+reportLoad5.avgTimeDoubleA+","+reportLoad5.probeDoubleA+"\n");
            myWriter.write("0.6,"+reportLoad6.avgTimeDoubleB+","+reportLoad6.probeDoubleB+","+reportLoad6.avgTimeDoubleA+","+reportLoad6.probeDoubleA+"\n");
            myWriter.write("0.7,"+reportLoad7.avgTimeDoubleB+","+reportLoad7.probeDoubleB+","+reportLoad7.avgTimeDoubleA+","+reportLoad7.probeDoubleA+"\n");
            myWriter.write("0.8,"+reportLoad8.avgTimeDoubleB+","+reportLoad8.probeDoubleB+","+reportLoad8.avgTimeDoubleA+","+reportLoad8.probeDoubleA+"\n");
            myWriter.write("0.9,"+reportLoad9.avgTimeDoubleB+","+reportLoad9.probeDoubleB+","+reportLoad9.avgTimeDoubleA+","+reportLoad9.probeDoubleA+"\n");
            myWriter.write("\n\n");

            for(int i = 4; i<=9; i+=1){
                Report report;
                if(i==4)report = reportLoad4;
                else if(i==5)report = reportLoad5;
                else if(i==6)report = reportLoad6;
                else if(i==7)report = reportLoad7;
                else if(i==8)report = reportLoad8;
                else report = reportLoad9;
                myWriter.write(",Load Factor 0."+i+"\n");
                myWriter.write(",Before Deletion,,After Deletion,"+"\n");
                myWriter.write("Method,Avg search time,Avg Number of probes,Avg Search Time,Avg number of probes"+"\n");
                myWriter.write("Seperate Chaining,"+report.avgTimeChainingB+",N/A,"+report.avgTimeChainingA+",N/A"+"\n");
                myWriter.write("Linear Probing,"+report.avgTimeLinearB+","+report.probeLinearB+","+report.avgTimeLinearA+","+report.probeLinearA+"\n");
                myWriter.write("Quadratic Probing,"+report.avgTimeQuadB+","+report.probeQuadB+","+report.avgTimeQuadA+","+report.probeQuadA+"\n");
                myWriter.write("Double Hashing,"+report.avgTimeDoubleB+","+report.probeDoubleB+","+report.avgTimeDoubleA+","+report.probeDoubleA+"\n");
                myWriter.write("\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
