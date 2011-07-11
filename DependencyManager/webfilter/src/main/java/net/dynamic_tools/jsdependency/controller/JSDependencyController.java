package net.dynamic_tools.jsdependency.controller;

import net.dynamic_tools.jsdependency.view.ConcatenatedJSView;
import net.dynamic_tools.jsdependency.view.ScriptTagsJSView;
import net.dynamic_tools.model.JSResource;
import net.dynamic_tools.service.combiner.JSResourceScriptTagCombiner;
import net.dynamic_tools.service.ResourceDependencyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/26/11
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/*")
public class JSDependencyController {

	@Autowired
	private ScriptTagsJSView scriptTagsJSView;

	@Autowired
	private ResourceDependencyManager<JSResource> resourceDependencyManager;

	@Autowired
	private JSResourceScriptTagCombiner jsResourceScriptTagCombiner;

	@Autowired
	private ConcatenatedJSView concatenatedJSView;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getJSResources(@RequestParam(value = "mode", required = false) String mode, HttpServletRequest request) {

		String uri = request.getRequestURI();
		String jsResourceName = uri.substring(uri.indexOf('/', 1) + 1).replace('/', '.');
		List<JSResource> jsResources = resourceDependencyManager.getResourcesFor(jsResourceName);

		View view = getView(mode);
		ModelAndView model = new ModelAndView(view);
		model.addObject("jsResources", jsResources);//jsResourceScriptTagCombiner.combineJSResource(jsResources));

		return model;
	}

	private View getView(String mode) {
		if (mode != null) {
			if (mode.equals("tags")) {
				return scriptTagsJSView;
			}
		}
		return concatenatedJSView;
	}

}
