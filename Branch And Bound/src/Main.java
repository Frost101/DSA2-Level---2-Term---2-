import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File file = new File("input.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String data = sc.nextLine();
        int n = Integer.parseInt(data);
        String[][] matrix = new String[n][n];

        for(int i=0; i<n; i++){
            data = sc.nextLine();
            String[] strings = data.split(" ");
            for(int j=0; j<n; j++){
               matrix[i][j] = strings[j];
            }
        }

        BandMatrix bandMatrix = new BandMatrix(matrix,n);
        bandMatrix.getMinimumBandMatrix(matrix,n);
    }

}
