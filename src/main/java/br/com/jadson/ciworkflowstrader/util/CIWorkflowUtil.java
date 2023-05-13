package br.com.jadson.ciworkflowstrader.util;

import br.com.jadson.ciworkflowstrader.model.WorkFlow;
import br.com.jadson.snooper.githubactions.data.workflow.WorkflowInfo;
import br.com.jadson.snooper.githubactions.operations.GHActionWorkflowsExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

@Component
public class CIWorkflowUtil {

    String githubToken = "";

    @Autowired
    YamlUtil yamlUtil;

    @Autowired
    FileUtil fileUtil;

    public CIWorkflowUtil(){}

    public CIWorkflowUtil(YamlUtil yamlUtil){
        this.yamlUtil = yamlUtil;
    }

    public CIWorkflowUtil(YamlUtil yamlUtil, FileUtil fileUtil){
        this.yamlUtil = yamlUtil;
        this.fileUtil = fileUtil;
    }


    /**
     * Proccess all workflow file content of the projects and return a list of most common words
     * @param githubProjectNames
     * @return
     */
    public Map<String, Integer> processMostCommonWord(List<String> githubProjectNames, boolean onlyCIWorkFlows) {

        if(githubToken == null || githubToken.isEmpty())
            throw new IllegalArgumentException("Please, provide the github token");

        Map<String, Integer> words = new HashMap<>();

        GHActionWorkflowsExecutor ghWorkflowExecutor = new GHActionWorkflowsExecutor(githubToken);

        int index = 1;

        forProjects:
        for (String projectName : githubProjectNames) {

            List<WorkflowInfo> workflows = ghWorkflowExecutor.getWorkflows(projectName);

            int wfIndex = 1;
            for (WorkflowInfo wf : workflows) {

                String wfFileName = extractWorkflowFileName(wf);

                String wfContent = extractWorkflowContent(projectName, wfFileName);

                if( ! onlyCIWorkFlows || ( onlyCIWorkFlows  && isCIBYDirectIdentification(wfFileName, wfContent ) ) ) {
                    String wfFileFullPath = saveWorkflowContent(projectName, wfFileName, wfContent);


                    File workflowFile = new File(wfFileFullPath);

                    System.out.println("Counting words of "+wfFileName);

                    Map<String, Integer> localWords = yamlUtil.countMostCommonsWordsInYaml(workflowFile);

                    // sum the local words with global words
                    for (String localKey : localWords.keySet()){
                        if (words.containsKey(localKey)) {
                            int counter = words.get(localKey);
                            counter = counter +  localWords.get(localKey);
                            words.put(localKey, counter);
                        } else {
                            words.put(localKey, localWords.get(localKey));
                        }
                    }

                }

                wfIndex++;

                if (wfIndex > 0 && wfIndex % 100 == 0)  sleep(1);

            } // all workflow of a project

            index++;

            if (index > 0 && index % 100 == 0) sleep(1);

        } // all projects

        return words;
    }

    /**
     * This method check for each project witch workflows of this project are a CI-related workflow.
     * @param githubProjectNames
     * @param commonCIWords
     * @return
     */
    public List<WorkFlow> checkCIWorkflows(List<String> githubProjectNames, List<String> commonCIWords) {

        if(githubToken == null || githubToken.isEmpty())
            throw new IllegalArgumentException("Please, provide the github token");

        List<WorkFlow> workflows = new ArrayList<>();

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
                    workflows.add( new WorkFlow(projectName, wf, true));
                }else{
                    workflows.add( new WorkFlow(projectName, wf, false));
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


    /**
     * Download the workflow file to local machine in a tmp Dir
     * @param projectName
     * @param workflowFileName
     * @return
     */
//    public String downloadWorkflowContent(String projectName, String workflowFileName){
//
//        String tmpDirLocation = System.getProperty("java.io.tmpdir");
//
//        String projectTmpDir = tmpDirLocation +"/"+ projectName;
//        String worflowFileFullPath = projectTmpDir +"/"+ workflowFileName;
//
//        fileUtil.createLocalDirectory(projectTmpDir);
//
//        String githubRawURL = "https://raw.githubusercontent.com/" + projectName + "/master/.github/workflows/" + workflowFileName;
//        fileUtil.downloadContent(githubRawURL, worflowFileFullPath);
//        return worflowFileFullPath;
//
//    }

    /**
     * Save the context of workflow to a file
     * @param projectName
     * @param workflowFileName
     * @param wfContent
     * @return
     */
    private String saveWorkflowContent(String projectName, String workflowFileName, String wfContent){

        String tmpDirLocation = System.getProperty("java.io.tmpdir");

        String projectTmpDir = tmpDirLocation +"/"+ projectName;
        String worflowFileFullPath = projectTmpDir +"/"+ workflowFileName;

        fileUtil.createLocalDirectory(projectTmpDir);

        fileUtil.createLocalFile(worflowFileFullPath, wfContent);

        return worflowFileFullPath;

    }




    private String extractWorkflowFileName(WorkflowInfo wf) {
        return wf.path.substring(wf.path.lastIndexOf("/")+1);
    }

    public String extractWorkflowContent(String nameWithOwner, String workflowFileName){
        String githubRawURL = "https://raw.githubusercontent.com/" + nameWithOwner + "/master/.github/workflows/" + workflowFileName;
        return fileUtil.getUrlContents(githubRawURL);
    }


    private static void sleep(int minutes) {
        System.out.println(">>>> Sleeping 1 min zzZ ...");
        try { Thread.sleep(minutes * 60 * 1000); } catch (InterruptedException e) {e.printStackTrace();}
    }


}
