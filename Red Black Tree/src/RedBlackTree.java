class Node{
    public int key;
    public int color; // 1 for black and 2 for red
    public Node parent,left,right;
    int leftChildCount,rightChildCount;

    public Node(){
        leftChildCount = rightChildCount = 0;
    }

    public Node(int key,int color){
        this.key = key;
        this.color = color;
        leftChildCount = rightChildCount = 0;
        parent=left=right=null;
    }
}


public class RedBlackTree {
    public Node root;
    public Node nil;

    public RedBlackTree(){
        root = new Node();
        nil = new Node(-999999,1);
        nil.color = 1;
        nil.parent = null;
        nil.left = null;
        nil.right = null;
        root = nil;
    }

    public Node getRoot() {
        return root;
    }

    private int totalCount(Node node){
        if(node == nil)return 0;
        return node.leftChildCount + node.rightChildCount + 1;
    }

    private void countFixUp(Node node){
        while (node.parent != nil){
            if(node == node.parent.left){
                node.parent.leftChildCount = totalCount(node);
            }
            else{
                node.parent.rightChildCount = totalCount(node);
            }
            node = node.parent;
        }
    }

    private void leftRotate(Node x){
        Node y = x.right;
        Node A = x.left;    //Alpha tree
        Node B = y.left;    //Beta Tree
        Node Y = y.right;   //Gamma Tree

        x.right = y.left;       //turning y's left subtree into x's right subtree
        x.rightChildCount = totalCount(B);
        if(y.left != nil){
            y.left.parent = x;      //Linking y's left child's parent to x
        }
        y.parent = x.parent;    //Linking x's parent to y

        if(x.parent == nil){
            root = y;
        }
        else if(x == x.parent.left){
            x.parent.left = y;
        }
        else{
            x.parent.right = y;
        }

        y.left = x;        //Setting up x as y's left child
        x.parent = y;
        y.leftChildCount = totalCount(x);

    }

    private void rightRotate(Node x){
        Node y = x.left;
        Node A = y.left;    //Alpha tree
        Node B = y.right;   //Beta Tree
        Node Y = x.right;   //Gamma Tree

        x.left = y.right;     //turning y's right subtree into x's left subtree
        x.leftChildCount = totalCount(B);

        if(y.right != nil){
            y.right.parent = x;   //Linking y's right child's parent to x
        }
        y.parent = x.parent;

        if(x.parent == nil){
            root = y;
        }
        else if(x == x.parent.right){
            x.parent.right = y;
        }
        else x.parent.left = y;
        y.right = x;        //Setting up x as y's right child
        x.parent = y;
        y.rightChildCount = totalCount(x);
    }

