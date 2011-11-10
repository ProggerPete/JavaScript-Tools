package net.dynamic_tools.model;

import net.dynamic_tools.exception.CircularDependencyException;
import org.junit.Test;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/9/11
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSResourceTest {
    private File dummyFile = new File("test");

//    @Test
//    public void jsResourcesWithNoDependenciesShouldBeOrderedByTheirFullName() throws Exception {
//        JSResource jsResourceOne = new JSResource(dummyFile, "resource1");
//        JSResource jsResourceTwo = new JSResource(dummyFile, "resource2");
//
//        assertTrue(jsResourceOne.compareTo(jsResourceTwo) < 0);
//        assertTrue(jsResourceTwo.compareTo(jsResourceOne) > 0);
//    }
//
//    @Test
//    public void jsResourcesDependenciesAreOrderedBeforeTheirDependents() throws Exception {
//        JSResource jsResourceOne = new JSResource(dummyFile, "resource1");
//        JSResource jsResourceTwo = new JSResource(dummyFile, "resource2");
//
//        jsResourceOne.addDependency(jsResourceTwo);
//
//        assertTrue(jsResourceOne.compareTo(jsResourceTwo) > 0);
//        assertTrue(jsResourceTwo.compareTo(jsResourceOne) < 0);
//    }
//
//    @Test
//    public void nestedJSResourceDependenciesAreHonored() throws Exception {
//        JSResource jsResourceOne = new JSResource(dummyFile, "resource1");
//        JSResource jsResourceTwo = new JSResource(dummyFile, "resource2");
//        JSResource jsResourceThree = new JSResource(dummyFile, "resource3");
//
//        jsResourceOne.addDependency(jsResourceTwo);
//        jsResourceTwo.addDependency(jsResourceThree);
//
//        assertTrue(jsResourceOne.compareTo(jsResourceThree) > 0);
//        assertTrue(jsResourceThree.compareTo(jsResourceOne) < 0);
//    }

    @Test(expected=CircularDependencyException.class)
    public void circularDependenciesAreProhibited() throws CircularDependencyException {
        JSResource jsResourceOne = new JSResource(dummyFile, "resource1");
        JSResource jsResourceTwo = new JSResource(dummyFile, "resource2");

        jsResourceOne.addDependency(jsResourceTwo);
        jsResourceTwo.addDependency(jsResourceOne);
    }
}
