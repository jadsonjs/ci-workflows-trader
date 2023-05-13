# ci-workflows-trader
ci-workflows-trader is a project to calculate GHActions CI workflows

Unlike other CI servers, GitHub Actions can define several workflows that can be responsible for executing tasks not related to CI. *“A workflow is an automated process composed of a series of jobs that gets executed when it’s triggered by an event. Workflows are defined in YAML files and are stored in a .github/workflows directory at the root of the repository. A repository can also have multiple workflows”* [4]. 
Therefore, it was necessary to select workflows related to CI.

We considered a workflow to be a CI-related workflow when:

**Direct Identification**: The workflow file contains “CI” in the name of the YAML file, example: “ci.yml” or “\*-ci.yml”, or contains “CI” in the workflow name, for example: “name: Node.js CI”

**Indirect Identification**: In this case, we identified workflows related to CI by the file content using three criteria: 

 * **(i)** checking if the workflow has the action “action/checkout”, as it is the most frequently used action in CI workflows [10].
            
 * **(ii)** Check if the workflow has the event “push” or “pull_request”, as they are the most frequent events [10].
            
 * **(iii)** checking if the file has one of the 30 most common CI-related keywords in workflow file metadata. To find the most common CI-related keywords, we downloaded the content of all workflows files directly identified as CI workflow, tokenized it, and manually identified the 30 most common tokens related to CI.




`[4] Ahmed Besbes. 2013. Github Actions — Everything You Need to Know to Get Started. Retrieved January 22, 2023 from https://towardsdatascience.com/github-actions-everything-you-need-to-know-to-get-started-537f1dffa0ed`

`[10] Alexandre Decan, Tom Mens, Pooya Rostami Mazrae, and Mehdi Golzadeh. 2022. On the Use of GitHub Actions in Software Development Repositories. In 2022 IEEE International Conference on Software Maintenance and Evolution (ICSME). 235–245. https://doi.org/10.1109/ICSME55016.2022.00029`


#### Change Logs:

- 1.0 - 2023-12-05 - Project initial distribution


### Dependencies

    Java 17
    Gradle 8.1.1
    Junit 5.8.2

### How do I get set up?

#### From the source code:

Clone the project -> Import it as a gradle project on your IDE.


#### From the binary:

ci-workflows-trader has a binary distribution on **libs/ci-workflows-trader-X.Y.Z-plain.jar** directory.

Include it on the classpath of your project.


#### Run the application as a service:

Download the binary distribution on **libs/ci-workflows-trader-X.Y.Z.jar** directory.

Run the command:

    java -jar -Dserver.port=808X ci-workflows-trader.jar


#### Run the application at Docker:

ci-workflows-trader is being published in Docker Hub, if you want to execute without need to install
the JavaVM in your machine, you can just run the follow docker command:

    docker container run -d -p 808X:8080 jadsonjs/ci-workflows-trader:vX.Y.Z


#### REST api documentation:

Execute the application and access the follow address, which will be shown to you a set of services available in the tool to calculate the CI sub-practices


    http://localhost:808X/swagger-ui/index.html