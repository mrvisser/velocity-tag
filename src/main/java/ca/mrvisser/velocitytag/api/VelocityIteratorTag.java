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

import java.util.Map;

import javax.servlet.jsp.JspException;

import org.apache.velocity.VelocityContext;

/**
 * An extension of the {@link VelocityBodyTag} that supports multiple executions of the tag body. This implementation
 * introduces 2 new velocimacros that can be used: {@code doBeforeBody} and {@code doAfterBody}. An example usage is
 * as follows:
 * <p>
 * <pre>
 * #macro(doStartTag)
 *   &lt;table&gt;
 *     &lt;caption&gt;$caption&lt;/caption&gt;
 * #end
 * 
 * #macro(doBeforeBody)
 *   &lt;tr&gt;
 * #end
 * 
 * #macro(doAfterBody)
 *   &lt;/tr&gt;
 * #end
 * 
 * #macro(doEndTag)
 *   &lt;/table&gt;
 * #end
 * </pre>
 * <p>
 * The life-cycle of this element goes as follows:
 * <p>
 * <pre>
 * if (evaluateTag()) {
 *  context = getContext();
 *  render #doStartTag() macro with context
 *  if (evaluateBody()) {
 *    render #doBeforeBody() macro with context
 *    execute JSP body-processing cycle
 *    render #doAfterBody() macro with context
 *    while (evaluateBodyAgain(context)) {
 *      render #doBeforeBody() macro with context
 *      execute JSP body-processing cycle
 *      render #doAfterBody() macro with context
 *    }
 *  }
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
public abstract class VelocityIteratorTag extends VelocityBodyTag {
	private static final long serialVersionUID = 1L;

	private final static String BEFORE_BODY_VM_NAME = "doBeforeBody";
	private final static String AFTER_BODY_VM_NAME = "doAfterBody";

	/* (non-Javadoc)
	 * @see ca.mrvisser.velocitytag.api.VelocityBodyTag#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		int result = super.doStartTag();
		if (result == EVAL_BODY_INCLUDE) {
			renderVelocimacro(BEFORE_BODY_VM_NAME, new VelocityContext(context), pageContext.getOut());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doAfterBody()
	 */
	@Override
	public int doAfterBody() throws JspException {
		renderVelocimacro(AFTER_BODY_VM_NAME, new VelocityContext(context), pageContext.getOut());
		if (evaluateBodyAgain(context)) {
			renderVelocimacro(BEFORE_BODY_VM_NAME, new VelocityContext(context), pageContext.getOut());
			return EVAL_BODY_AGAIN;
		} else {
			return SKIP_BODY;
		}
	}
	
	/**
	 * Determines whether or not the body of the tag should be re-evaluated. Over multiple iterations,
	 * it is expected that the template context may change, so the {@code context} is provided at each
	 * iteration so that it may be altered.
	 * 
	 * @param context The current template context.
	 * @return {@code true} if the body should be evaluated again, {@code false} otherwise.
	 */
	protected boolean evaluateBodyAgain(Map<String, Object> context) {
		return false;
	}

}
