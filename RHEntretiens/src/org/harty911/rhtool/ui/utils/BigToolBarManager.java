package org.harty911.rhtool.ui.utils;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.ToolBar;

public class BigToolBarManager extends ToolBarManager {

	public BigToolBarManager() {
		super();
	}

	public BigToolBarManager(int style) {
		super(style);
	}

	public BigToolBarManager(ToolBar toolbar) {
		super(toolbar);
	}

	@Override
	public void add(IAction action) {
		Assert.isNotNull(action, "Action must not be null");
		ActionContributionItem item = new ActionContributionItem(action);
		item.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		super.add(item);
	}
	

}
