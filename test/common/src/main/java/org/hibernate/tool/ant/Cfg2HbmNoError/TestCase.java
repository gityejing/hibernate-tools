package org.hibernate.tool.ant.Cfg2HbmNoError;

import java.io.File;

import org.hibernate.tools.test.util.AntUtil;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.hibernate.tools.test.util.ResourceUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCase {
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File destinationDir = null;
	private File resourcesDir = null;
	
	@Before
	public void setUp() {
		destinationDir = new File(temporaryFolder.getRoot(), "destination");
		destinationDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		JdbcUtil.createDatabase(this);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testCfg2HbmNoError() {

		String resourcesLocation = ResourceUtil.getResourcesLocation(this);
		String[] resources = new String[] {"build.xml"};
		ResourceUtil.createResources(this, resourcesLocation, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		ResourceUtil.createResources(this,  null, new String[] { "/hibernate.properties" }, resourcesDir);
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());

		File hbmxml = new File(destinationDir, "VerySimpleTable.hbm.xml");
		Assert.assertFalse(hbmxml.exists());
		
		project.executeTarget("testCfg2HbmNoError");
		
		Assert.assertTrue(hbmxml.exists());
		Assert.assertTrue(FileUtil
				.findFirstString("class", hbmxml)
				.contains("VerySimpleTable"));

	}
	
}
