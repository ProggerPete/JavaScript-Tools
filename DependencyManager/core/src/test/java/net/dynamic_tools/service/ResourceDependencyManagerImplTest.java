package net.dynamic_tools.service;

import net.dynamic_tools.model.NamedResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/11/11
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceDependencyManagerImplTest {
    ResourceDependencyManagerImpl<NamedResource> resourceDependencyManager;


    @Before
    public void setup() {
        resourceDependencyManager = new ResourceDependencyManagerImpl<NamedResource>();

        NamedResource a = new NamedResource("a");
        NamedResource b = new NamedResource("b");
        NamedResource c = new NamedResource("c");
        NamedResource d = new NamedResource("d");
        NamedResource e = new NamedResource("e");
        NamedResource f = new NamedResource("f");
        NamedResource g = new NamedResource("g");

//        resourceDependencyManager.addResource(a);
//        resourceDependencyManager.addResource(b);
//        resourceDependencyManager.addResource(c);
//        resourceDependencyManager.addResource(d);
//        resourceDependencyManager.addResource(e);
//        resourceDependencyManager.addResource(f);
//        resourceDependencyManager.addResource(g);

        resourceDependencyManager.addDependency(a, b);
        resourceDependencyManager.addDependency(a, d);
        resourceDependencyManager.addDependency(b, c);
        resourceDependencyManager.addDependency(b, e);
        resourceDependencyManager.addDependency(c, d);
        resourceDependencyManager.addDependency(c, f);
        resourceDependencyManager.addDependency(d, f);
        resourceDependencyManager.addDependency(g, e);
    }

    @Test
    public void noDependencies() {
        List<NamedResource> resources = resourceDependencyManager.getResourcesFor("f");
        assertEquals(1, resources.size());
        assertEquals("f", resources.get(0).getName());
    }

    @Test
    public void singleDependency() {
        List<NamedResource> resources = resourceDependencyManager.getResourcesFor("d");
        assertEquals(2, resources.size());
        assertEquals("f", resources.get(0).getName());
        assertEquals("d", resources.get(1).getName());
    }

	@Test
    public void multipleStartPoints() {
        List<NamedResource> resources = resourceDependencyManager.getResourcesFor("d", "g");
        assertEquals(4, resources.size());
        assertEither(resources.get(0).getName(), "f", "e");
        assertEither(resources.get(1).getName(), "f", "e", "g", "d");
        assertEither(resources.get(2).getName(), "f", "e", "g", "d");
        assertEither(resources.get(3).getName(), "g", "d");

		resources = resourceDependencyManager.getResourcesFor("b", "g");
        assertEquals(6, resources.size());
        assertEither(resources.get(0).getName(), "f", "e");
        assertEither(resources.get(1).getName(), "f", "e", "g", "d");
        assertEither(resources.get(5).getName(), "g", "b");
    }

	@Test
    public void getAllResources() {
        List<NamedResource> resources = resourceDependencyManager.getAllResources();
        assertEquals(7, resources.size());

        assertEither(resources.get(0).getName(), "e", "f");
        assertEither(resources.get(1).getName(), "e", "f");
        assertEither(resources.get(2).getName(), "g", "d");
        assertEither(resources.get(3).getName(), "g", "d", "c");
        assertEither(resources.get(4).getName(), "g", "c", "b");
        assertEither(resources.get(5).getName(), "g", "b", "a");
        assertEither(resources.get(6).getName(), "g", "a");
	}

	private void assertEither(Object actualValue, Object... options) {
		boolean hasMatch = false;
		String message = "Expected " + actualValue + " to be ";
		for (Object option : options) {
			hasMatch = hasMatch || actualValue.equals(option);
			message += option + " or ";
		}
		message = message.substring(0, message.length() -4);
		assertTrue(message, hasMatch);
	}

    @Test
    public void multiplePathsToDependency() {
        List<NamedResource> resources = resourceDependencyManager.getResourcesFor("a");
        assertEquals(6, resources.size());
        assertEither(resources.get(0).getName(), "e", "f");
        assertEither(resources.get(1).getName(), "e", "f");
        assertEquals("d", resources.get(2).getName());
        assertEquals("c", resources.get(3).getName());
        assertEquals("b", resources.get(4).getName());
        assertEquals("a", resources.get(5).getName());

        resources = resourceDependencyManager.getResourcesFor("b");
        assertEquals(5, resources.size());
        assertEither(resources.get(0).getName(), "e", "f");
        assertEither(resources.get(1).getName(), "e", "f");
        assertEquals("d", resources.get(2).getName());
        assertEquals("c", resources.get(3).getName());
        assertEquals("b", resources.get(4).getName());

        resources = resourceDependencyManager.getResourcesFor("c");
        assertEquals(3, resources.size());
        assertEquals("f", resources.get(0).getName());
        assertEquals("d", resources.get(1).getName());
        assertEquals("c", resources.get(2).getName());
    }
}
