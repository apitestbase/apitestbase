package io.apitestbase.core.teststep;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This is actually a factory and delegator instead of the actual runner.
 */
public class IIBTeststepActionRunner extends TeststepActionRunner {
    private static IIBTeststepActionRunnerClassLoader iib100ClassLoader;
    static {
        URL[] iib100URLs;
        File userDir = new File(System.getProperty("user.dir"));
        try {
            iib100URLs = new URL[] {
                    new File(userDir, "lib/iib/v100/IntegrationAPI.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v100/jetty-io.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v100/jetty-util.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v100/websocket-api.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v100/websocket-client.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v100/websocket-common.jar").toURI().toURL()
            };
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to initialize " + IIBTeststepActionRunner.class.getName(), e);
        }
        iib100ClassLoader = new IIBTeststepActionRunnerClassLoader(iib100URLs, IIBTeststepActionRunner.class.getClassLoader());
    }

    @Override
    public TeststepActionRunResult run() throws Exception {
        Class actualRunnerClass = Class.forName("io.apitestbase.core.teststep.IIB100TeststepActionRunner", false, iib100ClassLoader);
        Constructor<TeststepActionRunner> constructor = actualRunnerClass.getConstructor();
        TeststepActionRunner actualRunner = constructor.newInstance();
        actualRunner.setTeststep(getTeststep());
        actualRunner.setTestcaseRunContext(getTestcaseRunContext());
        actualRunner.setTestcaseIndividualRunContext(getTestcaseIndividualRunContext());

        return actualRunner.run();
    }
}