package br.com.jadson.ciworkflowstrader.model;

import br.com.jadson.snooper.githubactions.data.workflow.WorkflowInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GHAWorkFlow {

    String projectName;

    WorkflowInfo workflow;

    /***
     * indicates this workflow of this project is a CI workflow
     */
    boolean ci;


}
