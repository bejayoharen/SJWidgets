/**
 *
 * This file is part of the SJWidget library.
 * (c) 2005-2012 Bjorn Roche
 * Development of this library has been supported by Indaba Media (http://www.indabamusic.com)
 * and XO Audio (http://www.xoaudio.com)
 *
 * for copyright and sharing permissions, please see the COPYING.txt file which you should
 * have recieved with this file.
 *
 */
package com.xowave.sjwidget.help;

import org.jdom.*;


import com.xowave.util.XMLUtil;



/**
 * @author bjorn
 * Class that conveniently contains and returns all text needed for displaying within-window
 * help for a given component.
 */
public class ComponentHelpData {
	private final String helpKey;
	private final String toolTipText;
	private final String oneLineDescription;
	private final String name;
	private final String multiLineDescription;

	ComponentHelpData( ComponentHelpData chd ) {
		if( chd == null ) {
			this.helpKey              = "";
			this.toolTipText          = "";
			this.oneLineDescription   = "";
			this.name                 = "";
			this.multiLineDescription = "";
		} else {
			this.helpKey              = chd.helpKey;
			this.toolTipText          = chd.toolTipText;
			this.oneLineDescription   = chd.oneLineDescription;
			this.name                 = chd.name;
			this.multiLineDescription = chd.multiLineDescription;
		}
	}
	ComponentHelpData( String helpKey, String oneLineDescription, String name, String multilineDescription ) {
		this.helpKey = helpKey;
		this.toolTipText = null;
		this.oneLineDescription = oneLineDescription;
		this.name = name;
		this.multiLineDescription = multilineDescription;
		debugPrint();
	}
	ComponentHelpData( Element helpElement ) {
		if( !helpElement.getName().equals("Component") )
			throw new RuntimeException();
		helpKey = helpElement.getAttributeValue("id");
		if( helpKey == null )
			throw new RuntimeException( "Missing helpkey/Component ID in file." );
		name = helpElement.getChildText( "Name" );
		oneLineDescription = helpElement.getChildText( "OneLineDescription" );
		multiLineDescription = XMLUtil.getHTML( helpElement.getChild( "MultiLineDescription" ) );
		toolTipText = helpElement.getChildText( "ToolTipText" );
		debugPrint();
	}
	private void debugPrint() {
		if( HelpComponentRegistry.DEBUG ) {
			System.out.println( "<------ Read HelpData for " + name );
			System.out.println( "<--- help key: " + helpKey );
			System.out.println( "<--- tool tip text: " + oneLineDescription );
			System.out.println( "<--- One Line Description: " + oneLineDescription );
			System.out.println( "<--- MultiLineDescription:\n" + multiLineDescription );
		}
	}
	public String getHelpKey() {return helpKey; }
	public String getToolTipText() { return toolTipText; }
	public String getOneLineDescription() { return oneLineDescription; }
	public String getName() { return name; }
	public String getMultiLineDescription() { return multiLineDescription; }
}
