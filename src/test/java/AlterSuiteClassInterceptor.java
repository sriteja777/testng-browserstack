import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class AlterSuiteClassInterceptor implements IAlterSuiteListener {

    @Override
    public void alter(List<XmlSuite> suites) {
        JSONParser parser = new JSONParser();

        suites.forEach(suite -> {
            int platformLength = 0;
            JSONArray platforms;
            String configFile = suite.getParameter("config");
            try {
                JSONObject config = (JSONObject) parser.parse(new FileReader("src/test/resources/conf/" + configFile));
                if (!config.isEmpty()) {
                    platforms = (JSONArray) config.get("platforms");
                    platformLength = platforms.size();
                }
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
            int finalPlatformLength = platformLength;
            modifySuiteXML(suite, finalPlatformLength);
        });

    }

    private static void modifySuiteXML(XmlSuite suite, int numberOfPlatforms) {
        List<XmlTest> xmlTests = suite.getTests();
        List<XmlTest> xmlTests1 = new CopyOnWriteArrayList<>();
        int count = Integer.parseInt(String.valueOf(xmlTests.size()));
        for (int i = 0; i < count; i++) {
            XmlTest xmlTest = xmlTests.get(i);
            for (int j = 0; j < numberOfPlatforms; j++) {
                XmlTest xmlTestClone1 = (XmlTest) xmlTest.clone();
                xmlTestClone1.setName(xmlTestClone1.getName() + "-" + j);
                Map<String, String> map1 = xmlTestClone1.getAllParameters();
                map1.put("platform", String.valueOf(j));
                xmlTestClone1.setClasses(xmlTests.get(i).getClasses());
                xmlTestClone1.setParameters(map1);
                xmlTests1.add(xmlTestClone1);
            }
        }
        suite.getChildSuites().forEach(childSuite -> {
            if(childSuite.getParallel().toString().equalsIgnoreCase("false")) {
                childSuite.setParallel(XmlSuite.ParallelMode.TESTS);
                childSuite.setThreadCount(childSuite.getParentSuite().getThreadCount());
            }
            modifySuiteXML(childSuite, numberOfPlatforms);
        });
        suite.setTests(xmlTests1);
    }
}
