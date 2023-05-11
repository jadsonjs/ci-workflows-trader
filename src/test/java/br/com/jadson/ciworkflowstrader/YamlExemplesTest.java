package br.com.jadson.ciworkflowstrader;


/**
 * This class contains example of YAML files contents
 */
public class YamlExemplesTest {

    /**
     * Example of file from project: /simplycode07/SKYZoom
     * https://raw.githubusercontent.com/simplycode07/SKYZoom/master/.github/workflows/python-app.yml
     */
    public static final String ciWorkflowFile = "# This workflow will install Python dependencies, run tests and lint with a single version of Python\n" +
            "# For more information see: https://help.github.com/actions/language-and-framework-guides/using-python-with-github-actions\n" +
            "\n" +
            "name: Python application\n" +
            "\n" +
            "on:\n" +
            "  push:\n" +
            "    branches: [ master ]\n" +
            "  pull_request:\n" +
            "    branches: [ master ]\n" +
            "\n" +
            "jobs:\n" +
            "  build:\n" +
            "\n" +
            "    runs-on: macos-latest\n" +
            "\n" +
            "    steps:\n" +
            "    - uses: actions/checkout@v2\n" +
            "    - name: Set up Python 3.8\n" +
            "      uses: actions/setup-python@v2\n" +
            "      with:\n" +
            "        python-version: 3.8\n" +
            "    - name: Install dependencies\n" +
            "      run: |\n" +
            "        python -m pip install --upgrade pip\n" +
            "        pip install flake8 pytest\n" +
            "        if [ -f requirements.txt ]; then pip install -r requirements.txt; fi\n" +
            "\n" +
            "    - name: Build With py2app\n" +
            "      run: |\n" +
            "        rm -rf build dist\n" +
            "        python setup.py py2app -A";


    public static final String ciWorkflowFile2 = """
            name: Build
                        
            on: [push, pull_request]
                        
            jobs:
              build:
                runs-on: ubuntu-latest
                name: Build with PlatformIO
                steps:
                  - uses: actions/checkout@v2
                  - name: Set up Python
                    uses: actions/setup-python@v2
                    with:
                      python-version: "3.7"
                  - name: Install platformio
                    run: |
                      python -m pip install --upgrade pip
                      pip install platformio
                  - name: Run PlatformIO
                    run: platformio run
                        
              documentation:
                runs-on: ubuntu-latest
                name: Create the documentation
                steps:
                  - uses: actions/checkout@v2
                  - name: Set up Node.js
                    uses: actions/setup-node@v1
                    with:
                      node-version: "14.x"
                  - name: Install build dependencies
                    run: npm install
                  - name: Build documentation
                    run: npm run docs:build
            """;


    public static final String nodeCIFile = """
        name: Node.js CI
    
        on: [push, pull_request]
        
        jobs:
          build:
            runs-on: ubuntu-latest
        
            strategy:
              matrix:
                node-version: [16.x]
        
            steps:
              - uses: actions/checkout@v2
              - name: Use Node.js ${{ matrix.node-version }}
                uses: actions/setup-node@v1
                with:
                  node-version: ${{ matrix.node-version }}
              - run: npm install
              - run: npm test
                env:
                  CI: true
    
        """;

    public static final String javaMavenCiFile = """
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

    /**
     * Example of not CI workflow from project gradle/gradle
     * https://raw.githubusercontent.com/gradle/gradle/master/.github/workflows/auto-assign-pr-to-author.yml
     */
    public static final String notCIWorkflowFile = "name: 'Auto Assign PR to Author'\n" +
            "on:\n" +
            "  pull_request:\n" +
            "    types: [opened]\n" +
            "\n" +
            "permissions:\n" +
            "  contents: read\n" +
            "\n" +
            "jobs:\n" +
            "  add-reviews:\n" +
            "    permissions:\n" +
            "      contents: read  # for kentaro-m/auto-assign-action to fetch config file\n" +
            "      pull-requests: write  # for kentaro-m/auto-assign-action to assign PR reviewers\n" +
            "    runs-on: ubuntu-latest\n" +
            "    steps:\n" +
            "      - uses: kentaro-m/auto-assign-action@v1.2.1";


    public static final String newTestFile = """
                name: Tests Browser Extension
                            
                on: push
                
                jobs:
                  test:
                
                    runs-on: ubuntu-latest
                
                    steps:
                      - uses: actions/checkout@v2
                
                      - name: Use Node.js
                        uses: actions/setup-node@v1
                        with:
                          node-version: 14.x
                
                      - name: yarn install
                        run: yarn install
                
                      - name: yarn test
                        run: yarn run test
                
                  notify:
                    needs:
                      - test
                
                    if:
                      ${{ always() &&
                        (
                          github.event_name == 'push' ||
                          github.event.pull_request.head.repo.full_name == github.repository
                        )
                      }}
                    runs-on: ubuntu-latest
                    steps:
                      - name: Conclusion
                        uses: technote-space/workflow-conclusion-action@v1
                      - name: Send Slack notif
                        uses: 8398a7/action-slack@v3
                        with:
                          status: ${{ env.WORKFLOW_CONCLUSION }}
                          fields: workflow, repo, message, commit, author, eventName, ref
                        env:
                          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
                          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
            """;


}
