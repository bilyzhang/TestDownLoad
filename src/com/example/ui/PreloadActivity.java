/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.ui;

import java.util.HashMap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import com.example.common.ApiAsyncTask;
import com.example.common.ApiAsyncTask.ApiRequestListener;
import com.example.common.MarketAPI;
import com.example.download.ProductDetail;
import com.example.session.Constants;
import com.mappn.gfan.R;
import com.example.util.Utils;
import com.example.widget.LoadingDrawable;



/**
 * 产品详细页预加载页面
 * 
 * @author andrew
 * @date    2011-4-19
 *
 */
public class PreloadActivity extends BaseActivity implements ApiRequestListener {

    private static final String ACTION_PID = "pid";
    private static final String ACTION_PACKAGENAME = "pkgName";
    
    private ProgressBar mProgress;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.loading);
        
        final Intent intent = getIntent();
        
        mProgress = (ProgressBar) findViewById(R.id.progressbar);
        mProgress.setIndeterminateDrawable(new LoadingDrawable(
                getApplicationContext()));
        mProgress.setVisibility(View.VISIBLE);
        
        if (checkBarcode(intent)) {
            
            String packageName = intent.getStringExtra(Constants.EXTRA_PACKAGE_NAME);
            if (TextUtils.isEmpty(packageName)) {
                // 通过产品ID来获取内�?		
                String pId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID);
                String sourceType = intent.getStringExtra(Constants.EXTRA_SOURCE_TYPE);
                if (TextUtils.isEmpty(sourceType)) {
                    sourceType = Constants.SOURCE_TYPE_GFAN;
                }
                MarketAPI.getProductDetailWithId(this, this, -1, pId, sourceType);
            } else {
                // 通过产品包名来获取内�?                MarketAPI.getProductDetailWithPackageName(this, this, -1, packageName);
            }
        }
    }
   
    /*
     * 处理二维码消�?     */
    private boolean checkBarcode(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            HashMap<String, String> params = Utils.parserUri(uri);
            if (params != null) {
                String query = params.get("p");
                if (!TextUtils.isEmpty(query)) {
                    String[] temp = query.split(":");
                    String key = temp[0];
                    String value = temp[1];
                    if (ACTION_PID.equalsIgnoreCase(key)) {
                        MarketAPI.getProductDetailWithId(this, this, -1, value,
                                Constants.SOURCE_TYPE_GFAN);
                        return false;
                    } else if (ACTION_PACKAGENAME.equalsIgnoreCase(key)) {
                        MarketAPI.getProductDetailWithPackageName(this, this, -1, value);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onSuccess(int method, Object obj) {
/*        Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
        intent.putExtra(Constants.EXTRA_PRDUCT_DETAIL, (ProductDetail) obj);
        intent.putExtra(Constants.IS_BUY, getIntent().getBooleanExtra(Constants.IS_BUY, false));
        finish();
        startActivity(intent);*/
    }
    
    @Override
    public void onError(int method, int statusCode) {

        if (ApiAsyncTask.TIMEOUT_ERROR == statusCode) {
            // 网络异常
            //Utils.makeEventToast(getApplicationContext(), getString(R.string.no_network),
            //        false);
        } else {
           // Utils.makeEventToast(getApplicationContext(), getString(R.string.lable_not_found),
            //        false);
        }
        finish();
    }
}