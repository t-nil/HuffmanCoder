
package de.flomeise.huffmancoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Flohw
 */
public class Huffman {
    private static final int BUFF_SIZE = 8192;
    private File input, output;
    private BitCode eofCode;
    
    public void compress(File inputFile, File outputFile) {
        System.out.println("Input: " + inputFile + "; Output: " + outputFile);
        if(outputFile.exists()) {
            outputFile.delete();
        }
        input = inputFile;
        output = outputFile;
        long frequencies[] = getByteFrequencies();
        for(int i=0; i<256; i++) {
            System.out.println((char) i + ": " + frequencies[i]);
        }
        BinaryTree tree = frequenciesToTree(frequencies);
        tree = optimizeTree(tree);
        writeCompressedData(tree);
    }
    
    public void decompress(File inputFile, File outputFile) {
        System.out.println("Input: " + inputFile + "; Output: " + outputFile);
        if(outputFile.exists()) {
            outputFile.delete();
        }
        input = inputFile;
        output = outputFile;
        BinaryTree tree = getBinaryTreeFromCompressedFile();
        BitCode[] codes = new BitCode[256];
        treeToBitSetArray(tree, codes, new BitSet(), 0);
        for(int i=0; i<256; i++) {
            System.out.println((char) i + ": " + getBitSetAsString(codes[i]));
        }
    }

    private long[] getByteFrequencies() {
        long frequencies[] = new long[256];
        try {
            FileInputStream istream = new FileInputStream(input);
            byte[] buffer = new byte[BUFF_SIZE];
            int numBytes;
            while((numBytes = istream.read(buffer)) != -1) {
                for(int i=0; i<numBytes; i++) {
                    frequencies[btos(buffer[i])]++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return frequencies;
    }

    private BinaryTree getBinaryTreeFromCompressedFile() {
        try {
            BinaryTree t = new BinaryTree();
            FileInputStream istream = new FileInputStream(input);
            byte[] treeLengthArr = new byte[2];
            istream.read(treeLengthArr);
            int treeLength = btos(treeLengthArr[0]) * 256 + btos(treeLengthArr[1]);
            System.out.println("treeLength: " + treeLength);
            byte[] tree = new byte[treeLength];
            istream.read(tree);
            return arrayToTree(tree);
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private BinaryTree arrayToTree(byte[] arr) {
        int i = 0, depth = 1;
        BinaryTree tree = new BinaryTree();
        while(i < arr.length) {
            int count = btos(arr[i]);
            i++;
            for(int j=0; j<count; j++) {
                putIntoTree(tree, arr[i], depth);
                i++;
            }
            depth++;
        }
        return tree;
    }

    private int btos(byte b) {
        return (short)b<0?b+256:b;
    }

    private boolean putIntoTree(BinaryTree t, byte b, int depth) {
        if(depth == 0) {
            if(t.hasByte() == false && t.getLeft() == null && t.getRight() == null) {
                t.setByte(b);
                return true;
            } else {
                return false;
            }
        } else {
            if(t.hasByte()) {
                return false;
            } else {
                if(t.getLeft() == null) {
                    t.setLeft(new BinaryTree());
                }
                if(putIntoTree(t.getLeft(), b, depth-1) == false) {
                    if(t.getRight() == null) {
                        t.setRight(new BinaryTree());
                    }
                    if(putIntoTree(t.getRight(), b, depth-1) == false) {
                        return false;
                    }
                }
                return true;
            }
        }
    }
    
    private void treeToBitSetArray(BinaryTree t, BitCode[] bArr, BitSet b, int i) {
        if(t.hasByte()) {
            bArr[btos(t.getByte())] = new BitCode(b.get(0, i), i);
            return;
        } else {
            b.set(i, false);
            treeToBitSetArray(t.getLeft(), bArr, b, i+1);
            b.set(i, true);
            treeToBitSetArray(t.getRight(), bArr, b, i+1);
        }
    }

    private String getBitSetAsString(BitCode b) {
        if(b == null) {
            return null;
        }
        String s = new String();
        for(int i=0; i<b.getLength(); i++) {
            s += b.getSet().get(i)?1:0;
        }
        return s;
    }

    private BinaryTree frequenciesToTree(long[] frequencies) {
        TreeSet v = new TreeSet(new BinaryTree());
        for(int i=0; i<frequencies.length; i++) {
            if(frequencies[i] != 0) {
                BinaryTree b = new BinaryTree();
                b.setByte(stob((short)i));
                b.setFrequency(frequencies[i]);
                v.add(b);
            }
        }
        while(v.size() > 1) {
            BinaryTree t1 = (BinaryTree) v.pollFirst(), t2 = (BinaryTree) v.pollFirst();
            BinaryTree t3 = new BinaryTree();
            t3.setLeft(t1);
            t3.setRight(t2);
            t3.setFrequency(t1.getFrequency()+t2.getFrequency());
            v.add(t3);
        }
        return (BinaryTree) v.first();
    }

    private byte stob(short i) {
        if(i <= 255) {
            return (byte) (i>127?i-256:i);
        }
        throw new UnsupportedOperationException("Parameter bigger than byte size");
    }

    private BinaryTree optimizeTree(BinaryTree tree) {
        return arrayToTree(treeToArray(tree));
    }

    private byte[] treeToArray(BinaryTree tree) {
        ArrayList[] temp = new ArrayList[tree.getDepth()];
        countBytesInLayers(tree, temp, -1);
        int treeLength = temp.length;
        for(ArrayList a : temp) {
            treeLength += a.size();
        }
        byte[] arr = new byte[treeLength];
        
        for(ArrayList a : temp) {
            
        }
        return null;
    }

    private void countBytesInLayers(BinaryTree tree, ArrayList[] temp, int i) {
        if(tree.hasByte()) {
            temp[i].add(tree.getByte());
            return;
        }
        countBytesInLayers(tree.getLeft(), temp, i+1);
        countBytesInLayers(tree.getRight(), temp, i+1);
    }

    private void writeCompressedData(BinaryTree tree) {
        BitCode[] codes = new BitCode[256];
        treeToBitSetArray(tree, codes, new BitSet(), 0);
        byte[] treeInBytes = treeToArray(tree);
        
        try {
            FileInputStream istream = new FileInputStream(input);
            FileOutputStream ostream = new FileOutputStream(output);
            BitCode tempCode = new BitCode();
            byte[] bufferIn = new byte[BUFF_SIZE];
            byte[] bufferOut = new byte[BUFF_SIZE];
            int numBytes, j;
            /*while((numBytes = istream.read(bufferIn)) != -1) {
                for(int i=0; i<numBytes; i++) {
                    
                }
            }*/
            
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}