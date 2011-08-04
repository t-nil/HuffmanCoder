package de.flomeise.huffmancoder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener; 
import java.io.File; 
import javax.swing.JFileChooser; 

public class FileOpenDialog { 
    public static String open() { 
        final JFileChooser chooser = new JFileChooser("Verzeichnis wählen"); 
        chooser.setDialogType(JFileChooser.OPEN_DIALOG); 
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
        final File file = new File("/home"); 

        chooser.setCurrentDirectory(file); 

        chooser.addPropertyChangeListener(new PropertyChangeListener() { 
            public void propertyChange(PropertyChangeEvent e) { 
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) 
                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) { 
                    final File f = (File) e.getNewValue(); 
                } 
            } 
        }); 

        chooser.setVisible(true); 
        final int result = chooser.showOpenDialog(null); 

        if (result == JFileChooser.APPROVE_OPTION) { 
            File inputVerzFile = chooser.getSelectedFile(); 
            String inputVerzStr = inputVerzFile.getPath(); 
            return inputVerzStr;
        }
        chooser.setVisible(false);
        return null;
    } 
} 