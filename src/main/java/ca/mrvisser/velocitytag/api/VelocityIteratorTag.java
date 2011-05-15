/**
 * 
 */
package ca.mrvisser.velocitytag.api;

import java.util.Map;

import javax.servlet.jsp.JspException;

import org.apache.velocity.Template;
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
 * 	context = getContext();
 * 	render #doStartTag() macro with context
 * 	if (evaluateBody()) {
 * 		render #doBeforeBody() macro with context
 * 		execute JSP body-processing cycle
 * 		render #doAfterBody() macro with context
 * 		while (evaluateBodyAgain(context)) {
 * 			render #doBeforeBody() macro with context
 * 			execute JSP body-processing cycle
 * 			render #doAfterBody() macro with context
 * 		}
 * 	}
 * 	render #doEndTag() macro with context
 * }
 * 
 * if (evaluatePage()) {
 * 	exit with {@link javax.servlet.jsp.tagext.Tag#EVAL_PAGE}
 * } else {
 * 	exit with {@link javax.servlet.jsp.tagext.Tag#SKIP_PAGE}
 * }
 * </pre>
 * 
 * @author Branden
 */
public abstract class VelocityIteratorTag extends VelocityBodyTag {
	private static final long serialVersionUID = 1L;

	private final static String BEFORE_BODY_TEMPLATE = "ca/mrvisser/velocitytag/config/do-before-body.vm";
	private final static String AFTER_BODY_TEMPLATE = "ca/mrvisser/velocitytag/config/do-after-body.vm";
	
	private Template beforeBodyTemplate;
	private Template afterBodyTemplate;
	
	public VelocityIteratorTag() throws Exception {
		super();
		beforeBodyTemplate = engine.getTemplate(BEFORE_BODY_TEMPLATE);
		afterBodyTemplate = engine.getTemplate(AFTER_BODY_TEMPLATE);
	}

	/* (non-Javadoc)
	 * @see ca.mrvisser.velocitytag.api.VelocityBodyTag#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		int result = super.doStartTag();
		
		if (result == EVAL_BODY_INCLUDE) {
			render(beforeBodyTemplate, new VelocityContext(context), pageContext.getOut());
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doAfterBody()
	 */
	@Override
	public int doAfterBody() throws JspException {
		render(afterBodyTemplate, new VelocityContext(context), pageContext.getOut());
		
		if (evaluateBodyAgain(context)) {
			render(beforeBodyTemplate, new VelocityContext(context), pageContext.getOut());
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
	 * @param context The current template process context.
	 * @return {@code true} if the body should be evaluated again, {@code false} otherwise.
	 */
	protected boolean evaluateBodyAgain(Map<String, Object> context) {
		return false;
	}
}
