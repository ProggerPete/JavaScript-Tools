package net.dynamic_tools.service.combiner;

import net.dynamic_tools.exception.UnableToWriteJSResourcesException;
import net.dynamic_tools.model.JSResource;

import java.io.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/26/11
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSResourceConcatenatingCombiner implements JSResourceCombiner {
	private static final byte[] CARRIAGE_RETURN = System.getProperty("line.separator").getBytes();
	private static final byte[] FILE_HEADER = "// RESOURCE : ".getBytes();

	@Override
	public void writeJSResourcesToOutputStream(List<JSResource> jsResources, OutputStream outputStream) throws UnableToWriteJSResourcesException {
		try {
		for (JSResource jsResource: jsResources) {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(jsResource.getJsResourceFile()));
			outputStream.write(FILE_HEADER);
			outputStream.write(jsResource.getName().getBytes());
			outputStream.write(CARRIAGE_RETURN);
			int chr;
			while ((chr = bufferedInputStream.read()) != -1) {
				outputStream.write(chr);
			}
			outputStream.write(CARRIAGE_RETURN);
			bufferedInputStream.close();
		}
		outputStream.flush();
		} catch (FileNotFoundException e) {
			throw new UnableToWriteJSResourcesException(e.getMessage(), e);
		} catch (IOException e) {
			throw new UnableToWriteJSResourcesException(e.getMessage(), e);
		}
	}
}
