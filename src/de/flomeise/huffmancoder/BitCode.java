/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.flomeise.huffmancoder;

import java.util.BitSet;

/**
 *
 * @author Flohw
 */
public class BitCode extends BitSet {
	private int size;
	
	public BitCode() {
		super();
		size = 0;
	}
	
	public BitCode(int size) {
		super(size);
		this.size = size;
	}
	
	public int size() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public String toString() {
		String s = new String();
		for(int i = 0; i < size; i++) {
			s += get(i)?"1":"0";
		}
		return s;
	}
}
