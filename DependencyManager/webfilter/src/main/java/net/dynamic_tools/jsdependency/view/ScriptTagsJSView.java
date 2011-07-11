package net.dynamic_tools.jsdependency.view;

import net.dynamic_tools.model.JSResource;
import net.dynamic_tools.service.combiner.JSResourceScriptTagCombiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.util.List;
import java.util.Map;

/**
* Created by IntelliJ IDEA.
* User: Peter
* Date: 4/29/11
* Time: 11:30 PM
* To change this template use File | Settings | File Templates.
*/
@Component
public class ScriptTagsJSView implements View {
	@Autowired
	private JSResourceScriptTagCombiner jsResourceScriptTagCombiner;

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<JSResource> jsResources = (List<JSResource>) model.get("jsResources");
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
		jsResourceScriptTagCombiner.writeJSResourcesToOutputStream(jsResources, bufferedOutputStream);
	}
}
