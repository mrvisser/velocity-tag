/**
 * 
 */
package ca.mrvisser.velocitytag.api;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Branden Visser
 */
public interface TemplatableTag extends Serializable {
	
	/**
	 * A string that identifies the location of the template.
	 * 
	 * @return
	 */
	public String getTemplateReference();
	
	/**
	 * Build the data object that the tag will use to render its content. 
	 * 
	 * @return
	 */
	public Map<String, Object> getContext();

}
