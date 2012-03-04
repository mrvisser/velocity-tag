package ca.mrvisser.velocitytag.examples.helloworld.taglib;

import java.util.HashMap;
import java.util.Map;

import ca.mrvisser.velocitytag.api.VelocityTag;

/**
 * A tag that says hello to somebody or some thing.
 * 
 * @author branden
 */
public class HelloWorldTag extends VelocityTag {
	private static final long serialVersionUID = 1L;

	private String who = null;
	
	/* (non-Javadoc)
	 * @see ca.mrvisser.velocitytag.api.VelocityTag#buildContext()
	 */
	@Override
	protected Map<String, Object> buildContext() {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("who", getWho());
		return context;
	}

	/* (non-Javadoc)
	 * @see ca.mrvisser.velocitytag.api.VelocityTag#reset()
	 */
	@Override
	protected void reset() {
		setWho(null);
	}

	/**
	 * @return the who
	 */
	public String getWho() {
		return who;
	}

	/**
	 * @param who the who to set
	 */
	public void setWho(String who) {
		this.who = who;
	}
}
