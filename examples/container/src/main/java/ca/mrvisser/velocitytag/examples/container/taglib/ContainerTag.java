package ca.mrvisser.velocitytag.examples.container.taglib;

import java.util.HashMap;
import java.util.Map;

import ca.mrvisser.velocitytag.api.VelocityBodyTag;

/**
 * A tag that wraps its inner content into a container.
 * 
 * @author branden
 */
public class ContainerTag extends VelocityBodyTag {
	private static final long serialVersionUID = 1L;

	private String id;
	private String styleClass;
	private String title;
	
	/* (non-Javadoc)
	 * @see ca.mrvisser.velocitytag.api.VelocityTag#buildContext()
	 */
	@Override
	protected Map<String, Object> buildContext() {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("id", getId());
		context.put("styleClass", getStyleClass());
		context.put("title", getTitle());
		return context;
	}

	/* (non-Javadoc)
	 * @see ca.mrvisser.velocitytag.api.VelocityTag#reset()
	 */
	@Override
	protected void reset() {
		id = null;
		styleClass = null;
		title = null;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the styleClass
	 */
	public String getStyleClass() {
		return styleClass;
	}

	/**
	 * @param styleClass the styleClass to set
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
