package br.com.jadson.ciworkflowstrader.controllers;


import br.com.jadson.ciworkflowstrader.model.GHAWord;
import br.com.jadson.ciworkflowstrader.model.GHAWorkFlow;
import br.com.jadson.ciworkflowstrader.util.CIWorkflowUtil;
import br.com.jadson.ciworkflowstrader.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * http://localhost:8080/ci-workflow/most-common-words?projects=gradle/gradle,/simplycode07/SKYZoom,/onflow/flow-go
     *
     * @param githubProjectNames
     * @return
     */
    @RouterOperation(operation =
    @Operation(operationId = "mostCommonWords", summary = "Find most commons CI words in GHActiosn workflows files", tags = { "GHAWord" },
            parameters = { @Parameter(in = ParameterIn.QUERY, name = "projects", description = "GitHub project names that use GHActions to identify the most commonly used words in workflows files") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = GHAWord.class))),
                    @ApiResponse(responseCode = "500", description = "No github.token set as env variable"),
            }))
    @GetMapping(path = "most-common-words" , produces = MediaType.APPLICATION_JSON_VALUE )
    public @ResponseBody List<GHAWord> mostCommonWords(@RequestParam(name = "projects") List<String> githubProjectNames) {

        if(System.getenv("github.token") == null)
            throw new IllegalArgumentException("Set the github.token as a env variable.");

        List<GHAWord> words = ciWorkflowUtil.setGithubToken(System.getenv("github.token")).processMostCommonWord(githubProjectNames, true);

        return words;
    }



    /**
     * Checks if the workflows of project are CI workflow or not
     *
     * http://localhost:8080/ci-workflow/check-ci-workflows?projects=gradle/gradle,/simplycode07/SKYZoom,/onflow/flow-go&words=install,build,test,run,sudo,tests,yarn,cache
     *
     * @return
     */
    @RouterOperation(operation =
    @Operation(operationId = "checkUseOfCiServer", summary = "Return the workflows information and check is it is CI-related or not", tags = { "GHAWorkFlow" },
            parameters = { @Parameter(in = ParameterIn.QUERY, name = "projects", description = "GitHub project names that use GHActions to identify the most commonly used words in workflows files"),
                    @Parameter(in = ParameterIn.QUERY, name = "words", description = "Most commons CI-related works that should be calculated in advance")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = GHAWorkFlow.class))),
                    @ApiResponse(responseCode = "500", description = "No github.token set as env variable"),
            }))
    @GetMapping(path = "/check-ci-workflows", produces = MediaType.APPLICATION_JSON_VALUE )
    public  @ResponseBody List<GHAWorkFlow> checkUseOfCiServer(@RequestParam(name = "projects") List<String> githubProjectNames, @RequestParam(name = "words") List<String> commonCIWords) {

        if(System.getenv("github.token") == null)
            throw new IllegalArgumentException("Set the github.token as a env variable.");

        List<GHAWorkFlow> resultList = ciWorkflowUtil.setGithubToken(System.getenv("github.token")).checkCIWorkflows(githubProjectNames, commonCIWords);

        return resultList;
    }

}
