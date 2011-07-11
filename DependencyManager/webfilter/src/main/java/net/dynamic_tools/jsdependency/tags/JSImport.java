package net.dynamic_tools.jsdependency.tags;

import net.dynamic_tools.service.ResourceDependencyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

@Configurable
public class JSImport extends TagSupport {
	@Autowired
	ResourceDependencyManager resourceDependencyManager;

    // called at start of tag
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.print("test" + (resourceDependencyManager.toString()));
        }
        catch (java.io.IOException e) {
            throw new JspException("IOException while writing to client: " + e);
        }

        return SKIP_BODY;
    }

    // called at end of tag
    public int doEndTag() {
        return EVAL_PAGE;
    }
}