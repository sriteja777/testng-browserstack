# testng-browserstack

[TestNG](http://testng.org) Integration with BrowserStack.

Master branch contains **Selenium 3** samples, for **Selenium 4 - W3C protocol** please checkout [selenium-4](https://github.com/browserstack/testng-browserstack/tree/selenium-4) branch

![BrowserStack Logo](https://d98b8t1nnulk5.cloudfront.net/production/images/layout/logo-header.png?1469004780)

## Using Maven

### Setup

* Clone the repo
* Install dependencies `mvn compile`
* Create sample browserstack.yml file with the browserstack related capabilities with your [BrowserStack Username and Access Key](https://www.browserstack.com/accounts/settings) and place it in your root folder.
* Add maven dependency of browserstack-java-sdk in your pom.xml file
```sh
<dependency>
    <groupId>com.browserstack</groupId>
    <artifactId>browserstack-java-sdk</artifactId>
    <version>LATEST</version>
    <scope>compile</scope>
</dependency
```
* Modify your build plugin to run tests by adding argLine `-javaagent:${com.browserstack:browserstack-java-sdk:jar}` and `maven-dependency-plugin` for resolving dependencies in the profiles `sample-test` and `sample-local-test`.
```
            <plugin>
               <artifactId>maven-dependency-plugin</artifactId>
                 <executions>
                   <execution>
                     <id>getClasspathFilenames</id>
                       <goals>
                         <goal>properties</goal>
                       </goals>
                   </execution>
                 </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0-M5</version>
                <configuration>
                    <suiteXmlFiles>
                        <suiteXmlFile>config/sample-local-test.testng.xml</suiteXmlFile>
                    </suiteXmlFiles>
                    <argLine>
                        -javaagent:${com.browserstack:browserstack-java-sdk:jar}
                    </argLine>
                </configuration>
            </plugin>
```

### Running your tests

- To run the test suite having cross-platform with parallelization, run `mvn test -P sample-test`
- To run local tests, run `mvn test -P sample-local-test`

 Understand how many parallel sessions you need by using our [Parallel Test Calculator](https://www.browserstack.com/automate/parallel-calculator?ref=github)
 
 
 ## Using Gradle
 
 ### Setup

* Clone the repo
* Install dependencies `gradle build`
* Following are the changes required in `gradle.build` -
  * Add `compileOnly 'com.browserstack:browserstack-java-sdk:1.0.1'` in dependencies
  * Create `def browserstackSDKArtifact = configurations.compileClasspath.resolvedConfiguration.resolvedArtifacts.find { it.name == 'browserstack-java-sdk' }`
  * Modify Task *SampleTest* and *SampleLocalTest* as :
  ```
  task sampleTest(type: Test) {
    useTestNG() {
      dependsOn cleanTest
      useDefaultListeners = true
      suites "config/sample-test.testng.xml"
        jvmArgs "-javaagent:${browserstackSDKArtifact.file}"
    }
  }
  ```


### Running your tests

- To run the test suite having cross-platform with parallelization, run `gradle sampleTest`
- To run local tests, run `gradle sampleLocalTest`

 Understand how many parallel sessions you need by using our [Parallel Test Calculator](https://www.browserstack.com/automate/parallel-calculator?ref=github)


## Notes
* You can view your test results on the [BrowserStack Automate dashboard](https://www.browserstack.com/automate)
* To test on a different set of browsers, check out our [platform configurator](https://www.browserstack.com/automate/java#setting-os-and-browser)
* You can export the environment variables for the Username and Access Key of your BrowserStack account

  ```
  export BROWSERSTACK_USERNAME=<browserstack-username> &&
  export BROWSERSTACK_ACCESS_KEY=<browserstack-access-key>
  ```

## Additional Resources
* [Documentation for writing Automate test scripts in Java](https://www.browserstack.com/automate/java)
* [Customizing your tests on BrowserStack](https://www.browserstack.com/automate/capabilities)
* [Browsers & mobile devices for selenium testing on BrowserStack](https://www.browserstack.com/list-of-browsers-and-platforms?product=automate)
* [Using REST API to access information about your tests via the command-line interface](https://www.browserstack.com/automate/rest-api)
