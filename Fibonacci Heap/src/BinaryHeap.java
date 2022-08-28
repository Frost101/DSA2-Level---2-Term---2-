import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class BinaryHeapNode{
    int value;
    double key;

    public BinaryHeapNode(int value, double key){
        this.value = value;
        this.key = key;
    }
}

public class BinaryHeap {
    private ArrayList<BinaryHeapNode> heapArray;
    private int hSize;
    private Map<BinaryHeapNode, Integer> heapIndex = new HashMap<>();

    public BinaryHeap(){
        this.heapArray = new ArrayList<BinaryHeapNode>();
        this.hSize = 0;
    }

    private int leftChild(int i){
        return (2*i)+1;
    }

    private int rightChild(int i){
        return (2*i)+2;
    }

    private int parent(int i){
        return (i-1)/2;
    }

    public boolean isEmpty(){
        return hSize == 0;
    }

    public BinaryHeapNode minimum(){
        return heapArray.get(0);
    }

    public void insert(BinaryHeapNode node){
        heapArray.add(node);
        this.hSize++;
        int temp = this.hSize-1;
        heapIndex.put(node,temp);

        //Bubble Up
        while(temp!=0 && heapArray.get(parent(temp)).key > heapArray.get(temp).key){
            BinaryHeapNode t = heapArray.get(parent(temp));
            heapArray.set(parent(temp), heapArray.get(temp));
            heapIndex.replace(heapArray.get(temp),parent(temp));
            heapArray.set(temp,t);
            heapIndex.replace(t,temp);

            temp = parent(temp);
        }
    }

    private void minHeapify(int i){
        int l = leftChild(i);
        int r = rightChild(i);
        int smallest = i;

        if(l<hSize && heapArray.get(l).key < heapArray.get(smallest).key){
            smallest = l;
        }
        if(r<hSize && heapArray.get(r).key < heapArray.get(smallest).key){
            smallest = r;
        }
        if(smallest != i){
            BinaryHeapNode t = heapArray.get(i);
            heapArray.set(i, heapArray.get(smallest));
            heapIndex.replace(heapArray.get(smallest),i);
            heapArray.set(smallest,t);
            heapIndex.replace(t,smallest);
            minHeapify(smallest);
        }
    }

    public BinaryHeapNode extractMin(){
        if(hSize == 0)return null;

        BinaryHeapNode t = heapArray.get(0);
        heapArray.set(0, heapArray.get(hSize-1));
        heapIndex.replace(heapArray.get(hSize-1),0);
        heapArray.set(hSize-1,t);

        t = heapArray.get(hSize-1);
        hSize--;
        heapIndex.remove(t);
        heapArray.remove(hSize);

        if(hSize != 0)minHeapify(0);
        return t;
    }

    public void decreaseKey(BinaryHeapNode node, double key){
        if(key > node.key){
            System.out.println("Key is larger than the original key");
            return;
        }

        int i =  heapIndex.get(node);
        heapArray.get(i).key = key;

        //Bubble Up
        while(i!=0 && heapArray.get(parent(i)).key > heapArray.get(i).key){
            BinaryHeapNode t = heapArray.get(parent(i));
            heapArray.set(parent(i), heapArray.get(i));
            heapIndex.replace(heapArray.get(i),parent(i));
            heapArray.set(i,t);
            heapIndex.replace(t,i);

            i = parent(i);
        }

    }







}
