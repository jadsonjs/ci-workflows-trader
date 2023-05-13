package br.com.jadson.ciworkflowstrader.controllers;

import br.com.jadson.ciworkflowstrader.model.GHAWord;
import br.com.jadson.ciworkflowstrader.model.GHAWorkFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CIWorkflowControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    String query = "projects=gradle/gradle";
    String wordsQuery = "&words=git,build,clone,setup";

    @Test
    void mostCommonWords() {
        ResponseEntity<GHAWord[]> response = testRestTemplate.getForEntity("/ci-workflow/most-common-words?"+query, GHAWord[].class);
        GHAWord[] words = response.getBody();
        Assertions.assertTrue(words.length > 0);
    }

    @Test
    void checkUseOfCiServer() {
        ResponseEntity<GHAWorkFlow[]> response = testRestTemplate.getForEntity("/ci-workflow/check-ci-workflows?"+query+wordsQuery, GHAWorkFlow[].class);
        GHAWorkFlow[] workflows = response.getBody();
        Assertions.assertTrue(workflows.length > 0);


        for (int i = 0; i < workflows.length; i++) {
            if(workflows[i].getWorkflow().id == 14050307l){
                // Contributor CI Build
                Assertions.assertTrue(workflows[i].isCi());
            }
        }
    }

}