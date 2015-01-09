package org.harty911.rhtool.ui.utils;

import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.RHModelObject;

public class UIModelUtils {
	
	
	public static boolean refreshAndCheck( RHModelObject rhobj) {
		RHModel model = RHToolApp.getModel();
		if( rhobj!=null) {
			model.refresh( rhobj);
			if( !rhobj.isDeleted())
				return true;
		}
		RHToolApp.getWindow().updateFromModel();
		return false;
	}

}
