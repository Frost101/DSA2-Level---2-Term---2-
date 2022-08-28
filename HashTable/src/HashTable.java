import java.util.ArrayList;
import java.util.List;

class Node{
    public String key;
    public int value;

    Node(){
        key = "";
        value = Integer.MIN_VALUE;
    }

    Node(String key, int value){
        this.key = key;
        this.value = value;
    }
}

public class HashTable {
    private int size;
    private int probeCount;
    private List<List<Node>> table;
    private boolean[] tracker;
    int prevPrime;


    HashTable(int size){
        this.size = size;
        this.probeCount = 0;
        this.prevPrime = previousPrime(size);
        tracker = new boolean[size];
        for(int i=0; i<size; i++){
            tracker[i] = false;
        }
        table = new ArrayList<List<Node>>();
        for(int i=0; i<size; i++){
            List<Node> list = new ArrayList<Node>();
            table.add(list);
        }
    }

    public void setProbeCount(int probeCount) {
        this.probeCount = probeCount;
    }



    public int getProbeCount() {
        return probeCount;
    }

    boolean isPrime(long x){
        if(x == 2 || x == 3) return true;
        if(x == 1 || x %2 == 0) return false;
        for(int i=3; i*i <= x; i+=2){
            if(x%i==0) return false;
        }
        return true;
    }

    int previousPrime(int x){
        x-=2;
        while(!isPrime(x)){
            x-=2;
        }
        return x;
    }

    //Hash Function 1 : Polynomial Rolling Hash Function
    long H1(String key){
        long hash = 0;
        int PRC = 61;
        for(int i=0; i<key.length(); i++){
            int tmp = key.charAt(i);
            hash += (long)(tmp * Math.pow(PRC,i));
        }
        return hash;
    }

    /*long H1(String key){
        long hash = 0;
        long FNV_PRIME = 16777619;
        for(int i=0; i<key.length(); i++){
            hash += (hash * FNV_PRIME);
            hash ^= key.charAt(i);
        }
        return hash;
    }*/

    //Hash Function 2 : For double hash,Power Hash Function
    long H2(String key){
        long hash = 0;
        for(int i=0; i<key.length(); i++){
            hash += (long)Math.pow(key.charAt(i),i);
        }
        hash = prevPrime - (hash % prevPrime);
        return hash;
    }

    long linearProbingHash(String key, int i){
        long hash = 0;
        hash = (H1(key) + i) % size;
        return hash;
    }

    long quadraticProbingHash(String key, int i){
        long hash = 0;
        int C1 = 2, C2 = 3;
        hash = (H1(key) + C1 * i + C2 * i * i ) % size;
        return hash;
    }

    long doubleHash(String key, int i){
        long hash = 0;
        hash = (H1(key) + i * H2(key)) % size;
        return hash;
    }

    //Seperate Chaining Method
    void insertChainHash(String key, int value){
        int index = (int) (H1(key) % size);
        int listSize = table.get(index).size();
        table.get(index).add(new Node(key,value));
    }

    int searchChainHash(String key){
        int index = (int) (H1(key) % size);
        for(int i=0; i < table.get(index).size(); i++){
            if(table.get(index).get(i).key.equalsIgnoreCase(key)){
                return table.get(index).get(i).value;
            }
            probeCount++;
        }
        return Integer.MIN_VALUE;
    }

    void deleteChainHash(String key){
        if(searchChainHash(key) == Integer.MIN_VALUE) {
            System.out.println("The key is not present in the hash table");
            return;
        }
        int index = (int) (H1(key) % size);
        for(int i=0; i < table.get(index).size(); i++){
            if(table.get(index).get(i).key.equalsIgnoreCase(key)){
                table.get(index).remove(i);
            }
        }
    }

    //Linear Probing Method
    void insertLinearProbingHash(String key, int value){
        int index = 0;
        int i = 0;
        while (i<size){
            index = (int) linearProbingHash(key, i);
            if(table.get(index).size()==0){
                table.get(index).add(new Node(key,value));
                tracker[index] = true;
                break;
            }

            i++;
        }
    }

