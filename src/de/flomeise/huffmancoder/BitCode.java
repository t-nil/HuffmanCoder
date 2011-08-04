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
public class BitCode {
    private BitSet set;
    private int length;
    
    public BitCode() {}
    
    public BitCode(BitSet set) {
        this.set = set;
    }
    
    public BitCode(BitSet set, int length) {
        this.set = set;
        this.length = length;
    }
    
    public BitSet getSet() {
        return set;
    }
    
    public int getLength() {
        return length;
    }
    
    public void setSet(BitSet set) {
        this.set = set;
    }
    
    public void setLength(int length) {
        this.length = length;
    }
}
