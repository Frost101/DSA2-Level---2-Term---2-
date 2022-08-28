import java.util.Comparator;
import java.util.PriorityQueue;

class Node{
    String[][] matrix;
    int fixedColumnCount;
    int fixedRowCount;
    int lowerBound;
    int level;
    int order;
    int size;

    public Node(String[][] matrix, int size, int fixedRowCount, int fixedColumnCount, int level, int order){
        this.size = size;
        String[][] tmp = new String[size][size];
        this.matrix = tmp;
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                this.matrix[i][j] = matrix[i][j];
            }
        }
        this.fixedColumnCount = fixedColumnCount;
        this.fixedRowCount = fixedRowCount;
        this.level = level;
        this.order = order;
        this.lowerBound = BandMatrix.getLowerBound(matrix,fixedRowCount,fixedColumnCount,size);
        //System.out.println(this.lowerBound);
    }

    void print(){
        for(int i=0;i<size;i++){
            for(int j=0; j<size; j++){
                System.out.print(matrix[i][j]+" ");
            }
            System.out.println();
        }
    }

    Comparator<Node> priority = new Comparator<Node>() {
        @Override
        public int compare(Node node1, Node node2) {
            if(node1.lowerBound < node2.lowerBound){
                return 1;
            }
            else if(node1.lowerBound == node2.lowerBound && node1.level > node2.level){
                return 1;
            }
            else if(node1.lowerBound == node2.lowerBound && node1.level == node2.level && node1.order > node2.order){
                return 1;
            }
            return 1;
        }
    };
}

//class Priority implements Comparator<Node> {
//    @Override
//    public int compare(Node node1, Node node2) {
//       if(node1.lowerBound < node2.lowerBound){
//           return -1;
//       }
//       else if(node1.lowerBound == node2.lowerBound && node1.level > node2.level){
//           return -1;
//       }
//       else if(node1.lowerBound == node2.lowerBound && node1.level == node2.level && node1.order > node2.order){
//           return -1;
//       }
//       return -1;
//    }
//}

public class BandMatrix {
    private String[][] matrix;
    private int size;

    public BandMatrix(String[][] matrix, int size){
        this.matrix = matrix;
        this.size = size;
    }

    Comparator<Node> Priority = new Comparator<Node>() {
        @Override
        public int compare(Node node1, Node node2) {
            if(node1.lowerBound < node2.lowerBound){
           return -1;
       }
       else if(node1.lowerBound == node2.lowerBound && node1.level > node2.level){
           return -1;
       }
       else if(node1.lowerBound == node2.lowerBound && node1.level == node2.level && node1.order > node2.order){
           return -1;
       }
       return 1;
        }
    };

    public void columnSwap(String[][] matrix, int c1, int c2){
        for(int i = (c2-1); i>(c1-1); i--){
            for(int j=0; j<size; j++){
                //Swap
                String temp = matrix[j][i];
                matrix[j][i] = matrix[j][i-1];
                matrix[j][i-1] = temp;
            }
        }
    }

    public void rowSwap(String[][] matrix, int r1, int r2){
        for(int i = r2-1 ; i > (r1-1); i--){
            for(int j=0; j<size; j++){
                String temp = matrix[i][j];
                matrix[i][j] = matrix[i-1][j];
                matrix[i-1][j] = temp;
            }
        }
    }

