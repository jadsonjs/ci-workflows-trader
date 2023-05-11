# ci-workflows-trader
ci-workflows-trader is a project to calculate GHActions CI workflows

Unlike other CI servers, GitHub Actions can define several work- 336 flows that can be responsible for executing tasks not related to CI. 337 “A workflow is an automated process composed of a series of jobs that 338 gets executed when it’s triggered by an event. Workflows are defined 339 in YAML files and are stored in a .github/workflows directory at the 340 root of the repository. A repository can also have multiple workflows” 341 [4]. 
Therefore, it was necessary to select workflows related to CI.

we considered a workflow to be a CI workflow when:

**Direct Identification**: The workflow file contains “CI” in the name of the YAML file, example: “ci.yml” or “*-ci.yml”, or contains “CI” in the workflow name, for example: “name: Node.js CI”

**Indirect Identification**: In this case, we identified work- flows related to CI by the file content using three criteria: (i) checking if the workflow has the action “action/checkout”, as it is the most frequently used action in CI workflows [10], (ii) Check if the workflow has the event “push” or
“pull_request”, as they are the most frequent events[10], and (iii) checking if the file has one of the 30 most common CI-related keywords in workflow file metadata. To find the most common CI-related keywords, we downloaded the con- tent of all workflows files directly identified as CI workflow, tokenized it, and manually identified the 30 most common tokens related to CI.


      [4] Ahmed Besbes. 2013. Github Actions — Everything You Need to Know to Get Started. Retrieved January 22, 2023 from https://towardsdatascience.com/github- actions- everything- you- need- to- know- to- get- started- 537f1dffa0ed

      [10] Alexandre Decan, Tom Mens, Pooya Rostami Mazrae, and Mehdi Golzadeh. 2022. On the Use of GitHub Actions in Software Development Repositories. In 2022 IEEE International Conference on Software Maintenance and Evolution (ICSME). 235–245. https://doi.org/10.1109/ICSME55016.2022.00029
