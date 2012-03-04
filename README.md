Introduction
==============

VelocityTag is a simple abstraction of the JSP tag library that follows a "Model and View" approach to building your custom tags. Previously, writing JSP tags
seemed to be a pretty archaic exercise for me as I found myself writing HTML templates into Java strings and writing them out to the page writer
(Servlets circa. 1998, anyone?). VelocityTag redefines how you control the JSP tag life-cycle and will hopefully make maintaining your tag libraries
easier and less prone to error.

Examples
==============

Hello $world!
--------------

For the most trivial example, we will create a tag that greets a user. The custom tag will be used like this:

	<v:hello who="World" />

First, extend ``ca.mrvisser.velocitytag.api.VelocityTag`` like so:

	package ca.mrvisser.testtags;

	import java.util.HashMap;
	import java.util.Map;
	
	import ca.mrvisser.velocitytag.api.VelocityTag;
	
	public class HelloWorldTag extends VelocityTag {
		private static final long serialVersionUID = 1L;
	
		private String who;
	
		@Override
		protected Map<String, Object> buildContext() {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("who", who);
			return context;
		}
	
		@Override
		protected void reset() {
			who = null;
		}
	
		public void setWho(String who) {
			this.who = who;
		}
	
		public String getWho() {
			return who;
		}
	}

* ``reset()`` is used to clear the state of the tag after it has finished rendering. Since servlet containers may pool tag instances this is useful to clear potential memory-leaks and reset the tag state for the next execution.
* ``buildContext()`` is responsible for building the data that will be given directly to your velocity template. You'll see in the next file how the template uses the data

Second, write a velocity template for your tag and place it in a VM file that is in the same package as your java class, with the same file name:

``ca/mrvisser/testtags/HelloWorldTag.vm``

	#macro(doStartTag)
		<span>Hello, $who!
	#end
	#macro(doEndTag)
		</span>
	#end

* ``doStartTag`` velocimacro is executed in the same manner that the ``doStartTag()`` method of a JSP custom tag would be executed, except the logic for building the model was separated into the Java class
* ``$who`` variable was taken from the context built in ``buildContext()``
* ``doEndTag`` velocimacro is executed after doStartTag, similar to how JSP executes the ``doEndTag()`` method

And that's basically it. All the other JSP custom tag process apply. Such as adding the tag to your TLD file:

	<tag>
		<name>hello</name>
		<tagclass>ca.mrvisser.testtags.HelloWorldTag</tagclass>
		<attribute>
			<name>who</name>
			<required>true</required>
			<rtexprvalue>true</rtexprvalue>
		<attribute>
	</tag>

There is support for body tags (``ca.mrvisser.velocitytag.api.VelocityBodyTag``) as well as iteration (``ca.mrvisser.velocitytag.api.VelocityIteratorTag``).

I will post more examples and documentation in the near future. In the meantime, check out the code, the class comments provide great detail as well.

	