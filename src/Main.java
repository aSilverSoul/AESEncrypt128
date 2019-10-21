import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {


    //String path = "C:\\Users\\garym\\Desktop\\bigBean.txt";
    public static void main(String... args) throws Throwable {
        String []z=args[0].split("\\.");
        if(z[1].equals("txt")){
            z[1]=".enc";
        }
        System.out.println("enter key");
        Scanner scan= new Scanner(System.in);\
        

        File initialFile = new File(args[0]);
        InputStream targetStream = new FileInputStream(initialFile);

        boolean hasKey = false;
        char[] key = new char[BoxConstants.BLOCK_LENGTH];
        byte[] bytes;
        while (true) {
            bytes = new byte[BoxConstants.BLOCK_LENGTH];
            int next = targetStream.read(bytes);
            if (next == -1) break;
            char[] input = ByteOperations.byteArrayToCharArray(bytes);

            if (!hasKey) {
                for (int i = 0; i < input.length; ++i) {
                    key[i] = input[i];
                }
                hasKey = true;
            } else {
                byte data[] = enc(key, input);
                FileOutputStream out = new FileOutputStream(z[0]+z[1]);
                out.write(data);
                out.close();
            }
        }
    }
     //Encrypts then prints
    //key and plaintext used
    private static byte[] enc(char[] key, char[] plainText) {
        ByteOperations.fillInitialStateMatrix(plainText);

        byte[] encryption = ByteOperations.AES(key);
        try {
            return encryption;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

}