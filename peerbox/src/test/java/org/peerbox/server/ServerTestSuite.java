package org.peerbox.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.peerbox.server.servlets.VersionServletTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	ServerTest.class,
	VersionServletTest.class
})
public class ServerTestSuite {

}
