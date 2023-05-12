package br.com.jadson.ciworkflowstrader.util;

import br.com.jadson.ciworkflowstrader.YamlFilesContents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Jadson Santos - jadson.santos@ufrn.br
 */
class CIWorkflowUtilTest {

    List<String> CI_WORDS = List.of("install", "build", "test", "run", "sudo", "tests", "yarn", "cache", "make", "npm", "pip", "checkout", "dependencies", "set", "check", "setup", "bash", "docker", "git", "master", "cargo", "lint", "upload", "update", "export", "cmake", "code", "release", "coverage", "push");

    /**
     * The name of yaml file contains "CI"
     */
    @Test
    void isCIWorkflowDirectByFileName() {
        Assertions.assertTrue( new CIWorkflowUtil(new YamlUtil()).isCIWorkflow("ci.yml", YamlFilesContents.NOT_CI_WORKFLOW_FILE, CI_WORDS) );
    }

    /**
     * The name or workflow inside the file contains "CI"
     *
     *  name: Java CI with Maven
     */
    @Test
    void isCIWorkflowDirectByWorkflowName() {
        Assertions.assertTrue( new CIWorkflowUtil(new YamlUtil()).isCIWorkflow("java-app.yml", YamlFilesContents.JAVA_MAVEN_CI_FILE, CI_WORDS) );
    }

    /**
     * The content of the file has event, checktout and at least one CI words
     */
    @Test
    void isCIWorkflowIndirect() {
        Assertions.assertTrue( new CIWorkflowUtil(new YamlUtil()).isCIWorkflow("python-app.yml", YamlFilesContents.CI_WORKFLOW_FILE, CI_WORDS) );
    }

    @Test
    void isCIWorkflowIndirect2() {
        Assertions.assertTrue( new CIWorkflowUtil(new YamlUtil()).isCIWorkflow("java-app.yml", YamlFilesContents.NEW_TEST_FILE, CI_WORDS) );
    }


    @Test
    void isNotCIWorkflow() {
        Assertions.assertFalse( new CIWorkflowUtil(new YamlUtil()).isCIWorkflow("python-app.yml", YamlFilesContents.NOT_CI_WORKFLOW_FILE, CI_WORDS) );
    }

    @Test
    void isNotCIWorkflow2() {
        Assertions.assertFalse( new CIWorkflowUtil(new YamlUtil()).isCIWorkflow("auto-assign-pr-to-author.yml", YamlFilesContents.NOT_CI_WORKFLOW_FILE, CI_WORDS) );
    }



}