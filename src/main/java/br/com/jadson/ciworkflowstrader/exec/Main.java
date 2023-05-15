package br.com.jadson.ciworkflowstrader.exec;

import br.com.jadson.ciworkflowstrader.util.CIWorkflowUtil;
import br.com.jadson.ciworkflowstrader.util.YamlUtil;

import java.util.List;

/**
 * Some example of use of this code
 */
public class Main {

    static final List<String> CI_MOST_COMMON_WORDS = List.of("install", "build", "test", "run", "sudo", "tests", "yarn", "cache", "make", "npm", "pip", "checkout", "dependencies", "set", "check", "setup", "bash", "docker", "git", "master", "cargo", "lint", "upload", "update", "export", "cmake", "code", "release", "coverage", "push");

    public static void main(String[] args) {

        CIWorkflowUtil workflowUtil = new CIWorkflowUtil(new YamlUtil()).setGithubToken(args[0]);

        //////////////////////////////////////////////////////
        String yamlFile = """
            name: Java CI with Maven
                        
            on:
              push:
                branches: [ main, 2.3.x, 2.4.x, 3.0.x ]
              pull_request:
                branches: [ main, 2.3.x, 2.4.x, 3.0.x ]
                        
            permissions:
              contents: read
                        
            jobs:
              build:
                name: "Test with ${{ matrix.version }}"
                strategy:
                  matrix:
                    version: [ 8.0.332-tem, 11.0.13-tem ]
                runs-on: ubuntu-latest
                steps:
                  - uses: actions/checkout@v2
                  - name: Cache local Maven repository
                    uses: actions/cache@v2
                    with:
                      path: ~/.m2/repository
                      key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
                      restore-keys: ${{ runner.os }}-maven-
                  - name: Download ${{ matrix.version }}
                    uses: sdkman/sdkman-action@master
                    id: sdkman
                    with:
                      candidate: java
                      version: ${{ matrix.version }}
                  - name: Set up ${{ matrix.version }}
                    uses: actions/setup-java@v1
                    with:
                      java-version: 8
                      jdkFile: ${{ steps.sdkman.outputs.file }}
                  - name: Build with Maven
                    run: ./mvnw -V -B verify
              build-java-17:
                name: "Test with ${{ matrix.version }}"
                strategy:
                  matrix:
                    version: [ 17.0.1-tem ]
                runs-on: ubuntu-latest
                steps:
                  - uses: actions/checkout@v2
                  - name: Cache local Maven repository
                    uses: actions/cache@v2
                    with:
                      path: ~/.m2/repository
                      key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
                      restore-keys: ${{ runner.os }}-maven-
                  - name: Download ${{ matrix.version }}
                    uses: sdkman/sdkman-action@master
                    id: sdkman
                    with:
                      candidate: java
                      version: ${{ matrix.version }}
                  - name: Set up ${{ matrix.version }}
                    uses: actions/setup-java@v1
                    with:
                      java-version: 8
                      jdkFile: ${{ steps.sdkman.outputs.file }}
                  - name: Build with Maven
                    run: ./mvnw -V -B verify -Dspotless.check.skip=true -Dspotless.apply.skip=true
                        
            """;
        System.out.println("CI workflow: "+workflowUtil.isCIWorkflow("java-app.yml", yamlFile, CI_MOST_COMMON_WORDS));

        //////////////////////////////////////////////////////

        System.out.println("CI workflow: "+workflowUtil.isCIWorkflow("https://github.com/simplycode07/SKYZoom/blob/master/.github/workflows/python-app.yml", CI_MOST_COMMON_WORDS));

        //////////////////////////////////////////////////////

        System.out.println("Common Words: "+workflowUtil.generateCICommonWords(List.of("gradle/gradle", "onflow/flow-go")));
    }
}
