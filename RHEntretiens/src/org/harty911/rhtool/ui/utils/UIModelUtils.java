package org.harty911.rhtool.ui.utils;

import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.RHModelObject;

public class UIModelUtils {
	
	/**
	 * refresh object from model and check if not deleted
	 * return true if object is valid
	 */
	public static boolean refreshAndCheck( RHModelObject rhobj) {
		RHModel model = RHToolApp.getModel();
		if( rhobj!=null) {
			model.refresh( rhobj);
			if( !rhobj.isDeleted())
				return true;
		}
		return false;
	}

}