    private void insertFixUp(Node z){
        while (z.parent.color == 2){        //if z.parent.color is RED;
            if(z.parent == z.parent.parent.left){
                Node y = z.parent.parent.right;
                if(y.color == 2){     //if y.color is RED
                    /*
                        For Case 1
                        z's uncle y is RED
                    */
                    z.parent.color = 1;
                    y.color = 1;
                    z.parent.parent.color = 2;
                    z = z.parent.parent;
                }
                else {
                    if(z == z.parent.right){
                        /*
                            For Case 2
                            Uncle y is black and z is a right child
                        */
                        z = z.parent;
                        leftRotate(z);
                    }
                    /*
                        For Case 3
                        Uncle y is black and z is a left child
                    */
                    z.parent.color = 1;
                    z.parent.parent.color = 2;
                    rightRotate(z.parent.parent);
                }
            }
            else{
                Node y = z.parent.parent.left;
                if(y.color == 2){
                    z.parent.color = 1;
                    y.color = 1;
                    z.parent.parent.color = 2;
                    z = z.parent.parent;
                }
                else{
                    if(z == z.parent.left){
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = 1;
                    z.parent.parent.color = 2;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = 1;
    }

    public void insert(Node z){
        Node y = nil;
        Node x = root;
        while(x != nil){
            y = x;
            if(z.key < x.key){
                x = x.left;
            }
            else{
                x = x.right;
            }
        }
        z.parent = y;
        if(y == nil){
            root = z;
        }
        else if(z.key < y.key){
            y.left = z;
            y.leftChildCount = 1;
            countFixUp(y);
        }
        else {
            y.right = z;
            y.rightChildCount = 1;
            countFixUp(y);
        }

        z.left = nil;
        z.right = nil;
        z.color = 2; //RED
        insertFixUp(z);
    }

    private Node minimum(Node x){
        while (x.left!=nil){
            x = x.left;
        }
        return x;
    }

    private Node successor(Node x){
        if(x.right != nil){
            return minimum(x.right);
        }
        Node y = x.parent;
        while(y != nil && x == y.right){
            x = y;
            y = y.parent;
        }
        return y;
    }

    private boolean searchHelper(Node node,int key){
        boolean found = false;
        if(node == nil){
            return found;
        }

        if(node.key == key){
            found = true;
        }
        else if(node.key > key){
            if(node.left!= nil) found = searchHelper(node.left, key);
        }
        else{
            if(node.right != nil)found = searchHelper(node.right,key);
        }
        return found;
    }

    public boolean search(int key){
        return searchHelper(root, key);
    }

    private Node searchForDelete(Node node,int key){
        if(node.key == key){
            return node;
        }
        else if(node.key > key){
            if(node.left!= nil) return searchForDelete(node.left, key);
        }
        else{
            if(node.right != nil)return searchForDelete(node.right, key);
        }
        return null;
    }

    private void rbTransplant(Node u, Node v){
        if(u.parent == nil){
            root = v;
        }
        else if(u == u.parent.left){
            u.parent.left = v;
        }
        else{
            u.parent.right = v;
        }
        v.parent = u.parent;
        countFixUp(v);
    }

    private void deleteFixUp(Node x){
        while(x != root && x.color == 1){
            if(x == x.parent.left) {
                Node w = x.parent.right;
                if (w.color == 2) {
                    /*
                     Case:01:  x's sibling w is RED
                     */
                    w.color = 1;
                    x.parent.color = 2;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }

                if (w == nil) break;
               //if (w.right == null) System.out.println("right null");
                if (w.left.color == 1 && w.right.color == 1) {
                    /*
                    Case:02:  x's sibling w is black and both of w's children are black
                     */
                    w.color = 2;
                    x = x.parent;
                } else {
                    if (w.right.color == 1 && w.right != nil) {
                        /*
                        Case:03: x'a sibling w is black,left child == RED,right child == BLACK

                         */
                        w.left.color = 1;
                        w.color = 2;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    /*
                    Case:04, w is black and right child is red
                     */
                    w.color = x.parent.color;
                    x.parent.color = 1;
                    w.right.color = 1;
                    leftRotate(x.parent);
                    x = root;
                }
            }
            else{
                Node w = x.parent.left;
                if(w.color == 2){
                    w.color = 1;
                    x.parent.color = 2;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }

                if((w.right.color == 1) && (w.left.color == 1)){
                    w.color = 2;
                    x = x.parent;
                }
                else{
                    if(w.left.color == 1 && w.left != nil){
                        w.right.color = 1;
                        w.color = 2;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = 1;
                    w.left.color = 1;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = 1;
    }

    private void countDecrease(Node node){
        while(node.parent != nil){
            if(node == node.parent.left){
                node.parent.leftChildCount = (node.parent.leftChildCount-1);
            }
            else{
                node.parent.rightChildCount = (node.parent.rightChildCount-1);
            }
            node = node.parent;
        }
    }

    private void deleteHelper(Node z){
        countDecrease(z);
        Node y = z;
        int y_original_color = y.color;
        Node x;

        if(z.left == nil){
            x = z.right;
            rbTransplant(z, z.right);
        }
        else if(z.right == nil){
            x = z.left;
            rbTransplant(z, z.left);
        }
        else{
            y = minimum(z.right);
            y_original_color = y.color;
            x = y.right;
            if(y.parent == z){
                x.parent = y;
                y.leftChildCount = totalCount(z.left);
            }else{
                rbTransplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
                y.leftChildCount = totalCount(z.left);
                y.rightChildCount = totalCount(z.right);
            }
            rbTransplant(z,y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;

        }
        if(y_original_color == 1){
            deleteFixUp(x);
        }
    }

    public boolean delete(int key){
        if (search(key)) {
            Node z = searchForDelete(root, key);
            deleteHelper(z);
            return true;
        }
        return false;
    }

    private void printHelp(Node z){
        if(z != nil){
            System.out.print(z.key);
            System.out.print("(");
            printHelp(z.left);
            System.out.print(")");
            System.out.print("(");
            printHelp(z.right);
            System.out.print(")");
        }
    }

    public int lessPriority(int key){
            int count = 0;
            Node temp = root;
            while(temp != nil){
                if(temp.key == key){
                    count += totalCount(temp.left);
                    break;
                }
                else if(temp.key > key){
                    temp = temp.left;
                }
                else{
                    count += (totalCount(temp.left) + 1);
                    temp = temp.right;
                }
            }
            return count;
    }

    private void print(){
        printHelp(root);
        System.out.println();
    }
}
