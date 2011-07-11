package test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 24/06/11
 * Time: 11:54
 * To change this template use File | Settings | File Templates.
 */
public class PackerTest {
	@Test
	public void homePage() throws Exception {
		final WebClient webClient = new WebClient();
		final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
		assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());

		final String pageAsXml = page.asXml();
		assertTrue(pageAsXml.contains("<body class=\"composite\">"));

		final String pageAsText = page.asText();
		assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));

		System.out.println(pageAsText);
		webClient.closeAllWindows();
	}


}
