package net.dynamic_tools.service.combiner;

import net.dynamic_tools.exception.UnableToWriteJSResourcesException;
import net.dynamic_tools.model.JSResource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/26/11
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSResourceScriptTagCombiner implements JSResourceCombiner {
	private static final byte[] SCRIPT_TAG_START = "<script type=\"text/javascript\" src=\"".getBytes();
	private static final byte[] SCRIPT_TAG_END = "\"></script>\n".getBytes();
	private byte[] jsPath;

	public void setJsPath(String jsPath) {
		if (!jsPath.endsWith("/")) {
			jsPath += "/";
		}
		this.jsPath = jsPath.getBytes();
	}

	@Override
	public void writeJSResourcesToOutputStream(List<JSResource> jsResources, OutputStream outputStream) throws UnableToWriteJSResourcesException {
		try {
			for (JSResource jsResource : jsResources) {
				outputStream.write(SCRIPT_TAG_START);
				outputStream.write(jsPath);
				outputStream.write(jsResource.getName().replace('.', '/').getBytes());
				outputStream.write(SCRIPT_TAG_END);
			}
			outputStream.flush();
		} catch (IOException e) {
			throw new UnableToWriteJSResourcesException(e.getMessage(), e);
		}
	}
}
