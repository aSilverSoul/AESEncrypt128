//Gary Machorro
//CS4600
public class ByteOperations {

    //Static expanded key.
    private static char[] expandedKey = new char[BoxConstants.EXPANDED_KEY_SIZE];

    //Static mix column.
    private static char[] mixColumn = new char[4];

     //Static mix column copy.
    private static char[] mixColumnCopy = new char[4];

    //Static output byte array.
    private static byte[] output = new byte[BoxConstants.BLOCK_LENGTH];

    //Static chars array for converting byteToChars.
    private static char[] charsFromBytes = new char[BoxConstants.BLOCK_LENGTH];

    //Static round key matrix
    private static char[][] roundKey = new char[BoxConstants.BLOCK_LENGTH / 4][BoxConstants.BLOCK_LENGTH / 4];

    //Static state matrix.
    private static char[][] state = new char[BoxConstants.BLOCK_LENGTH / 4][BoxConstants.BLOCK_LENGTH / 4];

    //method used for XOR
    private static char xor(char a, char b) {
        int xor = a ^ b;
        return (char) (0xff & xor);
    }

    public static void fillInitialStateMatrix(char[] bytes) {
        fillInitialMatrix(bytes, state, 0);
    }
    //gets char, then turns it into a byte array
    private static void fillInitialMatrix(char[] bytes, char[][] matrix, int from) {
        for (int i = 0; i < BoxConstants.BLOCK_LENGTH; ++i) {
            int y = i % 4;
            int x = (i - y) / 4;
            matrix[y][x] = bytes[i + from];
        }
    }

    //xors state matrix with roundkey
    private static void addRoundKey(char[][] state, char[][] roundKey) {
        for (int i = 0; i < BoxConstants.BLOCK_LENGTH / 4; ++i) {
            for (int j = 0; j < BoxConstants.BLOCK_LENGTH / 4; ++j) {
                state[i][j] = xor(state[i][j], roundKey[i][j]);
            }
        }
    }

    //shift bit
    private static void shiftRows(char[][] state) {
        char temp;
        temp = state[1][0];
        state[1][0] = state[1][1];
        state[1][1] = state[1][2];
        state[1][2] = state[1][3];
        state[1][3] = temp;

        temp = state[2][0];
        state[2][0] = state[2][2];
        state[2][2] = temp;
        temp = state[2][1];
        state[2][1] = state[2][3];
        state[2][3] = temp;

        temp = state[3][0];
        state[3][0] = state[3][3];
        state[3][3] = state[3][2];
        state[3][2] = state[3][1];
        state[3][1] = temp;
    }

    //swap bytes with sbox values
    private static void subBytesSwap(char[][] state) {
        state[0][0] = BoxConstants.getSboxValue(state[0][0]);
        state[0][1] = BoxConstants.getSboxValue(state[0][1]);
        state[0][2] = BoxConstants.getSboxValue(state[0][2]);
        state[0][3] = BoxConstants.getSboxValue(state[0][3]);

        state[1][0] = BoxConstants.getSboxValue(state[1][0]);
        state[1][1] = BoxConstants.getSboxValue(state[1][1]);
        state[1][2] = BoxConstants.getSboxValue(state[1][2]);
        state[1][3] = BoxConstants.getSboxValue(state[1][3]);

        state[2][0] = BoxConstants.getSboxValue(state[2][0]);
        state[2][1] = BoxConstants.getSboxValue(state[2][1]);
        state[2][2] = BoxConstants.getSboxValue(state[2][2]);
        state[2][3] = BoxConstants.getSboxValue(state[2][3]);

        state[3][0] = BoxConstants.getSboxValue(state[3][0]);
        state[3][1] = BoxConstants.getSboxValue(state[3][1]);
        state[3][2] = BoxConstants.getSboxValue(state[3][2]);
        state[3][3] = BoxConstants.getSboxValue(state[3][3]);
    }

    //galois mult in mixcolumn method
    public static char galoisMult(char a, char b) {
        if (a == 0 || b == 0) return 0;
        char s;
        s = (char) ((int) BoxConstants.getLTableValue((int) a) + (int) BoxConstants.getLTableValue((int) b));
        s %= 255;
        s = BoxConstants.getATableValue((int) s);

        return s;
    }


     //Mix each column of the state matrix.Every column is multiplied with a matrix.
    private static void mixColumns(char[][] state) {
        int n = BoxConstants.BLOCK_LENGTH / 4;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                mixColumn[j] = state[j][i];
            }

            mixColumn(mixColumn);

