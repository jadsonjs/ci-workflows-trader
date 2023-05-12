package br.com.jadson.ciworkflowstrader.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.util.*;

@Component
public class CIWorkflowUtil {

    @Autowired
    YamlUtil yamlUtil;

    public CIWorkflowUtil(){}

    public CIWorkflowUtil(YamlUtil yamlUtil){
        this.yamlUtil = yamlUtil;
    }

    /**
     * Proccess all workflow file content of the projects and return a list of most common words
     * @param githubProjectNames
     * @return
     */
    public Map<String, Integer> processMostCommonWord(List<String> githubProjectNames, boolean onlyCI) {

        // TODO
        Map<String, Integer> words = new HashMap<>();
        words.put("install", 10);
        words.put("build", 40);
        words.put("test", 5);
        words.put("run", 15);
        words.put("sudo", 20);

        return words;
    }

    /**
     * This method check if a GHActions workflow is a CI-related workflow.
     *
     * This is the aim of this project.
     *
     * @param workflowFileName
     * @param content
     * @param commonCIWordsList
     * @return
     */
    public boolean isCIWorkflow(String workflowFileName, String content, final List<String> commonCIWordsList) {
        
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load( new ByteArrayInputStream(content.getBytes()));

        if (isCIBYDirectIdentification(data, workflowFileName) || isCIBYIndirectIdentification(data, commonCIWordsList))
            return true;

        return false;
    }





    //////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * The workflow file contains “CI” in the name of the YAML file, example: “ci.yml” or “*-ci.yml”, or contains “CI”
     * in the workflow name, for example: “name: Node.js CI”
     * @param workflowFileName
     * @param data
     * @return
     */
    private boolean isCIBYDirectIdentification(Map<String, Object> data, String workflowFileName) {
        if(yamlUtil.hasCIInFileName(workflowFileName))
            return true;

        if( yamlUtil.containsInWorkflowName(data, "ci") )
            return true;
        return false;
    }


    /**
     * In this case, we identified workflows related to CI by the file content using three criteria:
     *
     *     (i) checking if the workflow has the action “action/checkout”, as it is the most frequently used action in CI workflows [10].
     *
     *     (ii) Check if the workflow has the event “push” or “pull_request”, as they are the most frequent events [10].
     *
     *     (iii) checking if the file has one of the 30 most common CI-related keywords in workflow file metadata. To find the most
     *     common CI-related keywords, we downloaded the content of all workflows files directly identified as CI workflow, tokenized it,
     *     and manually identified the 30 most common tokens related to CI.
     *
     * @param commonCIWordsList
     * @param data
     * @return
     */
    private boolean isCIBYIndirectIdentification(Map<String, Object> data, List<String> commonCIWordsList) {
        if(data != null && yamlUtil.containsCheckoutAction(data) && yamlUtil.containsCIEvents(data) && yamlUtil.containsWord(data, commonCIWordsList)){ // or these conditions
            return true;
        }
        return false;
    }


}
