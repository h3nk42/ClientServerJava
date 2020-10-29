package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The FileContentReader class implements the handling of local files for the server
 * side of the model specified in assignment 1)
 */
public class FileContentReader {
    /**
     * This method lists all file names in the "files" directory of the package
     * @return a StringBuilder holding all file names
     */
    public StringBuilder getFileNames () {
        File folder = new File("./src/com/company/files");
        File[] listOfFiles = folder.listFiles();
        StringBuilder fileAnswer = new StringBuilder();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileAnswer.append(listOfFiles[i].getName() + "   ");
            }
        }
        return fileAnswer;
    }

    /**
     * This method specifies if a certain file exists in the "files" directory
     * @param filename a String to look up a file with corresponding name
     * @return a boolean specifying if the file with name corresponding to filename exists
     */
    private boolean fileExists (String filename) {
        File folder = new File("./src/com/company/files");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].getName().equals(filename)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method looks up a file in the "files" directory, attempts to read it's content
     * and if successful returns the content as a list of strings
     * @param fileName a String specifying the name of the file
     * @return an ArrayList of Strings holding the file's content
     */
    public List<String> getFileContent (String fileName) {
            List<String> records = new ArrayList<String>();
        boolean isFound = this.fileExists(fileName);
            if(isFound) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader("./src/com/company/files/" + fileName));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        records.add(line);
                    }
                    reader.close();
                    return records;
                } catch (Exception e) {
                    System.err.format("Exception occurred trying to read '%s'.", fileName);
                    e.printStackTrace();
                    records.add("File not found!");
                    return records;
                }
            }
            else {
                records.add("File not found!");
                return records;
            }
        }
}
