/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.flomeise.huffmancoder;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Flohw
 */
public class BitInputStream extends FilterInputStream {
	BitInputStream(InputStream in) {
		super(in);
	}
}
