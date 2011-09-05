/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.flomeise.huffmancoder;

/**
 *
 * @author Flohw
 */
public class ByteConversion {
	
    public static short btos(byte b) {
        return (short) (b<0?b+256:b);
    }

    public static byte stob(short i) {
        if(i <= 255) {
            return (byte) (i>127?i-256:i);
        }
        throw new UnsupportedOperationException("Parameter bigger than byte size");
    }
}
