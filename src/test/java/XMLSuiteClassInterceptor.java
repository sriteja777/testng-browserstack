import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class XMLSuiteClassInterceptor implements IAlterSuiteListener {

    @Override
    public void alter(List<XmlSuite> suites) {
        JSONParser parser = new JSONParser();
        int platformLength = 0;
        JSONArray platforms;
        try {
            JSONObject config = (JSONObject) parser.parse(new FileReader("src/test/resources/conf/cross.conf.json"));
            if (!config.isEmpty()) {
                platforms = (JSONArray) config.get("platforms");
                platformLength = platforms.size();
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        int finalPlatformLength = platformLength;
        suites.forEach(suite -> {
        XmlTest[] testCloneArray = new XmlTest[suite.getTests().size()];
        List<XmlTest> newXmlTests = new CopyOnWriteArrayList<>();
        List<XmlTest> xmlTests = suite.getTests();
        int count = Integer.parseInt(String.valueOf(xmlTests.size()));

        for (int i = 0; i < count; i++) {
            XmlTest testClone = (XmlTest) xmlTests.get(i).clone();
            xmlTests.get(i).addParameter("platform", "0");
            newXmlTests.add(xmlTests.get(i));
            testClone.setClasses(xmlTests.get(i).getClasses());
            testCloneArray[i] = testClone;
        }

        if (finalPlatformLength > 1) {
            for (int index = 1; index < finalPlatformLength - 1; index++) {
                int cloneIndex = 0;
                for (XmlTest testClone : testCloneArray) {
                    XmlTest tempclone = (XmlTest) testClone.clone();
                    Map<String, String> map = tempclone.getAllParameters();
                    map.put("platform", String.valueOf(index));
                    tempclone.setClasses(testClone.getClasses());
                    testClone.setParameters(map);
                    newXmlTests.add(testClone);
                    testCloneArray[cloneIndex] = tempclone;
                    cloneIndex++;
                }
            }

            for (XmlTest testClone : testCloneArray) {
                Map<String, String> map = testClone.getAllParameters();
                map.put("platform", String.valueOf(finalPlatformLength - 1));
                testClone.setParameters(map);
                newXmlTests.add(testClone);
            }
        }

        suite.setTests(newXmlTests);
        });
    }
}
