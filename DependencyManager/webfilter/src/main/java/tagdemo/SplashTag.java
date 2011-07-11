package tagdemo;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class SplashTag extends TagSupport {
    // called at start of tag
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.print("test");
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