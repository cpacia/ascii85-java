/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Chris Pacia
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Arrays;

public class Ascii85 {

    /**
     * Encode a string using Ascii85
     */
    public static String encode(String data){
        // Convert to bytes
        byte[] bytes = data.getBytes();

        // Pad with null bytes
        int padLen = 0;
        if (bytes.length % 4 != 0){
            padLen = 4 - bytes.length % 4;
        }
        byte[] pad = new byte [padLen];
        byte[] padded = new byte[bytes.length + padLen];
        System.arraycopy(bytes, 0, padded, 0, bytes.length);
        System.arraycopy(pad, 0, padded, bytes.length, padLen);

        // Encode each four byte block
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<padded.length; i+=4){
            byte[] block = Arrays.copyOfRange(padded, i, i + 4);
            long x = Long.parseLong(bytesToHex(block), 16);
            int N0 = (int) (x / 52200625) % 85 + 33;
            int N1 = (int)(x/614125) % 85 + 33;
            int N2 = (int)(x/7225) % 85 + 33;
            int N3 = (int) (x/85) % 85 + 33;
            int N4 = (int) x % 85 + 33;
            // Append a 'z' if all the bytes in the block are zero.
            if (N0 == 0 && N1 == 0 && N2 == 0 && N3 == 0 && N4 == 0 && i+4 != padded.length){
                builder.append("z");
            } else {
                // If this is the last block we break out early to avoid including the pad.
                builder.append(Character.toChars(N0));
                builder.append(Character.toChars(N1));
                if (i+4 == padded.length && bytes.length % 4 == 1){break;}
                builder.append(Character.toChars(N2));
                if (i+4 == padded.length && bytes.length % 4 == 2){break;}
                builder.append(Character.toChars(N3));
                if (i+4 == padded.length && bytes.length % 4 == 3){break;}
                builder.append(Character.toChars(N4));
            }
        }

        return "<~" + builder.toString() + "~>";
    }

    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
