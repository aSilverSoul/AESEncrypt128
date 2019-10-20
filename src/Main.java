import java.io.BufferedInputStream;
import java.io.InputStream;

public class Main {


    public static void main(String... args) throws Throwable {
        InputStream bufferedInputStream = new BufferedInputStream(System.in);
        boolean hasKey = false;
        char[] key = new char[BoxConstants.BLOCK_LENGTH];
        byte[] bytes;
        while (true) {
            bytes = new byte[BoxConstants.BLOCK_LENGTH];
            int next = bufferedInputStream.read(bytes);
            if (next == -1) break;
            char[] input = ByteOperations.byteArrayToCharArray(bytes);

            if (!hasKey) {
                for (int i = 0; i < input.length; ++i) {
                    key[i] = input[i];
                }
                hasKey = true;
            } else {
                System.out.println(BoxConstants.BLOCK_LENGTH);
                enc(key, input);
            }
        }
    }
     //Encrypts then prints
    //key and plaintext used
    private static void enc(char[] key, char[] plainText) {
        ByteOperations.fillInitialStateMatrix(plainText);

        byte[] encryption = ByteOperations.AES(key);
        try {
            System.out.write(encryption);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

}