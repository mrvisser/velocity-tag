/**
 * 
 */
package ca.mrvisser.velocitytag.api;

import javax.servlet.jsp.JspException;

/**
 * An extension of {@link VelocityTag} that supports body evaluation. The life-cycle of this extensions is only
 * slightly different from the parent:
 * <p>
 * <pre>
 * if (evaluateTag()) {
 * 	context = getContext();
 * 	render #doStartTag() macro with context
 * 	if (evaluateBody()) {
 * 		execute JSP body-processing cycle
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
public abstract class VelocityBodyTag extends VelocityTag {
	private static final long serialVersionUID = 1L;

	public VelocityBodyTag() throws Exception {
		super();
	}

	/* (non-Javadoc)
	 * @see ca.mrvisser.velocitytag.api.VelocityTag#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();
		return (evaluateTag && evaluateBody()) ? EVAL_BODY_INCLUDE : SKIP_BODY;
	}
	
	/**
	 * Specifies whether or not the body of the tag should be evaluated.
	 * 
	 * @return {@code true} if the body of the tag should be rendered, {@code false} otherwise.
	 */
	protected boolean evaluateBody() {
		return true;
	}
}
