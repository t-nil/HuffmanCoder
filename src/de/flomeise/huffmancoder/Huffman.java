
package de.flomeise.huffmancoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private static final int BUFF_SIZE = 4096;
    private File input, output;
    
    public void compress(File inputFile, File outputFile) {
        System.out.println("Input: " + inputFile + "; Output: " + outputFile);
        if(outputFile.exists()) {
            outputFile.delete();
        }
        input = inputFile;
        output = outputFile;
        long frequencies[] = getByteFrequencies();
        for(int i=0; i<256; i++) {
            System.out.println((char) i + "(" + i + ") = " + frequencies[i]);
        }
        BinaryTree tree = frequenciesToTree(frequencies);
        byte[] codeOutput = treeToArray(tree);
		tree = arrayToTree(codeOutput);
		BitCode[] codes = new BitCode[256];
		treeToBitCodeArray(tree, codes, new BitCode(1), 0);
		for(int i = 0; i < codes.length; i++) {
			if(codes[i] != null)
				System.out.println((char) i + "(" + i + ") = " + getBitCodeAsString(codes[i]));
		}
		writeCompressedData(codes, codeOutput);
		JOptionPane.showMessageDialog(null, "Successfully compressed");
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
        treeToBitCodeArray(tree, codes, new BitCode(), 0);
        for(int i=0; i<256; i++) {
            System.out.println((char) i + ": " + getBitCodeAsString(codes[i]));
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
                    frequencies[ByteConversion.btos(buffer[i])]++;
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
            int treeLength = ByteConversion.btos(treeLengthArr[0]) * 256 + ByteConversion.btos(treeLengthArr[1]);
            System.out.println("treeLength: " + treeLength);
            byte[] tree = new byte[treeLength];
            istream.read(tree);
            return arrayToTree(tree);
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
	private BinaryTree optimizeTree(BinaryTree tree) {
		return arrayToTree(treeToArray(tree));
	}
	
    private BinaryTree arrayToTree(byte[] arr) {
        int i = 1, depth = 1;
        BinaryTree tree = new BinaryTree();
        while(i < arr.length) {
            int count = ByteConversion.btos(arr[i]);
            i++;
            for(int j=0; j<count; j++) {
                putIntoTree(tree, arr[i], depth);
                i++;
            }
            depth++;
        }
        return tree;
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
    
    private void treeToBitCodeArray(BinaryTree t, BitCode[] bArr, BitCode b, int i) {
        if(t.hasByte()) {
			BitCode temp = new BitCode(i);
			temp.or(b);
            bArr[ByteConversion.btos(t.getByte())] = temp;
            return;
        } else {
            b.set(i, false);
            treeToBitCodeArray(t.getLeft(), bArr, b, i+1);
            b.set(i, true);
            treeToBitCodeArray(t.getRight(), bArr, b, i+1);
        }
    }

    private String getBitCodeAsString(BitCode b) {
        if(b == null) {
            return null;
        }
        String s = new String();
        for(int i=0; i<b.size(); i++) {
            s += b.get(i)?1:0;
        }
        return s;
    }

    private BinaryTree frequenciesToTree(long[] frequencies) {
        TreeSet v = new TreeSet(new BinaryTree());
        for(int i=0; i<frequencies.length; i++) {
            if(frequencies[i] != 0) {
                BinaryTree b = new BinaryTree();
                b.setByte(ByteConversion.stob((short)i));
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

    private byte[] treeToArray(BinaryTree tree) {
        ArrayList[] temp = new ArrayList[tree.getDepth()];
		for(int i = 0; i < temp.length; i++)
			temp[i] = new ArrayList();
        countBytesInLayers(tree, temp, -1);
        ArrayList arr = new ArrayList();
        
        for(ArrayList a : temp) {
            arr.add(ByteConversion.stob((short) a.size()));
			for(Object o : a) {
				arr.add((byte) o);
			}
        }
		arr.add(0, (byte) tree.getDepth());
		
		byte[] bArr = new byte[arr.size()];
		int i = 0;
		for(Object o : arr) {
			bArr[i] = (byte) o;
			i++;
		}
		
        return bArr;
    }

    private void countBytesInLayers(BinaryTree tree, ArrayList[] temp, int i) {
        if(tree.hasByte()) {
			if(temp[i] == null)
				temp[i] = new ArrayList();
            temp[i].add(tree.getByte());
            return;
        }
        countBytesInLayers(tree.getLeft(), temp, i+1);
        countBytesInLayers(tree.getRight(), temp, i+1);
    }

    private void writeCompressedData(BitCode[] codes, byte[] codeOutput) {
		try(FileInputStream fis = new FileInputStream(input); BitOutputStream bos = new BitOutputStream(new FileOutputStream(output))) {
			bos.write(codeOutput);
			byte[] buffer = new byte[BUFF_SIZE];
			int bytesRead = 0;
			long bytesReadAll = 0, filesize = input.length();
			while((bytesRead = fis.read(buffer)) != -1) {
				for(int i = 0; i < bytesRead; i++) {
					short inputByte = ByteConversion.btos(buffer[i]);
					bos.writeBitSet(codes[inputByte]);
				}
				bytesReadAll += bytesRead;
			}
			bos.flush();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
    }
}