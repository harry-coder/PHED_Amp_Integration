package com.fedco.mbc.utils;

/**
 * Invoked in case of any specific action on custom dialog click
 */
public interface OnCustomDialogClick {
	/**
	 * Called on custom dialog button click
	 */
	public void onCustomDialogClickView(boolean isPositiveButtonClick);
}