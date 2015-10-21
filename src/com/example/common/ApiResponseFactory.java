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
package com.example.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import com.example.codec.binary.Base64;
import com.example.codec.binary.Crypter;
import com.example.download.DownloadItem;
import com.example.download.ProductDetail;
import com.example.download.UpdateInfo;
import com.example.download.UpgradeInfo;
import com.example.session.Constants;
import com.example.session.Session;
import com.example.util.DBUtils;
import com.example.util.SecurityUtil;
import com.example.util.StringUtils;
import com.example.util.Utils;
import com.example.util.XmlElement;
import com.mappn.gfan.R;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;



/**
 * API ��Ӧ������������࣬���е�API��Ӧ���������Ҫ�ڴ���ɡ�
 * 
 * @author andrew
 * @date 2011-4-22
 * 
 */
public class ApiResponseFactory {

//    private static final String TAG = "ApiResponseFactory";

    /**
     * �����г�API��Ӧ���
     * 
     * @param action
     *            ����API����
     * @param response
     *            HTTP Response
     * @return ������Ľ���������������᷵��Null��
     */
    public static Object getResponse(Context context, int action, HttpResponse response) {

        InputStream in = null;
        String inputBody = null;
        if (MarketAPI.ACTION_GET_ALIPAY_ORDER_INFO == action
                || MarketAPI.ACTION_QUERY_ALIPAY_RESULT == action) {
            inputBody = Utils.getStringResponse(response);
            if (TextUtils.isEmpty(inputBody)) {
                return null;
            }
        } else {
            in = Utils.getInputStreamResponse(response);
            if (in == null) {
                return null;
            }
        }

        String requestMethod = "";
        Object result = null;
        try {
            switch (action) {
            
            case MarketAPI.ACTION_REGISTER:

                // ע��
                requestMethod = "ACTION_REGISTER";
                result = parseLoginOrRegisterResult(XmlElement.parseXml(in));
                break;

            case MarketAPI.ACTION_LOGIN:

                // ��¼
                requestMethod = "ACTION_LOGIN";
                result = parseLoginOrRegisterResult(XmlElement.parseXml(in));
                break;
                
            case MarketAPI.ACTION_GET_TOP_RECOMMEND:
                
                // ��ȡ��ҳ�����Ƽ�
                requestMethod = "ACTION_GET_TOP_RECOMMEND";
                result = parseTopRecommend(XmlElement.parseXml(in));
                break;
                
            case MarketAPI.ACTION_GET_HOME_RECOMMEND:
                
                // ��ȡ��ҳ�Ƽ�
                requestMethod = "ACTION_GET_HOME_RECOMMEND";
                result = parseProductList(context, XmlElement.parseXml(in), true);
                break;
            
            case MarketAPI.ACTION_GET_SEARCH_KEYWORDS:
                
                // ��ȡ�����ȴ�
                requestMethod = "ACTION_GET_SEARCH_KEYWORDS";
                result = parseSearchKeywords(XmlElement.parseXml(in));
                break;

            case MarketAPI.ACTION_GET_COMMENTS:
                
                // ��ȡ�����б�
                requestMethod = "ACTION_GET_COMMENTS";
                result = parseComments(XmlElement.parseXml(in));
                break;

            case MarketAPI.ACTION_GET_MYRATING:
                
                // ��ȡ�ҵ�����
                requestMethod = "ACTION_GET_MYRATING";
                result = parseMyRating(XmlElement.parseXml(in));
                break;

            case MarketAPI.ACTION_ADD_RATING:
                
                // �������
                requestMethod = "ACTION_ADD_RATIONG";
                result = true;
                break;

            case MarketAPI.ACTION_ADD_COMMENT:
                
                // �������
                requestMethod = "ACTION_ADD_COMMENT";
                result = true;
                break;

            case MarketAPI.ACTION_GET_PRODUCT_DETAIL:
                
                // ��ȡӦ����ϸ
                requestMethod = "ACTION_GET_PRODUCT_DETAIL";
                result = parseProductDetail(XmlElement.parseXml(in));
                break;

            case MarketAPI.ACTION_GET_RANK_BY_CATEGORY:

                // ��ȡ�����б�
                requestMethod = "ACTION_GET_RANK_BY_CATEGORY";
                result = parseProductList(context, XmlElement.parseXml(in), false);
                break;

            case MarketAPI.ACTION_GET_GROW_FAST:

                // ��ȡ�����������
                requestMethod = "ACTION_GET_GROW_FAST";
                result = parseProductList(context, XmlElement.parseXml(in), false);
                break;

            case MarketAPI.ACTION_GET_DETAIL:

                // ��ȡ��Ʒ��ϸ��Ϣ
                requestMethod = "ACTION_GET_DETAIL";
                result = parseProductDetail(XmlElement.parseXml(in));
                break;

            case MarketAPI.ACTION_SYNC_BUYLOG:

                // ��ȡ���Ѽ�¼
                requestMethod = "ACTION_SYNC_BUYLOG";
               // result = parseSyncBuyLog(XmlElement.parseXml(in));
                break;

            case MarketAPI.ACTION_SYNC_APPS:
                
                // �ύ��װӦ����Ϣ
                requestMethod = "ACTION_SYNC_APPS";
                result = parseSyncApps(XmlElement.parseXml(in));
                break;

            case MarketAPI.ACTION_CHECK_NEW_VERSION:
                
                // ���Ӧ�ð汾
                requestMethod = "ACTION_CHECK_NEW_VERSION";
                result = parseCheckNewVersion(XmlElement.parseXml(in));
                break;

            case MarketAPI.ACTION_PURCHASE_PRODUCT:
                
                // ����Ӧ��
                requestMethod = "ACTION_PURCHASE_PRODUCT";
                result = true;
                break;
                
            case MarketAPI.ACTION_GET_DOWNLOAD_URL:
                
                // ����������ӵ�ַ
                requestMethod = "ACTION_GET_DOWNLOAD_URL";
                result = parseDownloadInfo(XmlElement.parseXml(in));
                break;
                
            case MarketAPI.ACTION_GET_ALL_CATEGORY:
                
                // ���ȫ������
                requestMethod = "ACTION_GET_ALL_CATEGORY";
                result = parseAllCategory(XmlElement.parseXml(in));
                break;
                
            case MarketAPI.ACTION_GET_PRODUCTS:
                
                // ��÷����Ʒ�б�
                requestMethod = "ACTION_GET_PRODUCTS";
                result = parseProductList(context, XmlElement.parseXml(in), false);
                break;
                
            case MarketAPI.ACTION_GET_RECOMMEND_PRODUCTS:
                
                // ���ר���Ƽ���Ʒ�б�
                requestMethod = "ACTION_GET_RECOMMEND_PRODUCTS";
                result = parseProductList(context, XmlElement.parseXml(in), false);
                break;
                
            case MarketAPI.ACTION_BBS_SEARCH:
                
                // ���BBS���������б�
                requestMethod = "ACTION_BBS_SEARCH";
                String searchResult = new BufferedReader(new InputStreamReader(in)).readLine();
                result = parseBbsSearchResult(searchResult);
                break;
                
            case MarketAPI.ACTION_SEARCH:
                
                // ���BBS���������б�
                requestMethod = "ACTION_SEARCH";
                result = parseProductList(context, XmlElement.parseXml(in), false);
                break;
                
            case MarketAPI.ACTION_GET_REQUIRED:
                
                // װ���ر�
                requestMethod = "ACTION_GET_REQUIRED";
                result = parseGetRequired(context, XmlElement.parseXml(in));
                break;
                
            case MarketAPI.ACTION_GET_TOPIC:
                
                // ���ר���б�
                requestMethod = "ACTION_GET_TOPIC";
                result = parseTopicList(context, XmlElement.parseXml(in));
                break;
                
            case MarketAPI.ACTION_CHECK_UPGRADE:
                
                // ���Ӧ�ø���
                requestMethod = "ACTION_CHECK_UPGRADE";
                XmlElement r = null;
                result = parseUpgrade(context, r = XmlElement.parseXml(in));
                Log.i("test", "r:"+r);
                break;
                
            case MarketAPI.ACTION_CHECK_NEW_SPLASH:
                
                // ���Ӧ�ø���
                requestMethod = "ACTION_CHECK_NEW_SPLASH";
                //result = parseNewSplash(XmlElement.parseXml(in));
                break;
                
            case MarketAPI.ACTION_GET_PAY_LOG:

                // ��ȡ������ʷ��Ϣ�б�
                requestMethod = "ACTION_GET_PAY_LOG";
                //result = parseGetPayLog(context, XmlElement.parseXml(in));
                break;
                
            case MarketAPI.ACTION_BIND_ACCOUNT:

                // ���û��ֻ�
                requestMethod = "ACTION_BIND_ACCOUNT";
                result = true;
                break;
                
            case MarketAPI.ACTION_SYNC_CARDINFO:

                // ͬ����ֵ����Ϣ
                requestMethod = "ACTION_SYNC_CARDINFO";
                //result = parseSyncCardinfo(context, XmlElement.parseXml(in));
                break;
           
            case MarketAPI.ACTION_CHARGE:
                
                // ��ѯ��ֵ���
                requestMethod = "ACTION_CHARGE";
                result = parseChargeResult(XmlElement.parseXml(in));
                break;
                
            case MarketAPI.ACTION_QUERY_CHARGE_BY_ORDERID:
                
                // ��ѯ��ֵ���
                requestMethod = "ACTION_QUERY_CHARGE_BY_ORDERID";
                result = parseQueryChargeResultByOderID(XmlElement.parseXml(in));
                break;
                
            case MarketAPI.ACTION_GET_BALANCE:
                
                // ��ѯ���
                requestMethod = "ACTION_GET_BALANCE";
                result = parseGetBalance(XmlElement.parseXml(in));
                break;
                
            case MarketAPI.ACTION_GET_ALIPAY_ORDER_INFO:
                
                // ����֧�����������
                requestMethod = "ACTION_GET_ALIPAY_ORDER_INFO";
                result = parseGetAlipayOrderInfo(inputBody);
                break;
                
            case MarketAPI.ACTION_QUERY_ALIPAY_RESULT:
                
                // ����֧�������
                requestMethod = "ACTION_QUERY_ALIPAY_RESULT";
                result = parseGetAlipayOrderInfo(inputBody);
                break;
            
            case MarketAPI.ACTION_UNBIND:
            	
            	//�����
            	result = true;
            	break;
                
            default:
                break;
            }

        } catch (XmlPullParserException e) {
            Utils.D(requestMethod + " has XmlPullParserException", e);
        } catch (IOException e) {
            Utils.D(requestMethod + " has IOException", e);
        } catch (JSONException e) {
            Utils.D(requestMethod + " has JSONException", e);
        }
        if (result != null) {
            Utils.D(requestMethod + "'s Response is : " + result.toString());
        } else {
            Utils.D(requestMethod + "'s Response is null");
        }
        return result;
    }

