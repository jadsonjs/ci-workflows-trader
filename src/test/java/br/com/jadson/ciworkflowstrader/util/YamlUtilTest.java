package br.com.jadson.ciworkflowstrader.util;

import br.com.jadson.ciworkflowstrader.YamlFilesContents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 */
class YamlUtilTest {

    @TempDir
    Path tempDir;


    ///////////// count most common words test ////////////////

    @Test
    void countMostCommonsWordsInYaml() throws IOException  {

        Map<String, Integer> map = new YamlUtil().countMostCommonsWordsInYaml(YamlFilesContents.CI_WORKFLOW_FILE);

        for (String key : map.keySet()){
            System.out.println(key+ "->"+ map.get(key));
        }
        Assertions.assertTrue(map.keySet().size() > 0);
        Assertions.assertEquals(4, map.get("pip"));
        Assertions.assertEquals(4, map.get("install"));
    }



    ///////////// check word in file test /////

    List<String> CI_WORDS = List.of("install", "build", "test", "run", "sudo", "tests", "yarn", "cache", "make", "npm", "pip", "checkout", "dependencies", "set", "check", "setup", "bash", "docker", "git", "master", "cargo", "lint", "upload", "update", "export", "cmake", "code", "release", "coverage", "push");


    @Test
    void containsCIWord() {
        Assertions.assertTrue( new YamlUtil().containsWord( new Yaml().load(new ByteArrayInputStream(YamlFilesContents.CI_WORKFLOW_FILE.getBytes())), CI_WORDS) );
    }

    @Test
    void notContainsCIWord() {
        Assertions.assertFalse( new YamlUtil().containsWord( new Yaml().load(new ByteArrayInputStream(YamlFilesContents.NOT_CI_WORKFLOW_FILE.getBytes())), CI_WORDS) );
    }


    //////////// pull e pull_request events Test //////////////

    @Test
    void containsCIEvents() {
        Assertions.assertTrue( new YamlUtil().containsCIEvents( new Yaml().load(new ByteArrayInputStream(YamlFilesContents.CI_WORKFLOW_FILE.getBytes()))) );
    }

    @Test
    void hasCIEventsWithoutBranchesNames() {
        Assertions.assertTrue( new YamlUtil().containsCIEvents( new Yaml().load(new ByteArrayInputStream(YamlFilesContents.CI_WORKFLOW_FILE_2.getBytes()))) );
    }

    @Test
    void hasCIEventsJavaMavenProject() {
        Assertions.assertTrue( new YamlUtil().containsCIEvents( new Yaml().load(new ByteArrayInputStream(YamlFilesContents.JAVA_MAVEN_CI_FILE.getBytes()))) );
    }

    //////////// CheckoutAction Test //////////////

    @Test
    void containsCheckoutAction() {
        Assertions.assertTrue( new YamlUtil().containsCheckoutAction( new Yaml().load(new ByteArrayInputStream(YamlFilesContents.CI_WORKFLOW_FILE.getBytes()))) );
    }

    @Test
    void hasCheckoutAction2() {
        Assertions.assertTrue( new YamlUtil().containsCheckoutAction( new Yaml().load(new ByteArrayInputStream(YamlFilesContents.CI_WORKFLOW_FILE_2.getBytes()))) );
    }

    @Test
    void hasCheckoutActionJavaMavenProject() {
        Assertions.assertTrue( new YamlUtil().containsCheckoutAction( new Yaml().load(new ByteArrayInputStream(YamlFilesContents.JAVA_MAVEN_CI_FILE.getBytes()))) );
    }

    //////// CI in the workflow name tests ///////

    @Test
    void containsInWorkflowNameNodeProject() {
        Assertions.assertTrue( new YamlUtil().containsInWorkflowName(   new Yaml().load(new ByteArrayInputStream(YamlFilesContents.NODE_CI_FILE.getBytes())), "ci")   );
    }


    @Test
    void containsInWorkflowNameJavaProject() {
        Assertions.assertTrue( new YamlUtil().containsInWorkflowName(   new Yaml().load(new ByteArrayInputStream(YamlFilesContents.JAVA_MAVEN_CI_FILE.getBytes())), "ci")   );
    }

    @Test
    void containsInWorkflowNameNoCIFlow() {
        Assertions.assertFalse( new YamlUtil().containsInWorkflowName(   new Yaml().load(new ByteArrayInputStream(YamlFilesContents.NOT_CI_WORKFLOW_FILE.getBytes())), "ci")   );
    }

    ////////// name of file tests /////////////


    @Test
    void hasCIInFileName() {
        Assertions.assertTrue( new YamlUtil().hasCIInFileName("ci.yml") );
    }

    @Test
    void hasCIInFileCompostName() {
        Assertions.assertTrue( new YamlUtil().hasCIInFileName("android-ci.yml") );
    }


    @Test
    void hasNotCIInFileName1() {
        Assertions.assertFalse( new YamlUtil().hasCIInFileName("android-analysis.yml") );
    }

    @Test
    void hasNotCIInFileName2() {
        Assertions.assertFalse( new YamlUtil().hasCIInFileName("city.yml") );
    }


}