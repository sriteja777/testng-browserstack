import com.browserstack.local.Local;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SuiteClassIntercepter implements ISuiteListener {
    Local local;

    @Override
    public void onStart(ISuite iSuite) {
        String configFile = iSuite.getParameter("config");
        try {
            JSONParser parser = new JSONParser();
            JSONObject config = (JSONObject) parser.parse(new FileReader("src/test/resources/conf/" + configFile));
            Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
            if (commonCapabilities.get("browserstack.local") != null
                    && Objects.equals(commonCapabilities.get("browserstack.local"), "true")) {
                System.out.println("Starting BrowserStack Local...");
                local = new Local();
                Map<String, String> options = new HashMap<String, String>();
                options.put("key", config.get("key").toString());
                if (!local.isRunning()) {
                    local.start(options);
                    Runtime.getRuntime().addShutdownHook(new SuiteClassIntercepter.Closer(local));
                }
                System.out.println("BrowserStack Local Started Successfully.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFinish(ISuite iSuite) {
        if (local != null) {
            try {
                local.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class Closer extends Thread {
        private final Local local;

        public Closer(Local local) {
            this.local = local;
        }

        @Override
        public void run() {
            try {
                if (local.isRunning()) {
                    local.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
