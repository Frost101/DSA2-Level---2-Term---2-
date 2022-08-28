import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        File file = new File("input.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String data = sc.nextLine();
        int n = Integer.parseInt(data);
        RedBlackTree redBlackTree = new RedBlackTree();
        for(int i=1; i<=n; i++){
            data = sc.nextLine();
            String[] strings = data.split(" ");
            int e,x;
            e = Integer.parseInt(strings[0]);
            x = Integer.parseInt(strings[1]);
            if(e==0){
                boolean flag = redBlackTree.delete(x);
                int ans;
                if(flag)ans = 1;
                else  ans = 0;
                try {
                    myWriter.write(e+" "+x+" "+ans+"\n");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            else if(e==1){
                boolean flag = redBlackTree.search(x);
                int ans;
                if(flag){
                    ans = 0;
                }
                else{
                    redBlackTree.insert(new Node(x,2));
                    ans = 1;
                }
                try {
                    myWriter.write(e+" "+x+" "+ans+"\n");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            else if(e==2){
                boolean flag = redBlackTree.search(x);
                int ans;
                if(flag){
                    ans = 1;
                }
                else{
                    ans = 0;
                }
                try {
                    myWriter.write(e+" "+x+" "+ans+"\n");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            else if(e==3){

                int ans = redBlackTree.lessPriority(x);

                try {
                    myWriter.write(e+" "+x+" "+ans+"\n");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        }
        try {
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
