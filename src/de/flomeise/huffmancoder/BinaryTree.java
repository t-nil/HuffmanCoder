/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.flomeise.huffmancoder;

import java.io.InvalidClassException;
import java.util.Comparator;

/**
 *
 * @author Flohw
 */
class BinaryTree implements Comparator {
    private BinaryTree left, right;
    private byte assignedByte;
    private long frequency;
    private boolean hasByte;
    
    public BinaryTree() {
        left = null;
        right = null;
        assignedByte = 0;
        hasByte = false;
    }
    
    public void setLeft(BinaryTree newLeft) {
        if(hasByte) {
            throw new UnsupportedOperationException("Cannot set subtree when a byte has already been set!");
        }
        left = newLeft;
    }
    
    public void setRight(BinaryTree newRight) {
        if(hasByte) {
            throw new UnsupportedOperationException("Cannot set subtree when a byte has already been set!");
        }
        right = newRight;
    }
    
    public void setByte(byte b) {
        if(left != null || right != null) {
            throw new UnsupportedOperationException("Cannot set byte when a subtree has already been set!");
        }
        assignedByte = b;
        hasByte = true;
    }

    public BinaryTree getLeft() {
        return left;
    }
    
    public BinaryTree getRight() {
        return right;
    }

    public boolean hasByte() {
        return hasByte;
    }
    
    public byte getByte() {
        return assignedByte;
    }
    
    public long getFrequency() {
        return frequency;
    }
    
    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if(o1 instanceof BinaryTree && o2 instanceof BinaryTree) {
            BinaryTree b1 = (BinaryTree) o1;
            BinaryTree b2 = (BinaryTree) o2;
            if(b1.getFrequency() < b2.getFrequency()) {
                return -1;
            } else {
                return 1;
            }
        }
        return -1;
    }

    int getDepth() {
        if(hasByte()) {
            return 0;
        }
        return Math.max(left.getDepth(), right.getDepth())+1;
    }
}
