package br.com.jadson.ciworkflowstrader.util;

import br.com.jadson.ciworkflowstrader.model.GHAWord;
import br.com.jadson.ciworkflowstrader.model.GHAWorkFlow;
import br.com.jadson.snooper.githubactions.data.workflow.WorkflowInfo;
import br.com.jadson.snooper.githubactions.operations.GHActionWorkflowsExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

@Component
public class CIWorkflowUtil {

    String githubToken = "";

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
    public  List<GHAWord> generateCICommonWords(List<String> githubProjectNames) {

        if(githubToken == null || githubToken.isEmpty())
            throw new IllegalArgumentException("Please, provide the github token");

        Map<String, Integer> wordsMap = new HashMap<>();

        GHActionWorkflowsExecutor ghWorkflowExecutor = new GHActionWorkflowsExecutor(githubToken);

        int index = 1;

        forProjects:
        for (String projectName : githubProjectNames) {

            List<WorkflowInfo> workflows = ghWorkflowExecutor.getWorkflows(projectName);

            int wfIndex = 1;
            for (WorkflowInfo wf : workflows) {

                String wfFileName = extractWorkflowFileName(wf);

                String wfContent = extractWorkflowContent(projectName, wfFileName);

                if( isCIBYDirectIdentification(wfFileName, wfContent ) ) {


                    Map<String, Integer> localWords = yamlUtil.countMostCommonsWordsInYaml(wfContent);

                    // sum the local words with global words
                    for (String localKey : localWords.keySet()){
                        if (wordsMap.containsKey(localKey)) {
                            int counter = wordsMap.get(localKey);
                            counter = counter +  localWords.get(localKey);
                            wordsMap.put(localKey, counter);
                        } else {
                            wordsMap.put(localKey, localWords.get(localKey));
                        }
                    }

                }

                wfIndex++;

                if (wfIndex > 0 && wfIndex % 100 == 0)  sleep(1);

            } // all workflow of a project

            index++;

            if (index > 0 && index % 100 == 0) sleep(1);

        } // all projects

        List<GHAWord> words = new ArrayList<>();

        Stream<Map.Entry<String,Integer>> sorted = wordsMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
        sorted.forEach( (k) -> {
            words.add(new GHAWord(k.getKey(), k.getValue()));
        });

        return words;
    }

    /**
     * This method check for each project witch workflows of this project are a CI-related workflow.
     * @param githubProjectNames
     * @param commonCIWords
     * @return
     */
    public List<GHAWorkFlow> checkCIWorkflows(List<String> githubProjectNames, List<String> commonCIWords) {

        if(githubToken == null || githubToken.isEmpty())
            throw new IllegalArgumentException("Please, provide the github token");

        List<GHAWorkFlow> workflows = new ArrayList<>();

        GHActionWorkflowsExecutor ghWorkflowExecutor = new GHActionWorkflowsExecutor(githubToken);

        int index = 1;

        forProjects:
        for (String projectName : githubProjectNames) {

            List<WorkflowInfo> workflowsInfo = ghWorkflowExecutor.getWorkflows(projectName);

            int wfIndex = 1;
            for (WorkflowInfo wf : workflowsInfo) {

                String wfFileName = extractWorkflowFileName(wf);

                String wfContent = extractWorkflowContent(projectName, wfFileName);


                if(   isCIWorkflow(wfFileName, wfContent, commonCIWords)   ){
                    workflows.add( new GHAWorkFlow(projectName, wf, true));
                }else{
                    workflows.add( new GHAWorkFlow(projectName, wf, false));
                }

                wfIndex++;

                if (wfIndex > 0 && wfIndex % 100 == 0)  sleep(1);

            } // all workflow of a project

            index++;

            if (index > 0 && index % 100 == 0) sleep(1);

        } // all projects

        return workflows;
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

        if (isCIBYDirectIdentification(workflowFileName, content) || isCIBYIndirectIdentification(content, commonCIWordsList))
            return true;

        return false;
    }

    /**
     * This method check if a GHActions workflow is a CI-related workflow passing the the URL to the workflow file.
     * @param url
     * @param ciMostCommonWords
     * @return
     */
    public boolean isCIWorkflow(String url, List<String> ciMostCommonWords) {

        String projectName = extractProjectName(url);
        String wfYamlFileName = extractWorkflowFileName(url);
        String content = extractWorkflowContent(projectName, wfYamlFileName);

        if (isCIBYDirectIdentification(wfYamlFileName, content) || isCIBYIndirectIdentification(content, ciMostCommonWords))
            return true;

        return false;
    }



    public CIWorkflowUtil setGithubToken(String githubToken) {
        this.githubToken = githubToken;
        return this;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * The workflow file contains “CI” in the name of the YAML file, example: “ci.yml” or “*-ci.yml”, or contains “CI”
     * in the workflow name, for example: “name: Node.js CI”
     * @param workflowFileName
     * @param wfContent
     * @return
     */
    private boolean isCIBYDirectIdentification(String workflowFileName, String wfContent) {

        try {
            if (yamlUtil.hasCIInFileName(workflowFileName))
                return true;

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(new ByteArrayInputStream(wfContent.getBytes()));

            if (yamlUtil.containsInWorkflowName(data, "ci"))
                return true;

        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }

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
     * @param wfContent
     * @return
     */
    private boolean isCIBYIndirectIdentification(String wfContent, List<String> commonCIWordsList) {

        try {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(new ByteArrayInputStream(wfContent.getBytes()));

            if (data != null && yamlUtil.containsCheckoutAction(data) && yamlUtil.containsCIEvents(data) && yamlUtil.containsWord(data, commonCIWordsList)) { // or these conditions
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return false;
    }



    private String extractWorkflowFileName(WorkflowInfo wf) {
        return wf.path.substring(wf.path.lastIndexOf("/")+1);
    }

    private String extractWorkflowFileName(String url) {
        return url.substring(url.lastIndexOf("/")+1);
    }

    /**
     * Extract project name from WorkFlow URL
     *
     * @param url https://github.com/simplycode07/SKYZoom/blob/master/.github/workflows/python-app.yml
     * @return simplycode07/SKYZoom
     */
    public String extractProjectName(String url) {
        if(url == null || url.isEmpty())
            return "";
        if(!url.startsWith("https://github.com"))
            throw new IllegalArgumentException("not a valid github url: "+url);

        String s = (url.substring(19));                                    // remove https://github.com/
        return s.substring(0, s.indexOf("/", s.indexOf("/") + 1));      // get until the second "/"
    }


    public String extractWorkflowContent(String nameWithOwner, String workflowFileName){
        String githubRawURL = "https://raw.githubusercontent.com/" + nameWithOwner + "/master/.github/workflows/" + workflowFileName;
        return getUrlContents(githubRawURL);
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


    private static void sleep(int minutes) {
        System.out.println(">>>> Sleeping 1 min zzZ ...");
        try { Thread.sleep(minutes * 60 * 1000); } catch (InterruptedException e) {e.printStackTrace();}
    }



}