            for (int j = 0; j < n; ++j) {
                state[j][i] = mixColumn[j];
            }
        }
    }


    //AES round-diagram from class
    private static void AESRound(char[][] state, char[][] roundKey) {
        subBytesSwap(state);//swap
        shiftRows(state);//shift row
        mixColumns(state);//column mix
        addRoundKey(state, roundKey);//add key round
    }

    //create round key matrix from expanded key used 16 bytes of expanded key
    private static void createRoundKey(char[] expandedKey, int from) {
        fillInitialMatrix(expandedKey, roundKey, from);
    }


     //Mixes the given column by multiplying it with a matrix.
    //matrix used:
     // 2 3 1 1
     //1 2 3 1
     //1 1 2 3
     //3 1 1 2
    private static void mixColumn(char[] column) {
        int n = BoxConstants.BLOCK_LENGTH / 4;

        for (int i = 0; i < n; i++) {
            mixColumnCopy[i] = column[i];
        }

        column[0] = xor(xor(xor(galoisMult(mixColumnCopy[0], (char) 2),
                galoisMult(mixColumnCopy[3], (char) 1)),
                galoisMult(mixColumnCopy[2], (char) 1)),
                galoisMult(mixColumnCopy[1], (char) 3));

        column[1] = xor(xor(xor(galoisMult(mixColumnCopy[1], (char) 2),
                galoisMult(mixColumnCopy[0], (char) 1)),
                galoisMult(mixColumnCopy[3], (char) 1)),
                galoisMult(mixColumnCopy[2], (char) 3));

        column[2] = xor(xor(xor(galoisMult(mixColumnCopy[2], (char) 2),
                galoisMult(mixColumnCopy[1], (char) 1)),
                galoisMult(mixColumnCopy[0], (char) 1)),
                galoisMult(mixColumnCopy[3], (char) 3));

        column[3] = xor(xor(xor(galoisMult(mixColumnCopy[3], (char) 2),
                galoisMult(mixColumnCopy[2], (char) 1)),
                galoisMult(mixColumnCopy[1], (char) 1)),
                galoisMult(mixColumnCopy[0], (char) 3));
    }
    //shifts the word 1 step to the left
    private static void rotate(char[] word) {
        char first = word[0];
        for (int i = 0; i < 3; ++i) {
            word[i] = word[i + 1];
        }

        word[3] = first;
    }

    //sbox permutaion and xor with rcon
    private static void applyCore(char[] word, int round) {
        rotate(word);
        for (int i = 0; i < 4; ++i) {
            word[i] = BoxConstants.getSboxValue(word[i]);
        }

        word[0] = xor(word[0], BoxConstants.getRCONValue((char) round));
    }


    //expands key to 176 bytes instead of 32
    private static char[] keyExpansion(char[] key) {
        int n = BoxConstants.BLOCK_LENGTH / 4;
        int currentSize = 0;
        int rconIteration = 1;
        char[] temp = new char[n];

        for (int i = 0; i < BoxConstants.KEY_SIZE; ++i) {
            expandedKey[i] = key[i];
        }

        currentSize += BoxConstants.KEY_SIZE;

        while (currentSize < BoxConstants.EXPANDED_KEY_SIZE) {
            for (int i = 0; i < n; ++i) {
                temp[i] = expandedKey[(currentSize - n) + i];
            }

            if (currentSize % BoxConstants.KEY_SIZE == 0) {
                applyCore(temp, rconIteration++);
            }

            for (int i = 0; i < n; ++i) {
                char expandedKeyFormer = expandedKey[currentSize - BoxConstants.KEY_SIZE];
                char tempByte = temp[i];
                expandedKey[currentSize++] = xor(expandedKeyFormer, tempByte);
            }
        }

        return expandedKey;
    }

    //bytes to char conversion
    public static char[] byteArrayToCharArray(byte[] bytes) {
        for (int i = 0; i < charsFromBytes.length; ++i) {
            charsFromBytes[i] = (char) (bytes[i] < 0 ? bytes[i] + (1 << 8) : bytes[i]);
        }

        return charsFromBytes;
    }

    //returns byte array for the final aes state matrix
    public static byte[] createEncryptionByteArray(char[][] finalState) {
        for (int i = 0; i < BoxConstants.BLOCK_LENGTH; ++i) {
            int y = i % 4;
            int x = (i - y) / 4;
            output[i] = (byte) finalState[y][x];
        }

        return output;
    }

    //10 round aes for state matrix
    public static byte[] AES(char[] key) {
        char[] expandedKey = keyExpansion(key);

        createRoundKey(expandedKey, 0);
        addRoundKey(state, roundKey);

        for (int i = 1; i < BoxConstants.ROUNDS; ++i) {
            createRoundKey(expandedKey, i * BoxConstants.BLOCK_LENGTH);
            AESRound(state, roundKey);
        }

        createRoundKey(expandedKey, BoxConstants.ROUNDS * BoxConstants.BLOCK_LENGTH);
        subBytesSwap(state);
        shiftRows(state);
        addRoundKey(state, roundKey);

        return createEncryptionByteArray(state);
    }
}