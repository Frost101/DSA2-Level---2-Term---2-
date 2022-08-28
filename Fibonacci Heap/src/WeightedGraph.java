import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Node{
    int value;
    int key;

    public Node(int value,int key){
        this.value = value;
        this.key = key;
    }
}

public class WeightedGraph{
    private int V;
    private List<List<Node>> adjlist;
    private boolean processed[];
    private int parents[];
    private FibonacciHeapNode distanceFib[];
    private BinaryHeapNode distanceBin[];

    public WeightedGraph(int V){
        this.V = V;
        processed = new boolean[V];
        parents = new int[V];
        distanceFib = new FibonacciHeapNode[V];
        distanceBin = new BinaryHeapNode[V];
        adjlist = new ArrayList<List<Node>>();
        for(int i=0; i<V; i++){
            List<Node> list = new ArrayList<Node>();
            adjlist.add(list);
            distanceFib[i] = new FibonacciHeapNode(i,Integer.MAX_VALUE);
            distanceBin[i] = new BinaryHeapNode(i, Integer.MAX_VALUE);
        }
    }

    private void initializeFib(){
        for (int i=0;i<V;i++){
            distanceFib[i] = new FibonacciHeapNode(i,Integer.MAX_VALUE);
        }
        for (int i=0;i<V;i++){
            processed[i] = false;
        }
        for (int i=0;i<V;i++){
            parents[i] = -1;
        }
    }

    private void initializeBin(){
        for (int i=0;i<V;i++){
            distanceBin[i] = new BinaryHeapNode(i,Integer.MAX_VALUE);
        }
        for (int i=0;i<V;i++){
            processed[i] = false;
        }
        for (int i=0;i<V;i++){
            parents[i] = -1;
        }
    }

    public int dijkstraFibHeap(int src, int destination){
        initializeFib();
        FibonacciHeap fibonacciHeap = new FibonacciHeap();
        for(int i=0; i<V; i++){
            if(i==src)distanceFib[i].key = 0;
            fibonacciHeap.insert(distanceFib[i]);
        }

        while (!fibonacciHeap.isEmpty()){
            int u = fibonacciHeap.minimum().value;
            int w = (int)fibonacciHeap.minimum().key;
            fibonacciHeap.extractMin();

            if(processed[u]) continue;
            else processed[u] = true;
            for(int i=0; i<adjlist.get(u).size(); i++){
                int v = adjlist.get(u).get(i).value;
                int dist = adjlist.get(u).get(i).key;


                if(!processed[v] && w!= Integer.MAX_VALUE && w+dist < (int)distanceFib[v].key){
                    parents[v] = u;
                    fibonacciHeap.decreaseKey(distanceFib[v],w+dist);
                    distanceFib[v].key = w+dist;
                }
            }
        }

        return (int)distanceFib[destination].key;
    }

    public int dijkstraBinHeap(int src, int destination){
        initializeBin();
        BinaryHeap binaryHeap = new BinaryHeap();
        for(int i=0; i<V; i++){
            if(i==src)distanceBin[i].key = 0;
            binaryHeap.insert(distanceBin[i]);
        }

        while (!binaryHeap.isEmpty()){
            int u = binaryHeap.minimum().value;
            int w = (int)binaryHeap.minimum().key;
            binaryHeap.extractMin();

            if(processed[u]) continue;
            else processed[u] = true;
            for(int i=0; i<adjlist.get(u).size(); i++){
                int v = adjlist.get(u).get(i).value;
                int dist = adjlist.get(u).get(i).key;


                if(!processed[v] && w!= Integer.MAX_VALUE && w+dist < (int)distanceBin[v].key){
                    parents[v] = u;
                    binaryHeap.decreaseKey(distanceBin[v],w+dist);
                    distanceBin[v].key = w+dist;
                }
            }
        }

        return (int)distanceBin[destination].key;
    }

    public int pathLength(int src,int dest){
        dijkstraBinHeap(src,dest);
        int cnt = 0;
        while(dest != src){
            if(dest==-1){
                cnt=0;
                break;
            }
            dest = parents[dest];
            cnt++;
        }
        return cnt;
    }

    public void addEdge(int u, int v, int w){
        adjlist.get(u).add(new Node(v,w));
    }


}

class main{
    public static void main(String[] args) {
        File file = new File("input1.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String data = sc.nextLine();
        String[] strings = data.split(" ");
        int n,m;
        n = Integer.parseInt(strings[0]);
        m = Integer.parseInt(strings[1]);
        WeightedGraph graph = new WeightedGraph(n);
        for(int i=0; i<m; i++){
            int u,v;
            int w;
            data = sc.nextLine();
            strings = data.split(" ");
            u = Integer.parseInt(strings[0]);
            v = Integer.parseInt(strings[1]);
            w = Integer.parseInt(strings[2]);
            graph.addEdge(u,v,w);
            graph.addEdge(v,u,w);
        }
        sc.close();
        file = new File("input2.txt");
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        data = sc.nextLine();
        strings = data.split(" ");
        int k = Integer.parseInt(strings[0]);

        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            myWriter.write("Path_Length   Cost   Fib_Heap     Binary_Heap \n");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        for (int i=0; i<k; i++){
            data = sc.nextLine();
            strings = data.split(" ");
            int s,t;
            s = Integer.parseInt(strings[0]);
            t = Integer.parseInt(strings[1]);

            long startFib, endFib, startBin,endBin;
            int costFib, costBin;
            int pathLength;

            startFib = System.nanoTime();
            costFib = graph.dijkstraFibHeap(s,t);
            endFib = System.nanoTime();

            startBin = System.nanoTime();
            costBin = graph.dijkstraBinHeap(s,t);
            endBin = System.nanoTime();

            pathLength = graph.pathLength(s,t);

            try {
                myWriter.write(pathLength+" "+costFib+" "+costBin+" "+(endFib-startFib)+" "+(endBin-startBin)+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
