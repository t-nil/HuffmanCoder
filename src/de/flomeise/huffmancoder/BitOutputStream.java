/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.flomeise.huffmancoder;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;

/**
 *
 * @author Flohw
 */
public class BitOutputStream extends FilterOutputStream {
	private BitSet bitset;
	private byte counter;
	
	BitOutputStream(OutputStream out) {
		super(out);
		bitset = new BitSet(8);
		counter = 0;
	}
	
	public void writeBit(boolean bit) throws IOException {
		bitset.set(counter, bit);
		counter++;
		
		if(counter > 7) {
			out.write(bitset.toByteArray()[0]);
			counter = 0;
		}
	}
	
	public void writeBitSet(BitSet bitset) throws IOException {
		for(int i = 0; i < bitset.size(); i++) {
			writeBit(bitset.get(i));
		}
	}
	
	public void flush() throws IOException {
		for(int i = counter; i < 8; i++) {
			bitset.set(i, false);
		}
		out.write(bitset.toByteArray()[0]);
		super.flush();
	}
}