    public int getBandwidth(String[][] matrix){
        int bandwidth = 0;
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                if(matrix[i][j].equalsIgnoreCase("X"))bandwidth = Math.max(bandwidth, Math.abs(i-j)+1);
            }
        }
        return bandwidth;
    }

    public static int getLowerBoundFixed(String[][] matrix, int r, int c, int size){
        int lowerBoundFixed = 0;

        //For columns
        for(int i=0; i<c; i++){
            int tempF = 0;
            int tempU = r;
            for(int j=0; j<size; j++){
                if(j<r){
                    //Within Fixed Portion
                    if(matrix[j][i].equalsIgnoreCase("X"))tempF = Math.max(tempF, Math.abs(i-j)+1);
                }
                else{
                    //Within Unfixed Portion
                    if(matrix[j][i].equalsIgnoreCase("X"))tempU++;
                }
            }

            if(tempU!=r){
                lowerBoundFixed = Math.max(lowerBoundFixed, Math.abs((i+1)-tempU)+1);
            }
            else {
                lowerBoundFixed = Math.max(lowerBoundFixed, tempF);
            }
        }



        //For Rows
        for(int i=0; i<r; i++){
            int tempF = 0;
            int tempU = c;
            for(int j=0; j<size; j++){
                if(j<c){
                    //Within Fixed Portion
                    if(matrix[i][j].equalsIgnoreCase("X"))tempF = Math.max(tempF, Math.abs(i-j)+1);
                }
                else{
                    //Within Unfixed Portion
                    if(matrix[i][j].equalsIgnoreCase("X"))tempU++;
                }
            }

            if(tempU!=c){
                lowerBoundFixed = Math.max(lowerBoundFixed, Math.abs((i+1)-tempU)+1);
            }
            else{
                lowerBoundFixed = Math.max(lowerBoundFixed,tempF);
            }
        }

        return lowerBoundFixed;
    }


    public static int getLowerBoundUnfixed(String[][] matrix, int r, int c, int size){
        int count = 0;
        for(int i = r; i < size; i++){
            int temp = 0;
            for(int j = c; j<size; j++){
                if(matrix[i][j].equalsIgnoreCase("X")){
                    temp++;
                }
            }
            count = Math.max(count,temp);
        }

        for(int i = c ; i < size; i++){
            int temp = 0;
            for(int j = r; j < size; j++){
                if(matrix[j][i].equalsIgnoreCase("X")){
                    temp++;
                }
            }
            count = Math.max(count,temp);
        }

        count = ((count+1)/2);
        return count;
    }

    public static int getLowerBound(String[][] matrix, int r, int c, int size){
        return Math.max(getLowerBoundFixed(matrix, r, c, size), getLowerBoundUnfixed(matrix, r, c, size));
    }

    public void getMinimumBandMatrix(String[][] matrix, int size){
        int minimumBand = getBandwidth(matrix);
        String[][] ansMatrix = new String[size][size];
        PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>(Priority);
        priorityQueue.add(new Node(matrix, size,0,0,0,0));
        while(!priorityQueue.isEmpty()){
            Node minNode = priorityQueue.remove();
//            System.out.println(minNode.lowerBound+" "+minNode.level+" "+minNode.order);
//            minNode.print();
//            System.out.println("------------------------------");

            if(minNode.fixedRowCount == size - 1){
                if(minNode.lowerBound < minimumBand){
                    ansMatrix = minNode.matrix;
                    for(int k = 0; k<size; k++){
                        for(int l = 0; l<size; l++){
                            ansMatrix[k][l] = minNode.matrix[k][l];
                        }
                    }
                    minimumBand = getBandwidth(ansMatrix);
                }
            }
            else if(minNode.fixedColumnCount == minNode.fixedRowCount){
                String[][] tempMatrix = new String[size][size];
                int minLowerBound = size;
                int fixedColumnCount = minNode.fixedColumnCount + 1;
                int fixedRowCount = minNode.fixedRowCount;
                int level = minNode.level + 1;
                for(int i = fixedColumnCount; i<=size; i++){
                    for(int k = 0; k<size; k++){
                        for(int l = 0; l<size; l++){
                            tempMatrix[k][l] = minNode.matrix[k][l];
                        }
                    }
                    columnSwap(tempMatrix, fixedColumnCount, i);
                    minLowerBound = Math.min(minLowerBound, getLowerBound(tempMatrix, fixedRowCount, fixedColumnCount, size));
                }
                for(int i=fixedColumnCount; i<=size; i++){
                    for(int k = 0; k<size; k++){
                        for(int l = 0; l<size; l++){
                            tempMatrix[k][l] = minNode.matrix[k][l];
                        }
                    }
                    columnSwap(tempMatrix, fixedColumnCount, i);

                    if(getLowerBound(tempMatrix, fixedRowCount, fixedColumnCount, size) == minLowerBound){
                        priorityQueue.add(new Node(tempMatrix,size,fixedRowCount,fixedColumnCount,level,i));
                    }
                }
            }
            else{
                String[][] tempMatrix = new String[size][size];
                int minLowerBound = size;
                int fixedColumnCount = minNode.fixedColumnCount;
                int fixedRowCount = minNode.fixedRowCount + 1;
                int level = minNode.level + 1;
                for(int i = fixedRowCount; i<=size; i++){
                    for(int k = 0; k<size; k++){
                        for(int l = 0; l<size; l++){
                            tempMatrix[k][l] = minNode.matrix[k][l];
                        }
                    }
                    rowSwap(tempMatrix, fixedRowCount, i);
                    minLowerBound = Math.min(minLowerBound, getLowerBound(tempMatrix, fixedRowCount, fixedColumnCount, size));
                }
                for(int i = fixedRowCount; i<=size; i++){
                    for(int k = 0; k<size; k++){
                        for(int l = 0; l<size; l++){
                            tempMatrix[k][l] = minNode.matrix[k][l];
                        }
                    }
                    rowSwap(tempMatrix, fixedRowCount, i);
                    if(getLowerBound(tempMatrix, fixedRowCount, fixedColumnCount, size) == minLowerBound){
                        priorityQueue.add(new Node(tempMatrix,size,fixedRowCount,fixedColumnCount,level,i));
                    }
                }
            }
        }
        System.out.println(minimumBand);
        for(int i=0;i<size;i++){
            for(int j=0; j<size; j++){
                System.out.print(ansMatrix[i][j]+" ");
            }
            System.out.println();
        }
    }


}
