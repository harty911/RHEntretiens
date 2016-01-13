package org.harty911.rhtool.ui.resources;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class Icons {

	public static final String LOGO_CAL = "logoCAL.gif";
	
	public static final String USER_ADMIN	= "user_admin.png";
	public static final String COLLABS		= "collabs_tab.gif";
	public static final String TALKS		= "talks_tab.gif";
	public static final String HELP			= "help.gif";
	public static final String IMPORT 		= "import.gif";
	public static final String EXPORT       = "export.gif";
	
	public static final String CREATE	= "add_obj.gif";
	public static final String EDIT		= "edit_obj.gif";
	public static final String DELETE	= "delete_obj.gif";
	public static final String TRASH 	= "trash.gif";

	public static final String COPY 	= "copy.gif";
	public static final String PASTE 	= "paste.gif";

	public static final String UP 	= "up.gif";
	public static final String DOWN	= "down.gif";

	
	private static ImageRegistry imgReg;

	public static ImageDescriptor getDescriptor( String imgName) {
		_register(imgName);
		return imgReg.getDescriptor(imgName);
	}
	
	public static Image getImage( String imgName) {
		_register(imgName);
		return imgReg.get(imgName);
	}

	private static void _register(String imgName) {
		if( imgReg==null)
			imgReg = new ImageRegistry(Display.getDefault());
		if( imgReg.getDescriptor(imgName)==null)
			imgReg.put(imgName, ImageDescriptor.createFromFile( Icons.class, imgName));
	}
}
