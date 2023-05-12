package br.com.jadson.ciworkflowstrader.controllers;


import br.com.jadson.ciworkflowstrader.util.CIWorkflowUtil;
import br.com.jadson.ciworkflowstrader.util.FileUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/ci-workflow")
public class CIWorkflowController {

    @Autowired
    CIWorkflowUtil ciWorkflowUtil;

    @Autowired
    FileUtil fileUtil;

    /**
     * To find the most common CI-related keywords, we downloaded the content of all workflows files directly identified as CI workflow, tokenized it,
     * and manually identified the 30 most common tokens related to CI.
     *
     * localhost:8080/ci-workflow/most-common-words?projects=gradle/gradle,/simplycode07/SKYZoom,/onflow/flow-go
     * @param githubProjectNames
     * @return
     */
    @GetMapping(path = "most-common-words" , produces = MediaType.APPLICATION_OCTET_STREAM_VALUE )
    public @ResponseBody byte[] mostCommonWords(@RequestParam(name = "projects") List<String> githubProjectNames, HttpServletResponse response) {

        Map<String, Integer> words = ciWorkflowUtil.processMostCommonWord(githubProjectNames, true);

        ByteArrayOutputStream out = writeContextToByteArray(words);
        response.setHeader("Content-Disposition", "attachment; filename=words.txt" );
        return out.toByteArray();
    }

    /**
     * Checks if the workflows of project are CI workflow or not
     * @return
     */
    @GetMapping(path = "/check-ci-workflows")
    public ResponseEntity<String> checkUseOfCiServer(@RequestParam(name = "projects") List<String> githubProjectNames) {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
    


    private ByteArrayOutputStream writeContextToByteArray(Map<String, Integer> content) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Stream<Map.Entry<String, Integer>> sorted = content.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
        sorted.forEach( (k) -> {
            try {
                out.write(  ( format(k.getKey(), 50) + k.getValue() ).getBytes());
                out.write("\n".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return out;
    }

    private String format(String word, int size){
        StringBuilder buffer = new StringBuilder(word);
        for (int i = 0 ; i < (size-word.length()) ; i++){
            buffer.append(" ");
        }
        return buffer.toString();
    }
}
