/**
 * Copyright (c) 2011, Branden Visser (mrvisser at gmail dot com)
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package ca.mrvisser.velocitytag.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * A JSP tag that provides the ability to render tag content from a velocity template.
 * <p>
 * The rendering of the tag content is powered by a velocity template stored in your classpath. The template path
 * is specified by implementing the {@link #getTemplateReference()} method for your particular tag. Simply put, you
 * need to provide two velocimacros in your template: {@code doStartTag} and {@code doEndTag}. Here is an example
 * template file that can be used with with this Tag implementation:
 * <p>
 * <pre>
 * #macro(doStartTag)
 *   &lt;span&gt;Hello World!
 * #end
 * 
 * #macro(doEndTag)
 *   &lt;/span&gt;
 * #end
 * </pre>
 * <p>
 * The context elements your template macros have access to when rendering can be provided by implementing the
 * {@link #getContext()} method.
 * <p>
 * This is a simple implementation that only supports tags that do not require body content or iteration. If you do
 * require body content, refer to {@link VelocityBodyTag}. In the latter case, refer to {@link VelocityIteratorTag}.
 * <p>
 * The life-cycle of this tag can be expressed in the following pseudo-code:
 * <p>
 * <pre>
 * if (evaluateTag()) {
 *  context = getContext();
 *  render #doStartTag() macro with context
 *  render #doEndTag() macro with context
 * }
 * 
 * if (evaluatePage()) {
 *  reset()
 *  exit with {@link javax.servlet.jsp.tagext.Tag#EVAL_PAGE}
 * } else {
 *  reset()
 *  exit with {@link javax.servlet.jsp.tagext.Tag#SKIP_PAGE}
 * }
 * </pre>
 * 
 * @author Branden
 */
public abstract class VelocityTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	private final static String START_TAG_VM_NAME = "doStartTag";
	private final static String END_TAG_VM_NAME = "doEndTag";
	
	boolean evaluateTag;
	VelocityEngine engine;
	Map<String, Object> context;
	
	public VelocityTag() {
		try {
			engine = new VelocityEngine();
			engine.init(getConfiguration());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		evaluateTag = evaluateTag();
		if (evaluateTag) {
			context = buildContext();
			renderVelocimacro(START_TAG_VM_NAME, new VelocityContext(context), pageContext.getOut());
		}
		return SKIP_BODY;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		if (evaluateTag)
			renderVelocimacro(END_TAG_VM_NAME, new VelocityContext(context), pageContext.getOut());
		int result = (evaluatePage()) ? EVAL_PAGE : SKIP_PAGE;
		reset();
		context = null;
		return result;
	}

	/**
	 * Specifies whether or not the tag should be evaluated. If {@code false}, neither the doStartTag or doEndTag
	 * macros will be rendered. However, {@link #evaluatePage()} will be executed afterward to determine if the
	 * rest of the page should be rendered.
	 * 
	 * @return {@code true} if the tag should be rendered, {@code false} otherwise.
	 */
	protected boolean evaluateTag() {
		return true;
	}
	
	/**
	 * Specifies whether or not the rest of the page should be evaluated.
	 * 
	 * @return {@code true} if the rest of the page should be evaluated by the JSP processor, {@code false} otherwise.
	 */
	protected boolean evaluatePage() {
		return true;
	}
	
	/**
	 * The path on the class-path from which the tag's velocity template may be loaded.
	 * 
	 * @return
	 */
	protected abstract String getTemplateReference();
	
	/**
	 * Build a Map from which the VelocityContext will be derived when rendering the tag's template macros.
	 * 
	 * @return
	 */
	protected abstract Map<String, Object> buildContext();
	
	/**
	 * Reset the tag state to its initial form and clean memory resources. In some (most?) servlet containers, instances of Tag's may be
	 * pooled. If your tag changes state while rendering a template or holds on to heavy resources, it's important to reset that state to
	 * its initial configuration using this method. Refer to the life-cycle of the {@link VelocityTag} to see how this method is used.
	 */
	protected abstract void reset();

	/**
	 * Convenience method to render a velocimacro, and output it into the writer. The convenience is that exceptions are wrapped in a
	 * JspException and escalated.
	 * 
	 * @param template The template to render
	 * @param context The context that the template will use
	 * @param out The rendered content will be output here
	 * @throws JspException Thrown if <b>any</b> exception occurs when attempting to render the template.
	 */
	protected void renderVelocimacro(String vmName, VelocityContext context, Writer out) throws JspException {
		try {
			engine.invokeVelocimacro(vmName, getClass().getName()+"#"+vmName, new String[0], context, out);
		} catch (Exception e) {
			throw new JspException(e);
		}
	}
	
	private Properties getConfiguration() throws IOException {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ca/mrvisser/velocitytag/config/velocitytag.properties");
		Properties props = new Properties();
		props.load(stream);
		props.put("velocimacro.library", getTemplateReference());
		return props;
	}
}