    int searchLinearProbingHash(String key){
        int i = 0;
        int index = (int) linearProbingHash(key,i);
        if(table.get(index).isEmpty() && !tracker[index]){
            return Integer.MIN_VALUE;
        }
        while (i<size){

            /*if(table.get(index).isEmpty() && tracker[index]){
                i++;
                index = (int) linearProbingHash(key,i);
                continue;
            }*/
            if(!table.get(index).isEmpty() && table.get(index).get(0).key.equalsIgnoreCase(key)){
                return table.get(index).get(0).value;
            }
            i++;
            probeCount++;
            index = (int) linearProbingHash(key,i);
            if(table.get(index).isEmpty() && !tracker[index]){
                return Integer.MIN_VALUE;
            }
        }
        return Integer.MIN_VALUE;
    }

    void deleteLinearProbingHash(String key){
        int index = 0;
        int i = 0;
        while (i<size){
            index = (int) linearProbingHash(key, i);
            if(table.get(index).isEmpty() && !tracker[index]){
                System.out.println("The key doesn't exist in the hash table");
                break;
            }
            else {
                if(table.get(index).size()!=0){
                    if(table.get(index).get(0).key.equalsIgnoreCase(key)){
                        table.get(index).remove(0);
                        break;
                    }
                }
                i++;
            }
        }
    }


    //Quadrating Probing Method
    void insertQuadraticProbingHash(String key, int value){
        int index = 0;
        int i = 0;
        while (i<size){
            index = (int) quadraticProbingHash(key,i);
            if(table.get(index).size()==0){
                table.get(index).add(new Node(key,value));
                tracker[index] = true;
                break;
            }

            i++;
        }
    }

    int searchQuadraticProbingHash(String key){
        int i = 0;
        int index = (int) quadraticProbingHash(key, i);
        if(table.get(index).isEmpty() && !tracker[index]){
            return Integer.MIN_VALUE;
        }
        while (i<size){

            /*if(table.get(index).isEmpty() && tracker[index]){
                i++;
                index = (int) linearProbingHash(key,i);
                continue;
            }*/
            if(!table.get(index).isEmpty() && table.get(index).get(0).key.equalsIgnoreCase(key)){
                return table.get(index).get(0).value;
            }
            i++;
            probeCount++;
            index = (int) quadraticProbingHash(key,i);
            if(table.get(index).isEmpty() && !tracker[index]){
                return Integer.MIN_VALUE;
            }
        }
        return Integer.MIN_VALUE;
    }

    void deleteQuadraticProbingHash(String key){
        int index = 0;
        int i = 0;
        while (i<size){
            index = (int) quadraticProbingHash(key, i);
            if(table.get(index).isEmpty() && !tracker[index]){
                System.out.println("The key doesn't exist in the hash table");
                break;
            }
            else {
                if(table.get(index).size()!=0){
                    if(table.get(index).get(0).key.equalsIgnoreCase(key)){
                        table.get(index).remove(0);
                        break;
                    }
                }
                i++;
            }
        }
    }


    //Double Hashing Method
    void insertDoubleHash(String key, int value){
        int index = 0;
        int i = 0;
        while (i<size){
            index = (int) doubleHash(key,i);
            if(table.get(index).size()==0){
                table.get(index).add(new Node(key,value));
                tracker[index] = true;
                break;
            }

            i++;
        }
    }

    int searchDoubleHash(String key){
        int i = 0;
        int index = (int) doubleHash(key, i);
        if(table.get(index).isEmpty() && !tracker[index]){
            return Integer.MIN_VALUE;
        }
        while (i<size){
            /*if(table.get(index).isEmpty() && tracker[index]){
                i++;
                index = (int) linearProbingHash(key,i);
                continue;
            }*/
            if(!table.get(index).isEmpty() && table.get(index).get(0).key.equalsIgnoreCase(key)){
                return table.get(index).get(0).value;
            }
            i++;
            probeCount++;
            index = (int) doubleHash(key,i);
            if(table.get(index).isEmpty() && !tracker[index]){
                return Integer.MIN_VALUE;
            }
        }
        return Integer.MIN_VALUE;
    }

    void deleteDoubleHash(String key){
        int index = 0;
        int i = 0;
        while (i<size){
            index = (int) doubleHash(key, i);
            if(table.get(index).isEmpty() && !tracker[index]){
                System.out.println("The key doesn't exist in the hash table");
                break;
            }
            else {
                if(table.get(index).size()!=0){
                    if(table.get(index).get(0).key.equalsIgnoreCase(key)){
                        table.get(index).remove(0);
                        break;
                    }
                }
                i++;
            }
        }
    }








}