	/*
     * ��ȡ���з����б�
     */
    private static ArrayList<HashMap<String, Object>> parseAllCategory(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        List<XmlElement> categorys = xmlDocument.getChildren(Constants.KEY_CATEGORY);
        ArrayList<HashMap<String, Object>> result = null;
        if (categorys != null) {
            result = new ArrayList<HashMap<String, Object>>();

            for (int i = 1; i < categorys.size(); i++) {
                XmlElement category = categorys.get(i);
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put(Constants.KEY_CATEGORY_NAME,
                        category.getAttribute(Constants.KEY_CATEGORY_NAME));
                item.put(Constants.KEY_APP_COUNT,
                        category.getAttribute(Constants.KEY_APP_COUNT));
                item.put(Constants.KEY_CATEGORY_ICON_URL,
                        category.getAttribute(Constants.KEY_CATEGORY_ICON_URL));
                
                String subCategoryText = category.getChild(Constants.KEY_SUB_CATEGORY, 0).getAttribute(
                        Constants.KEY_CATEGORY_NAME) + ", ";
                XmlElement category2 = category.getChild(Constants.KEY_SUB_CATEGORY, 1);
                if(category2 != null) {
                    subCategoryText +=  (category2.getAttribute(
                            Constants.KEY_CATEGORY_NAME) + ", ");
                }
                XmlElement category3 = category.getChild(Constants.KEY_SUB_CATEGORY, 2);
                if(category3 != null) {
                    subCategoryText += (category3.getAttribute(
                            Constants.KEY_CATEGORY_NAME) + ", ");
                }
                if (subCategoryText.length() > 0) {
                    subCategoryText = subCategoryText.substring(0, subCategoryText.length() - 2);
                }
                item.put(Constants.KEY_TOP_APP, subCategoryText);

                List<XmlElement> subCategorys = category.getChildren(Constants.KEY_SUB_CATEGORY);
                ArrayList<HashMap<String, Object>> subCategoryList = new ArrayList<HashMap<String, Object>>();
                for (XmlElement element : subCategorys) {
                    HashMap<String, Object> subCategory = new HashMap<String, Object>();
                    subCategory.put(Constants.KEY_CATEGORY_ID,
                            element.getAttribute(Constants.KEY_CATEGORY_ID));
                    subCategory.put(Constants.KEY_CATEGORY_NAME,
                            element.getAttribute(Constants.KEY_CATEGORY_NAME));
                    subCategory.put(Constants.KEY_APP_COUNT,
                            element.getAttribute(Constants.KEY_APP_COUNT));
                    subCategory.put(Constants.KEY_CATEGORY_ICON_URL,
                            element.getAttribute(Constants.KEY_CATEGORY_ICON_URL));
                    String app1 = element.getAttribute(Constants.KEY_APP_1);
                    String app2 = element.getAttribute(Constants.KEY_APP_2);
                    String app3 = element.getAttribute(Constants.KEY_APP_3);
                    String topApp = (TextUtils.isEmpty(app1) ? "" : app1 + ", ")
                            + (TextUtils.isEmpty(app2) ? "" : app2 + ", ")
                            + (TextUtils.isEmpty(app3) ? "" : app3 +  ", ");
                    if (topApp.length() > 0) {
                        topApp = topApp.substring(0, topApp.length() - 2);
                    }
                    subCategory.put(Constants.KEY_TOP_APP, topApp);
                    subCategoryList.add(subCategory);
                }
                item.put(Constants.KEY_SUB_CATEGORY, subCategoryList);
                result.add(item);
            }
            
            // չ����һ��һ���б�
            XmlElement firstCategory = categorys.get(0);
            List<XmlElement> firstSubCategorys = firstCategory
                    .getChildren(Constants.KEY_SUB_CATEGORY);
            for (XmlElement element : firstSubCategorys) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put(Constants.KEY_CATEGORY_ID,
                            element.getAttribute(Constants.KEY_CATEGORY_ID));
                item.put(Constants.KEY_CATEGORY_NAME,
                        element.getAttribute(Constants.KEY_CATEGORY_NAME));
                item.put(Constants.KEY_APP_COUNT,
                        element.getAttribute(Constants.KEY_APP_COUNT));
                item.put(Constants.KEY_CATEGORY_ICON_URL,
                        element.getAttribute(Constants.KEY_CATEGORY_ICON_URL));
                String app1 = element.getAttribute(Constants.KEY_APP_1);
                String app2 = element.getAttribute(Constants.KEY_APP_2);
                String app3 = element.getAttribute(Constants.KEY_APP_3);
                String topApp = (TextUtils.isEmpty(app1) ? "" : app1 + ", ")
                        + (TextUtils.isEmpty(app2) ? "" : app2 + ", ")
                        + (TextUtils.isEmpty(app3) ? "" : app3 +  ", ");
                if (topApp.length() > 0) {
                    topApp = topApp.substring(0, topApp.length() - 2);
                }
                item.put(Constants.KEY_TOP_APP, topApp);
                result.add(item);
            }
        }
        return result;
    }
    
    /*
     * ��ȡ��Ʒ��ϸ��Ϣ 
     */
    private static Object parseProductDetail(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        XmlElement product = xmlDocument.getChild(Constants.KEY_PRODUCT, 0);
        ProductDetail result = null;

        if (product != null) {
            result = new ProductDetail();
            result.setPid(product.getAttribute(Constants.KEY_PRODUCT_ID));
            result.setProductType(product.getAttribute(Constants.KEY_PRODUCT_TYPE));
            result.setName(product.getAttribute(Constants.KEY_PRODUCT_NAME));
            result.setPrice(Utils.getInt(product.getAttribute(Constants.KEY_PRODUCT_PRICE)));
            result.setPayCategory(Utils.getInt(product.getAttribute(Constants.KEY_PRODUCT_PAY_TYPE)));
            result.setRating(Utils.getInt(product.getAttribute(Constants.KEY_PRODUCT_RATING)));
            result.setIconUrl(product.getAttribute(Constants.KEY_PRODUCT_ICON_URL));
            result.setIconUrlLdpi(product.getAttribute(Constants.KEY_PRODUCT_ICON_URL_LDPI));
            result.setShotDes(product.getAttribute(Constants.KEY_PRODUCT_SHORT_DESCRIPTION));
            result.setAppSize(Utils.getInt(product.getAttribute(Constants.KEY_PRODUCT_SIZE)));
            result.setSourceType(product.getAttribute(Constants.KEY_PRODUCT_SOURCE_TYPE));
            result.setPackageName(product.getAttribute(Constants.KEY_PRODUCT_PACKAGE_NAME));
            result.setVersionName(product.getAttribute(Constants.KEY_PRODUCT_VERSION_NAME));
            result.setVersionCode(Utils.getInt(product
                    .getAttribute(Constants.KEY_PRODUCT_VERSION_CODE)));
            result.setCommentsCount(Utils.getInt(product
                    .getAttribute(Constants.KEY_PRODUCT_COMMENTS_COUNT)));
            result.setRatingCount(Utils.getInt(product
                    .getAttribute(Constants.KEY_PRODUCT_RATING_COUNT)));
            result.setDownloadCount(Utils.getInt(product
                    .getAttribute(Constants.KEY_PRODUCT_DOWNLOAD_COUNT)));
            result.setLongDescription(product.getAttribute(Constants.KEY_PRODUCT_LONG_DESCRIPTION));
            result.setAuthorName(product.getAttribute(Constants.KEY_PRODUCT_AUTHOR));
            result.setPublishTime(Utils.getInt(product
                    .getAttribute(Constants.KEY_PRODUCT_PUBLISH_TIME)));
            final String[] screenShot = new String[5];
            screenShot[0] = product.getAttribute(Constants.KEY_PRODUCT_SCREENSHOT_1);
            screenShot[1] = product.getAttribute(Constants.KEY_PRODUCT_SCREENSHOT_2);
            screenShot[2] = product.getAttribute(Constants.KEY_PRODUCT_SCREENSHOT_3);
            screenShot[3] = product.getAttribute(Constants.KEY_PRODUCT_SCREENSHOT_4);
            screenShot[4] = product.getAttribute(Constants.KEY_PRODUCT_SCREENSHOT_5);
            result.setScreenshot(screenShot);
            final String[] screenShotLdpi = new String[5];
            screenShotLdpi[0] = product.getAttribute(Constants.KEY_PRODUCT_SCREENSHOT_LDPI_1);
            screenShotLdpi[1] = product.getAttribute(Constants.KEY_PRODUCT_SCREENSHOT_LDPI_2);
            screenShotLdpi[2] = product.getAttribute(Constants.KEY_PRODUCT_SCREENSHOT_LDPI_3);
            screenShotLdpi[3] = product.getAttribute(Constants.KEY_PRODUCT_SCREENSHOT_LDPI_4);
            screenShotLdpi[4] = product.getAttribute(Constants.KEY_PRODUCT_SCREENSHOT_LDPI_5);
            result.setScreenshotLdpi(screenShotLdpi);
            result.setUpReason(product.getAttribute(Constants.KEY_PRODUCT_UP_REASON));
            result.setUpTime(Utils.getLong(product.getAttribute(Constants.KEY_PRODUCT_UP_TIME)));
            result.setPermission(product.getAttribute(Constants.KEY_PRODUCT_PERMISSIONS));
        }
        return result;
    }
    
    /**
     * ����ͬ��Ӧ��
     */
    private static Object parseSyncApps(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }
        UpdateInfo updateInfo = new UpdateInfo();

        updateInfo.setUpdageLevel(Integer.valueOf(xmlDocument.getChild(
                Constants.EXTRA_UPDATE_LEVEL, 0).getText()));
        updateInfo.setVersionCode(Integer.valueOf(xmlDocument.getChild(
                Constants.EXTRA_VERSION_CODE, 0).getText()));
        updateInfo.setVersionName(xmlDocument.getChild(
                Constants.EXTRA_VERSION_NAME, 0).getText());
        updateInfo.setDescription(xmlDocument.getChild(
                Constants.EXTRA_DESCRIPTION, 0).getText());
        updateInfo.setApkUrl(xmlDocument.getChild(Constants.EXTRA_URL, 0)
                .getText());

        return updateInfo;
    }

    /*
     * �����ҵ����ǽ��
     */
    private static Object parseMyRating(XmlElement xmlDocument) {
        if (xmlDocument == null) {
            return null;
        }

        XmlElement element = xmlDocument.getChild(Constants.KEY_PRODUCT_RATING, 0);
        if (element != null) {
            return element.getAttribute(Constants.KEY_VALUE);
        }
        return null;
    }

    /*
     * ���������б�
     */
    private static Object parseComments(XmlElement xmlDocument) {
        if (xmlDocument == null) {
            return null;
        }
        HashMap<String, Object> result = null;
        XmlElement comments = xmlDocument.getChild(Constants.KEY_COMMENTS, 0);
        if (comments != null) {
            result = new HashMap<String, Object>();
            
            int totalSize = Utils.getInt(comments.getAttribute(Constants.KEY_TOTAL_SIZE));
            result.put(Constants.KEY_TOTAL_SIZE, totalSize);
            
            if (totalSize > 0) {
                ArrayList<HashMap<String, Object>> commentList = new ArrayList<HashMap<String, Object>>();
                List<XmlElement> children = comments.getChildren(Constants.KEY_COMMENT);
                for (XmlElement element : children) {
                    HashMap<String, Object> commentEntity = new HashMap<String, Object>();

                    commentEntity.put(Constants.KEY_COMMENT_ID,
                            element.getAttribute(Constants.KEY_COMMENT_ID));
                    commentEntity.put(Constants.KEY_COMMENT_AUTHOR,
                            element.getAttribute(Constants.KEY_COMMENT_AUTHOR));
                    commentEntity.put(Constants.KEY_COMMENT_BODY,
                            element.getAttribute(Constants.KEY_COMMENT_BODY));
                    long millis = Utils.getLong(element.getAttribute(Constants.KEY_COMMENT_DATE));
                    commentEntity.put(Constants.KEY_COMMENT_DATE, Utils.formatTime(millis));
                    commentList.add(commentEntity);
                }
                result.put(Constants.KEY_COMMENT_LIST, commentList);
            }
        }
        return result;
    }

    /*
     * ����ע����ߵ�¼���
     */
    private static HashMap<String, String> parseLoginOrRegisterResult(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        HashMap<String, String> result = new HashMap<String, String>();
        result.put(Constants.KEY_USER_UID, xmlDocument.getChild(Constants.KEY_USER_UID, 0)
                .getText());
        result.put(Constants.KEY_USER_NAME, xmlDocument.getChild(Constants.KEY_USER_NAME, 0)
                .getText());
        result.put(Constants.KEY_USER_EMAIL, xmlDocument.getChild(Constants.KEY_USER_EMAIL, 0)
                .getText());
        return result;
    }

    /*
     * ����Ӧ���б�
     */
    private static HashMap<String, Object> parseProductList(Context context,
            XmlElement xmlDocument, boolean isIgnoreStar) {

        if (xmlDocument == null) {
            return null;
        }
        final String IS_STAR_PRODUCT = "1";
        XmlElement products = xmlDocument.getChild(Constants.KEY_PRODUCTS, 0);
        HashMap<String, Object> result = null;
        ArrayList<HashMap<String, Object>> productArray = null;
        if (products != null) {

            // ��ȡ�Ѿ���װ��Ӧ���б�
            Session session = Session.get(context);
            ArrayList<String> installedApps = session.getInstalledApps();

            List<XmlElement> productList = products.getChildren(Constants.KEY_PRODUCT);
            if (productList == null) {
                return null;
            }
            result = new HashMap<String, Object>();
            result.put(Constants.KEY_TOTAL_SIZE,
                    Utils.getInt(products.getAttribute(Constants.KEY_TOTAL_SIZE)));
            result.put(Constants.KEY_END_POSITION,
                    Utils.getInt(products.getAttribute(Constants.KEY_END_POSITION)));
            productArray = new ArrayList<HashMap<String, Object>>();
            for (XmlElement element : productList) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put(Constants.KEY_PRODUCT_ID, element.getAttribute(Constants.KEY_PRODUCT_ID));
                String packageName = element.getAttribute(Constants.KEY_PRODUCT_PACKAGE_NAME);
                item.put(Constants.KEY_PRODUCT_PACKAGE_NAME, packageName);
                int price = Utils.getInt(element.getAttribute(Constants.KEY_PRODUCT_PRICE));
                String priceText = price == 0 ? context.getString(R.string.free) : context
                        .getString(R.string.duihuanquan_unit, price);
                item.put(Constants.KEY_PRODUCT_PRICE, priceText);
                boolean isStar = IS_STAR_PRODUCT.equals(element
                        .getAttribute(Constants.KEY_PRODUCT_IS_STAR)) ? true : false;
                if (isIgnoreStar) {
                    // �����Ǳ�
                    item.put(Constants.KEY_PRODUCT_IS_STAR, false);
                } else {
                    item.put(Constants.KEY_PRODUCT_IS_STAR, isStar);
                }
                
                if (installedApps.contains(packageName)) {
                    
                    if (isIgnoreStar && !isStar) {
                        // ��ҳ�����Ѿ���װ��Ӧ��
                        continue;
                    }
                    
                    // Ӧ���Ѿ���װ����ʾ�Ѿ���װ����Ϣ��ʾ
                    item.put(Constants.KEY_PRODUCT_DOWNLOAD, Constants.STATUS_INSTALLED);
                } else {
                    // Ӧ��δ��װ����ʾ������Ϣ��ʾ
                    item.put(Constants.KEY_PRODUCT_DOWNLOAD, Constants.STATUS_NORMAL);
                }

                item.put(Constants.KEY_PRODUCT_NAME,
                        element.getAttribute(Constants.KEY_PRODUCT_NAME));
                item.put(Constants.KEY_PRODUCT_AUTHOR,
                        element.getAttribute(Constants.KEY_PRODUCT_AUTHOR));
                item.put(
                        Constants.KEY_PRODUCT_SUB_CATEGORY,
                        element.getAttribute(Constants.KEY_PRODUCT_TYPE) + " > "
                                + element.getAttribute(Constants.KEY_PRODUCT_SUB_CATEGORY));
                
                item.put(Constants.KEY_PRODUCT_PAY_TYPE,
                        Utils.getInt(element.getAttribute(Constants.KEY_PRODUCT_PAY_TYPE)));
                item.put(Constants.KEY_PRODUCT_RATING,
                        Utils.getInt(element.getAttribute(Constants.KEY_PRODUCT_RATING)));
                item.put(Constants.KEY_PRODUCT_SIZE,
                        StringUtils.formatSize(element.getAttribute(Constants.KEY_PRODUCT_SIZE)));
                item.put(Constants.KEY_PRODUCT_ICON_URL,
                        element.getAttribute(Constants.KEY_PRODUCT_ICON_URL));
                item.put(Constants.KEY_PRODUCT_ICON_URL_LDPI,
                        element.getAttribute(Constants.KEY_PRODUCT_ICON_URL_LDPI));
                item.put(Constants.KEY_PRODUCT_SHORT_DESCRIPTION,
                        element.getAttribute(Constants.KEY_PRODUCT_SHORT_DESCRIPTION));
                
                String source = element.getAttribute(Constants.KEY_PRODUCT_SOURCE_TYPE);
                if (Constants.SOURCE_TYPE_GOOGLE.equals(source)) {
                    item.put(Constants.KEY_PRODUCT_SOURCE_TYPE,
                            context.getString(R.string.leble_google));
                }
                
                productArray.add(item);
            }
            result.put(Constants.KEY_PRODUCT_LIST, productArray);
        }
        return result;
    }
    
    /*
     * ���ɸ��²�Ʒ�б�
     */
    private static String parseUpgrade(Context context, XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return "";
        }

        XmlElement products = xmlDocument.getChild(Constants.KEY_PRODUCTS, 0);
        String count = "";
        if (products != null) {
            List<XmlElement> productList = products.getChildren(Constants.KEY_PRODUCT);
            if (productList == null) {
                // û�пɸ��µ�Ӧ��
                return count;
            }
            ArrayList<UpgradeInfo> list = new ArrayList<UpgradeInfo>();
            for (XmlElement element : productList) {
                UpgradeInfo info = new UpgradeInfo();
                info.pid = element.getAttribute(Constants.KEY_PRODUCT_ID);
                info.pkgName = element.getAttribute(Constants.KEY_PRODUCT_PACKAGE_NAME);
                info.versionName = element.getAttribute(Constants.KEY_PRODUCT_VERSION_NAME);
                info.versionCode = Utils.getInt(element
                        .getAttribute(Constants.KEY_PRODUCT_VERSION_CODE));
                info.update = 0;
                list.add(info);
            }
            count = String.valueOf(DBUtils.addUpdateProduct(context, list));
        }
        return count;
    }
    
    /*
     * ����ר���б�
     */
    private static ArrayList<HashMap<String, Object>> parseTopicList(Context context,
            XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        XmlElement topics = xmlDocument.getChild(Constants.KEY_TOPICS, 0);
        ArrayList<HashMap<String, Object>> topicArray = null;
        if (topics != null) {
            final String MUST_HAVE_ID = "5";
            List<XmlElement> topicList = topics.getChildren(Constants.KEY_TOPIC);
            topicArray = new ArrayList<HashMap<String, Object>>();
            for (XmlElement element : topicList) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                    
                String id = element.getAttribute(Constants.KEY_ID);
                if (MUST_HAVE_ID.equals(id)) {
                    // װ���ر�����Ҫ������б���չʾ
                    continue;
                }
                item.put(Constants.KEY_ID, id);
                item.put(Constants.KEY_CATEGORY_NAME,
                        element.getAttribute(Constants.KEY_TOPIC_NAME));
                item.put(Constants.KEY_CATEGORY_ICON_URL,
                        element.getAttribute(Constants.KEY_TOPIC_ICON_LDPI));

                String app1 = element.getAttribute(Constants.KEY_APP_1);
                String app2 = element.getAttribute(Constants.KEY_APP_2);
                String app3 = element.getAttribute(Constants.KEY_APP_3);
                String description = app1 + ", ";
                if (!TextUtils.isEmpty(app2)) {
                    description += (app2 + ", ");
                }
                if (!TextUtils.isEmpty(app3)) {
                    description += (app3 + ", ");
                }
                if (description.length() > 1) {
                    description = description.substring(0, description.lastIndexOf(",") - 2);
                }
                item.put(Constants.KEY_TOP_APP, description);
                item.put(Constants.KEY_APP_COUNT, element.getAttribute(Constants.KEY_APP_COUNT));
                topicArray.add(item);
            }
        }
        return topicArray;
    }

    /*
     * ������ҳ�����Ƽ����б�
     */
    private static ArrayList<HashMap<String, Object>> parseTopRecommend(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        List<XmlElement> recommends = xmlDocument.getAllChildren();
        ArrayList<HashMap<String, Object>> recommendList = null;
        if (recommends != null) {
            recommendList = new ArrayList<HashMap<String, Object>>();
            for (XmlElement element : recommends) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                if (Constants.KEY_CATEGORY.equals(element.getName())) {
                    item.put(Constants.KEY_RECOMMEND_TYPE, Constants.KEY_CATEGORY);
                } else if (Constants.KEY_TOPIC.equals(element.getName())) {
                    item.put(Constants.KEY_RECOMMEND_TYPE, Constants.KEY_TOPIC);
                } else if (Constants.KEY_PRODUCT.equals(element.getName())) {
                    item.put(Constants.KEY_RECOMMEND_TYPE, Constants.KEY_PRODUCT);
                } else {
                    item.put(Constants.KEY_RECOMMEND_TYPE, -1);
                }
                item.put(Constants.KEY_ID,
                        element.getAttribute(Constants.KEY_ID));
                item.put(Constants.KEY_RECOMMEND_ICON,
                        element.getAttribute(Constants.KEY_RECOMMEND_ICON));
                item.put(Constants.KEY_RECOMMEND_TITLE,
                        element.getAttribute(Constants.KEY_RECOMMEND_TITLE));
                recommendList.add(item);
            }
        }
        return recommendList;
    }
    
    /**
     * ��ȡͬ�������б�
     */
 /*   private static Object parseSyncBuyLog(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        XmlElement products = xmlDocument.getChild(Constants.KEY_PRODUCTS, 0);
        if (products == null) {
            return null;
        }
        List<XmlElement> productList = products.getChildren(Constants.KEY_PRODUCT);
        if (productList == null) {
            return null;
        }
        List<BuyLog> result = new ArrayList<BuyLog>();
        for (int i = 0, length = productList.size(); i < length; i++) {
            XmlElement product = products.getChild(Constants.KEY_PRODUCT, i);
            BuyLog buyLog = new BuyLog();
            buyLog.pId = product.getAttribute(Constants.KEY_PRODUCT_ID);
            buyLog.packageName = product.getAttribute(Constants.PRODUCT_PACKAGENAME);
            result.add(buyLog);
        }
        return result;
    }*/

    /**
     * ����Ƿ����°汾
     */
    private static Object parseCheckNewVersion(XmlElement xmlDocument) {
        if (xmlDocument == null) {
            return null;
        }

        int level = Utils.getInt(xmlDocument.getChild(Constants.EXTRA_UPDATE_LEVEL, 0).getText());

        if (level == 0) {
            File root = new File(Environment.getExternalStorageDirectory(),
                    Constants.IMAGE_CACHE_DIR);
            root.mkdirs();
            File output = new File(root, "aMarket.apk");
            output.delete();
            return null;
        }
        
        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.setUpdageLevel(level);
        updateInfo.setVersionCode(Utils.getInt(xmlDocument
                .getChild(Constants.EXTRA_VERSION_CODE, 0).getText()));
        updateInfo.setVersionName(xmlDocument.getChild(Constants.EXTRA_VERSION_NAME, 0).getText());
        updateInfo.setDescription(xmlDocument.getChild(Constants.EXTRA_DESCRIPTION, 0).getText());
        updateInfo.setApkUrl(xmlDocument.getChild(Constants.EXTRA_URL, 0).getText());
        return updateInfo;
    }
    
    /*
     * ��ȡ��Ʒ������Ϣ 
     */
    private static DownloadItem parseDownloadInfo(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        DownloadItem item = null;
        XmlElement downloadInfo = xmlDocument.getChild(Constants.KEY_DOWNLOAD_INFO, 0);
        if (downloadInfo != null) {
            item = new DownloadItem();
            item.pId = downloadInfo.getAttribute(Constants.KEY_PRODUCT_ID);
            item.packageName = downloadInfo.getAttribute(Constants.KEY_PRODUCT_PACKAGE_NAME);
            item.url = downloadInfo.getAttribute(Constants.KEY_PRODUCT_DOWNLOAD_URI);
            item.fileMD5 = downloadInfo.getAttribute(Constants.KEY_PRODUCT_MD5);
        }
        return item;
    }

    /*
     * ���������ȴ��б�
     */
    private static ArrayList<String> parseSearchKeywords(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        XmlElement keyList = xmlDocument.getChild(Constants.KEY_KEYLIST, 0);
        ArrayList<String> keywords = null;
        if (keyList != null) {
            keywords = new ArrayList<String>();
            List<XmlElement> keys = keyList.getAllChildren();
            for (XmlElement key : keys) {
                keywords.add(key.getAttribute(Constants.KEY_TEXT));
            }
        }
        return keywords;
    }
    
    /*
     * �������������BBS����������
     */
    private static HashMap<String, Object> parseBbsSearchResult(String body) {

        if (body == null) {
            return null;
        }
        HashMap<String, Object> result = null;
        try {
            JSONObject jsonBody = new JSONObject(body);
            result = new HashMap<String, Object>();
            result.put(Constants.KEY_TOTAL_SIZE, jsonBody.getInt("totalSize"));
            result.put(Constants.KEY_END_POSITION, jsonBody.getInt("endPosition"));
            JSONArray array = jsonBody.getJSONArray(Constants.KEY_JK_LIST);

            final int length = array.length();
            if (length > 0) {
                ArrayList<HashMap<String, Object>> topicList = new ArrayList<HashMap<String, Object>>();
                for (int i = 0; i < length; i++) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    JSONObject item = array.getJSONObject(i);
                    map.put(Constants.SEARCH_RESULT_TITLE,
                            String.valueOf(item.get(Constants.KEY_SUBJECT)));
                    map.put(Constants.KEY_PLACEHOLDER, true);
                    topicList.add(map);
                    JSONArray subArray = item.getJSONArray(Constants.KEY_FILE_LIST);
                    for (int j = 0, len = subArray.length(); j < len; j++) {
                        HashMap<String, Object> subMap = new HashMap<String, Object>();
                        JSONObject subItem = subArray.getJSONObject(j);
                        subMap.put(Constants.KEY_PLACEHOLDER, false);
                        subMap.put(Constants.SEARCH_RESULT_TITLE,
                                String.valueOf(subItem.get(Constants.KEY_FILE_NAME)));
                        subMap.put(Constants.KEY_DOWN_URL,
                                String.valueOf(subItem.getString(Constants.KEY_DOWN_URL)));
                        topicList.add(subMap);
                    }
                }
                result.put(Constants.KEY_JK_LIST, topicList);
            }
        } catch (JSONException e) {
            Utils.D("have json exception when parse search result from bbs", e);
        }
        return result;
    }
    
    /*
     * ��ȡװ���ر��б�
     */
    private static Object parseGetRequired(Context context, XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        ArrayList<HashMap<String, Object>> result = null;
        List<XmlElement> productGroup = 
                xmlDocument.getChildren(Constants.KEY_REQUIRED_CATEGORY);
        if (productGroup != null && productGroup.size() > 0) {

            result = new ArrayList<HashMap<String, Object>>();
            
            // ��ȡ�Ѿ���װ��Ӧ���б�
            Session session = Session.get(context);
            ArrayList<String> installedApps = session.getInstalledApps();
            
            for (XmlElement group : productGroup) {

                // ������Ϣ
                HashMap<String, Object> groupItem = new HashMap<String, Object>();
                groupItem.put(Constants.INSTALL_PLACE_HOLDER, true);
                groupItem.put(Constants.INSTALL_APP_TITLE,
                        group.getAttribute(Constants.KEY_PRODUCT_NAME));
                List<XmlElement> productList = group.getChildren(Constants.KEY_PRODUCT);
                result.add(groupItem);

                if (productList == null || productList.size() == 0) {
                    continue;
                }

                // �����µĲ�Ʒ�б���Ϣ
                for (XmlElement product : productList) {
                    HashMap<String, Object> productItem = new HashMap<String, Object>();
                    productItem.put(Constants.INSTALL_PLACE_HOLDER, false);
                    productItem.put(Constants.KEY_PRODUCT_ID,
                            product.getAttribute(Constants.KEY_PRODUCT_ID));
                    productItem.put(Constants.INSTALL_APP_LOGO,
                            product.getAttribute(Constants.KEY_PRODUCT_ICON_URL));
                    productItem.put(Constants.INSTALL_APP_TITLE,
                            product.getAttribute(Constants.KEY_PRODUCT_NAME));
                    productItem.put(Constants.INSTALL_APP_DESCRIPTION,
                            product.getAttribute(Constants.KEY_PRODUCT_SHORT_DESCRIPTION));
                    String packageName = product.getAttribute(Constants.KEY_PRODUCT_PACKAGE_NAME);
                    if (installedApps.contains(packageName)) {
                        productItem.put(Constants.KEY_PRODUCT_IS_INSTALLED, true);
                    } else {
                        productItem.put(Constants.INSTALL_APP_IS_CHECKED, false);    
                    }
                    result.add(productItem);
                }
            }
        }
        return result;
    }
    
    /*
     * �����µ�Splashҳ
     */
