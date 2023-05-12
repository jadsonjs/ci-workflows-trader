package br.com.jadson.ciworkflowstrader.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Operation related with files
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 */
@Component
public class FileUtil {

    /**
     * Download a content of a url and save in a file
     * @param url
     * @return
     */
    public void downloadContent(String url, String outputFile){


        String directoryPath = outputFile.substring(0, outputFile.lastIndexOf("/"));
        this.createLocalDirectory(directoryPath);


        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {



            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.err.println("FileUtil: "+e.getMessage()+" cause -> "+e.getCause());
        }

    }

    /**
     * Get a content of a url to a String
     * @param theUrl
     * @return
     */
    public String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();

        try (BufferedInputStream in = new BufferedInputStream(new URL(theUrl).openStream());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in)) ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
        }catch(Exception e) {
            System.err.println("FileUtil: "+e.getMessage()+" cause -> "+e.getCause());
        }
        return content.toString();
    }


    public void createLocalDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();
    }


    public List<File> getAllFilesForFolder(final File folder, List<File> files) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                getAllFilesForFolder(fileEntry, files);
            } else {
                files.add(fileEntry);
            }
        }
        return files;
    }
}
