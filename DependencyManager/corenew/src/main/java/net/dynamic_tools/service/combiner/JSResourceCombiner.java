package net.dynamic_tools.service.combiner;

import net.dynamic_tools.exception.UnableToWriteJSResourcesException;
import net.dynamic_tools.model.JSResource;

import java.io.OutputStream;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/30/11
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface JSResourceCombiner {
	public void writeJSResourcesToOutputStream(List<JSResource> jsResources, OutputStream outputStream) throws UnableToWriteJSResourcesException;
}