/*    private static SplashInfo parseNewSplash(XmlElement xmlDocument) {
        
        if (xmlDocument == null) {
            return null;
        }
        
        SplashInfo info = new SplashInfo();
        XmlElement url = xmlDocument.getChild(SplashInfo.URL, 0);
        if(url != null) {
            info.url = url.getText();
        }
        XmlElement time = xmlDocument.getChild(SplashInfo.TIMESTAMP, 0);
        if(time != null) {
            info.timestamp = Utils.getLong(time.getText());
        }
        return info;
    }*/
    
    /*
     * ����֧����ʷ
     */
/*    private static PayAndChargeLogs parseGetPayLog(Context context,
            XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }
        PayAndChargeLogs result = null;
        XmlElement logs = xmlDocument.getChild(Constants.KEY_PAY_LOGS, 0);
        if (logs != null) {
            result = new PayAndChargeLogs();
            result.endPosition = Utils.getInt(logs.getAttribute(Constants.KEY_END_POSITION));
            result.totalSize = Utils.getInt(logs.getAttribute(Constants.KEY_TOTAL_SIZE));

            List<XmlElement> consumes = logs.getChildren(Constants.KEY_PAY_CONSUME);
            getPayAndChargeLog(consumes, result, Constants.KEY_PAY_CONSUME);
            List<XmlElement> charges = logs
                    .getChildren(Constants.KEY_PAY_CHARGE);
            getPayAndChargeLog(charges, result, Constants.KEY_PAY_CHARGE);

            List<XmlElement> buyApps = logs
                    .getChildren(Constants.KEY_PAY_BUY_APP);
            getPayAndChargeLog(buyApps, result, Constants.KEY_PAY_BUY_APP);
        }
        return result;
    }*/
    
    /*
     * ��ȡconsume,charge,buy_app��ǩ
     */
