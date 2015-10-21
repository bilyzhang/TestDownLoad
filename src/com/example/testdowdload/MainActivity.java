package com.example.testdowdload;

import java.io.File;

import com.example.session.Constants;
import com.example.ui.AppsManagerAdapter;
import com.example.ui.BaseActivity;
import com.example.ui.PreloadActivity;
import com.example.ui.AppsManagerAdapter.AppItem;
import com.example.util.TopBar;
import com.example.util.Utils;
import com.example.widget.LoadingDrawable;
import com.mappn.gfan.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;


public class MainActivity extends BaseActivity{
    private final static int CONTEXT_MENU_DELETE_FILE = 0;
    private final static int CONTEXT_MENU_IGNORE_UPDATE = 1;
    
    private FrameLayout mLoading;
    private ProgressBar mProgress;
    private ListView mList;
    private AppsManagerAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps_manager_layout);
		
	       doInitView(savedInstanceState);
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
	        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
	        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
	        filter.addDataScheme("package");
	      //  registerReceiver(mInstallReceiver, filter);
	}
	
	   private BroadcastReceiver mInstallReceiver = new BroadcastReceiver() {

	        @Override
	        public void onReceive(Context context, Intent intent) {

	            final String action = intent.getAction();
	            final String packageName = intent.getData().getSchemeSpecificPart();

	            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {

	              //  mAdapter.installAppWithPackageName(packageName);

	            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {

	              //  mAdapter.removedAppWithPackageName(packageName);

	            }
	        }
	    };

	
	   public boolean doInitView(Bundle savedInstanceState) {

	        TopBar.createTopBar(getApplicationContext(), 
	                new View[] { 
	                    findViewById(R.id.top_bar_title),
	                    findViewById(R.id.top_bar_files),
	                    findViewById(R.id.top_bar_search) 
	                }, 
	                new int[] { 
	                    View.VISIBLE, 
	                    View.VISIBLE, 
	                    View.VISIBLE 
	                },
	                getString(R.string.app_manager_title));

	        mLoading = (FrameLayout) findViewById(R.id.loading);
	        mProgress = (ProgressBar) mLoading.findViewById(R.id.progressbar);
	        mProgress.setIndeterminateDrawable(new LoadingDrawable(getApplicationContext()));
	        mProgress.setVisibility(View.VISIBLE);
	        
	        mList = (ListView) findViewById(android.R.id.list);
	        mList.setEmptyView(mLoading);
	        //mList.setOnItemClickListener(this);
	        mList.setItemsCanFocus(true);
	        mAdapter = doInitListAdapter();
	        //mList.setAdapter(mAdapter);
	        registerForContextMenu(mList);

	        return true;
	    }
	    public AppsManagerAdapter doInitListAdapter() {
	        return new AppsManagerAdapter(getApplicationContext(), null);
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}
