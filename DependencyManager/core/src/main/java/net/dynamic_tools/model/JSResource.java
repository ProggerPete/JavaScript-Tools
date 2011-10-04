package net.dynamic_tools.model;

import java.io.File;
import java.net.URI;
import java.security.InvalidParameterException;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/9/11
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSResource extends NamedResource<JSResource> {
    private File jsResourceFile;

    public JSResource(File jsResourceFile, String fullName) {
        super(fullName);
        if (fullName == null || jsResourceFile == null) {
            throw new InvalidParameterException("You must specify the full name and file for the resource.");
        }
        this.jsResourceFile = jsResourceFile;
    }

    public File getJsResourceFile() {
        return jsResourceFile;
    }

	public URI getJsResourceURI() {
        return jsResourceFile.toURI();
    }
}
