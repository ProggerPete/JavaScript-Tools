package net.dynamic_tools.jsdependency.filter;


import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/30/11
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSCompressionFilter implements Filter {

	private int linebreak = -1;
	private boolean munge = true;
	private boolean verbose = true;
	private boolean preserveAllSemiColons = true;
	private boolean disableOptimizations = true;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		JavaScriptCompressionResponseWrapper javaScriptCompressionResponseWrapper = new JavaScriptCompressionResponseWrapper((HttpServletResponse) servletResponse);
		filterChain.doFilter(servletRequest, javaScriptCompressionResponseWrapper);

		//		javaScriptCompressionResponseWrapper.getOutputStream();

		PrintWriter pw;

	}

	@Override
	public void destroy() {
	}

	private class JavaScriptCompressionResponseWrapper extends HttpServletResponseWrapper {

		private ServletOutputStream servletOutputStream;

		private final StringBuilder builder = new StringBuilder();

		public JavaScriptCompressionResponseWrapper(final HttpServletResponse httpServletResponse) throws IOException {
			super(httpServletResponse);

			servletOutputStream = new ServletOutputStream() {
				@Override
				public void write(int b) throws IOException {
					builder.append((char) b);
				}

				@Override
				public void flush() throws IOException {
					ServletOutputStream realOutputStream = httpServletResponse.getOutputStream();

					Reader source = new StringReader(builder.toString());

					JavaScriptCompressor compressor = new JavaScriptCompressor(source, new ErrorReporter() {
						@Override
						public void warning(String s, String s1, int i, String s2, int i1) {
							System.out.println(s);
						}

						@Override
						public void error(String s, String s1, int i, String s2, int i1) {
							System.out.println(s);
						}

						@Override
						public EvaluatorException runtimeError(String s, String s1, int i, String s2, int i1) {
							System.out.println(s);

							return new EvaluatorException(s, s1, i, s2, i1);
						}
					});

					Writer writer = new OutputStreamWriter(realOutputStream);

					compressor.compress(writer, linebreak, munge, verbose, preserveAllSemiColons, disableOptimizations);

					writer.flush();
					writer.close();
					super.flush();
				}
			};
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			throw new IOException("Unsupported operation. The JSCompressionFilter doesn't support getWriter at the moment.");
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return servletOutputStream;
		}
	}
}

