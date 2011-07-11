package net.dynamic_tools.javascript.packer;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 24/06/11
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class Packer {
	public static void main(String args[]) {
		Context ctx = Context.enter();
		Scriptable scope = ctx.initStandardObjects();
		String code = "x = 'hello';";
//		loadJSFile(ctx, scope, "base2.js");
//		loadJSFile(ctx, scope, "base2-dom.js");

		loadJSFile(ctx, scope, "packer2.js");
//		loadJSFile(ctx, scope, "Words.js");

		// pack("(function (){alert('hello world');})();", 62, 1, 0);
		code = "pack(\"(function (){alert('hello world');})();\", 62, 1, 0);";
		Object result = ctx.evaluateString(scope, code, "<code>", 1, null);
		System.out.println(Context.toString(result));
		Context.exit();
	}

	private static void loadJSFile(Context ctx, Scriptable scope, String fileName) {
		InputStream inputStream = Packer.class.getClassLoader().getResourceAsStream(fileName);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		try {
			Object result = ctx.evaluateReader(scope, inputStreamReader, "<code>", 1, null);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}
}
