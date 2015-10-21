package com.example.download;

import com.example.util.MarketProvider;

import android.content.ContentValues;



public class UpgradeInfo {
    public String name;
	public String pid;
	public String versionName;
	public int versionCode;
	public String pkgName;
	public int update;

	public ContentValues getContentValues() {
		final ContentValues values = new ContentValues();
		values.put(MarketProvider.COLUMN_P_ID, pid);
		values.put(MarketProvider.COLUMN_P_NEW_VERSION_NAME, versionName);
		values.put(MarketProvider.COLUMN_P_NEW_VERSION_CODE, versionCode);
		values.put(MarketProvider.COLUMN_P_PACKAGE_NAME, pkgName);
		values.put(MarketProvider.COLUMN_P_IGNORE, update);
		return values;
	}
}
