import java.util.ArrayList;
import java.util.List;

class FibonacciHeapNode{
    int value;
    boolean mark;
    double key;
    int degree;
    FibonacciHeapNode parent;
    FibonacciHeapNode child;
    FibonacciHeapNode left;
    FibonacciHeapNode right;

    public FibonacciHeapNode(int value, double key){
        this.value = value;
        this.key = key;
        this.right = this;
        this.left = this;
        this.parent = null;
        this.child = null;
        this.degree = 0;
        this.mark = false;
    }

    public double getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }
}



public class FibonacciHeap {
    private int nodeCount;
    private FibonacciHeapNode minNode;
    private double oneByLogPhi = (1.0 / Math.log((1.0 + Math.sqrt(5.0))/2.0));

    public FibonacciHeap(){
        this.nodeCount = 0;
        this.minNode = null;
    }

    public void clear(){
        nodeCount = 0;
        minNode = null;
    }

    public boolean isEmpty(){
        return minNode == null;
    }

    public void insert(FibonacciHeapNode node){
        double key = node.key;
        if(minNode == null){
            minNode = node;
        }
        else{
            node.right = minNode;
            node.left = minNode.left;
            minNode.left = node;
            node.left.right = node;
            if(key < minNode.key){
                minNode = node;
            }
        }
        nodeCount++;
    }

    public FibonacciHeapNode minimum(){
        return minNode;
    }

    private void fibHeapLink(FibonacciHeapNode y, FibonacciHeapNode x){
        //Removing y from root node list
        y.left.right = y.right;
        y.right.left = y.left;

        //Adding y as a child of x
        y.parent = x;
        x.degree++;
        if(x.child == null){
            //If x doesn't have any child,then add y directly
            x.child = y;
            y.right = y;
            y.left = y;
        }
        else{
            //If x has already one/more children,y should be connected to that children list
            y.left = x.child;
            y.right = x.child.right;
            x.child.right = y;
            y.right.left = y;
        }
        y.mark = false;
    }


    private void consolidate(){
        int dArraySize = (int)(Math.floor(Math.log(nodeCount) * oneByLogPhi) + 1);
        List<FibonacciHeapNode> dArray = new ArrayList<FibonacciHeapNode>(dArraySize);

        for(int i=0; i<dArraySize; i++){
            dArray.add(null);
        }

        //Counting Root Nodes
        int rootNodeCount = 0;
        FibonacciHeapNode x = minNode;
        if(x!=null){
            rootNodeCount++;
            x = x.right;
            while(x!=minNode){
                rootNodeCount++;
                x = x.right;
            }
        }

        while(rootNodeCount > 0){
            int degree = x.degree;
            FibonacciHeapNode nextRoot = x.right;
            while(true){
                FibonacciHeapNode y = dArray.get(degree);
                if(y == null){
                    break;
                }

                if(x.key > y.key){
                    //swapping x and y
                    FibonacciHeapNode tmp = y;
                    y = x;
                    x = tmp;
                }

                fibHeapLink(y, x);
                dArray.set(degree, null);
                degree++;
            }
            //System.out.println("Mama ber hoisi");

            dArray.set(degree, x);
            x = nextRoot;
            rootNodeCount--;
        }

        //Reconstructing root list
        minNode = null;
        for(int i=0; i<dArraySize; i++){
            FibonacciHeapNode y = dArray.get(i);
            if(y==null)continue;
            if(minNode == null){
                minNode = y;
            }
            else{
                //Removing element from the root list for safety
                y.left.right = y.right;
                y.right.left = y.left;

                //Adding to the root list again
                y.right = minNode;
                y.left = minNode.left;
                minNode.left = y;
                y.left.right = y;

                //updating minNode if necessary
                if(y.key < minNode.key){
                    minNode = y;
                }
            }
        }
    }




    public FibonacciHeapNode extractMin(){
        FibonacciHeapNode z = minNode;
        if(z!=null){
            int childrenCount = z.degree;
            FibonacciHeapNode x = z.child;
            FibonacciHeapNode temp;

            //Removing All The Children
            while(childrenCount>0){
                temp = x.right;

                //Removing child x
                x.left.right = x.right;
                x.right.left = x.left;

                //Adding x to the root list
                x.right = minNode;
                x.left = minNode.left;
                minNode.left = x;
                x.left.right = x;

                //removing the parent of x
                x.parent = null;
                x = temp;
                childrenCount--;
            }

            //Removing z from the root list
            z.left.right = z.right;
            z.right.left = z.left;
            if(z == z.right){
                minNode = null;
            }
            else{
                minNode = z.right;
                consolidate();
            }

            nodeCount--;
        }
        return z;
    }

    public static FibonacciHeap union(FibonacciHeap H1, FibonacciHeap H2){
        FibonacciHeap H = new FibonacciHeap();
        if(H1 != null && H2 != null){
            H.minNode = H1.minNode;

            if(H.minNode != null){
                if(H2.minNode != null){
                    H.minNode.left.right = H2.minNode.right;
                    H2.minNode.right.left = H.minNode.left;
                    H.minNode.left = H2.minNode;
                    H2.minNode.right = H.minNode;

                    //Update Min Node If Necessary
                    if(H2.minNode.key < H.minNode.key){
                        H.minNode = H2.minNode;
                    }
                }
            }
            else{
                H.minNode = H2.minNode;
            }
            H.nodeCount = H1.nodeCount + H2.nodeCount;
        }
        return H;
    }

    private void cut(FibonacciHeapNode x, FibonacciHeapNode y){
        //Removing x from childlist of y
        x.left.right = x.right;
        x.right.left = x.left;
        y.degree--;
        if(y.child == x){
            y.child = x.right;
        }
        if(y.degree == 0){
            y.child = null;
        }

        //Adding x to the root node list
        x.right = minNode;
        x.left = minNode.left;
        minNode.left = x;
        x.left.right = x;
        x.parent = null;
        x.mark = false;
    }

    private void cascadingCut(FibonacciHeapNode y){
        FibonacciHeapNode z = y.parent;
        if(z!=null){
            if(!y.mark){
                y.mark = true;
            }
            else{
                cut(y, z);
                cascadingCut(z);
            }
        }
    }

    public void decreaseKey(FibonacciHeapNode x, double k){
        if(k > x.key){
            System.out.println("Key is larger than the original key ");
        }
        x.key = k;
        FibonacciHeapNode y = x.parent;
        if(y!=null && x.key < y.key){
            cut(x, y);
            cascadingCut(y);
        }
        if(x.key < minNode.key){
            minNode = x;
        }
    }

    public void delete(FibonacciHeapNode x){
        decreaseKey(x, -999999);
        extractMin();
    }

}