/*    private static void getPayAndChargeLog(List<XmlElement> tags, PayAndChargeLogs result,
            String flag) {
        if (tags != null && tags.size() > 0) {
            for (XmlElement tag : tags) {
                PayAndChargeLog log = new PayAndChargeLog();
                log.name = tag.getAttribute(Constants.KEY_PAY_FLAG);
                log.id = Utils.getInt(tag.getAttribute(Constants.KEY_PAY_ORDER_ID));
                log.desc = tag.getAttribute(Constants.KEY_PAY_DESCRIPTION);
                log.time = Utils.formatDate(Utils.getLong(tag.getAttribute(Constants.KEY_PAY_TIME)));
                log.payment = (int) Utils.getFloat(tag.getAttribute(Constants.KEY_PAY_MONEY));

                if (Constants.KEY_PAY_CONSUME.equals(flag)) {
                    log.type = PayAndChargeLog.TYPE_CONSUME;
                } else if (Constants.KEY_PAY_CHARGE.equals(flag)) {
                    log.type = PayAndChargeLog.TYPE_CHARGE;
                } else if (Constants.KEY_PAY_BUY_APP.equals(flag)) {
                    log.id = Utils.getInt(tag.getAttribute(Constants.KEY_PRODUCT_ID));
                    log.name = tag.getAttribute(Constants.KEY_PRODUCT_NAME);
                    log.iconUrl = tag.getAttribute(Constants.KEY_CATEGORY_ICON_URL);
                    log.type = PayAndChargeLog.TYPE_MARKET;
                    log.sourceType = Utils.getInt(tag
                            .getAttribute(Constants.KEY_PRODUCT_SOURCE_TYPE));
                }
                result.payAndChargeLogList.add(log);
            }
        }
    }*/
    
    /*
     * ͬ����ֵ����Ϣ
     */
 /*   private static CardsVerifications parseSyncCardinfo(Context context, XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }
        CardsVerifications results = new CardsVerifications();
        results.version = Utils.getInt(xmlDocument.getAttribute(Constants.REMOTE_VERSION));

        List<XmlElement> cards = xmlDocument.getChildren(Constants.PAY_CARD);
        for (XmlElement card : cards) {
            CardsVerification subCard = new CardsVerification();
            subCard.name = card.getAttribute(Constants.KEY_USER_NAME);
            subCard.pay_type = card.getAttribute(Constants.PAY_TYPE);
            subCard.accountNum = Utils.getInt(card.getAttribute(Constants.ACCOUNT_LEN));
            subCard.passwordNum = Utils.getInt(card.getAttribute(Constants.PASSWORD_LEN));
            subCard.credit = card.getAttribute(Constants.PAY_CREDIT);
            results.cards.add(subCard);
        }
        return results;
    }*/
    
    /*
     * ������ֵ���
     */
    private static String parseChargeResult(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        XmlElement result = xmlDocument.getChild(Constants.PAY_RESULT, 0);
        if (result != null) {
            return result.getAttribute(Constants.KEY_PAY_ORDER_ID);
        }
        return null;
    }
    
    /*
     * ������ֵ���(��������)
     */
    private static int parseQueryChargeResultByOderID(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return 0;
        }

        XmlElement result = xmlDocument.getChild(Constants.PAY_RESULT, 0);
        if (result != null) {
            return Utils.getInt(result.getAttribute(Constants.KEY_PAY_STATUS));
        }
        return 0;
    }
    
    /*
     * ������ѯ���
     */
    private static String parseGetBalance(XmlElement xmlDocument) {

        if (xmlDocument == null) {
            return null;
        }

        XmlElement result = xmlDocument.getChild(Constants.RESULT, 0);
        if (result != null) {
            return result.getText();
        }
        return null;
    }
    
    /*
     * ����֧�����������
     */
    private static JSONObject parseGetAlipayOrderInfo(String in) throws JSONException {
        byte[] data = Base64.decodeBase64(in);
        return new JSONObject(new String(new Crypter().decrypt(data,
                SecurityUtil.SECRET_KEY_HTTP_CHARGE_ALIPAY)));
    }
    
}