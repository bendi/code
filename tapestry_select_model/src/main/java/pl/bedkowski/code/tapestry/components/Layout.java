package pl.bedkowski.code.tapestry.components;

import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.SymbolConstants;

/**
 * Layout component for pages of application tapestry.
 */
@Import(stylesheet = "context:layout/layout.css")
public class Layout
{
	/**
	 * The page title, for the <title> element and the <h1> element.
	 */
	@SuppressWarnings("unused")
	@Property
	@Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
	private String title;

	@Property
	private String pageName;

	@SuppressWarnings("unused")
	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String sidebarTitle;

	@SuppressWarnings("unused")
	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private Block sidebar;

	@Inject
	private ComponentResources resources;

	@SuppressWarnings("unused")
	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;


	public String getClassForPageName()
	{
		return resources.getPageName().equalsIgnoreCase(pageName)
				? "current_page_item"
				: null;
	}

	public String[] getPageNames()
	{
		return new String[]{"Index", "About", "Contact"};
	}
}
