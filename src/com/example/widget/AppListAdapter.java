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
package com.example.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.example.common.ApiAsyncTask.ApiRequestListener;
import com.example.common.MarketAPI;
import com.example.download.DownloadItem;
import com.example.download.DownloadManager;
import com.example.download.DownloadManager.Request;
import com.example.download.UpgradeInfo;
import com.example.session.Constants;
import com.example.session.Session;
import com.mappn.gfan.R;
import com.example.ui.PreloadActivity;
import com.example.util.ImageUtils;
import com.example.util.Utils;
import com.example.vo.DownloadInfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;



/**
 * GfanClient ListView associating adapter<br>
 * It has lazyload feature, which load data on-demand.
 * 
 * @author andrew.wang
 * 
 */
public class AppListAdapter extends BaseAdapter implements Observer, ApiRequestListener {

	private ArrayList<HashMap<String, Object>> mDataSource;
	private LazyloadListener mLazyloadListener;

	private int mResource;
	private int mDividerResource;
	private boolean mHasGroup;
	private boolean mIsLazyLoad;
	private String[] mFrom;
	private int[] mTo;
	private LayoutInflater mInflater;
	private Context mContext;
	private boolean mIsProductList;
	private HashMap<String, DownloadInfo> mDownloadingTask;
	private ArrayList<String> mInstalledList;
	private DownloadManager mDownloadManager;
	private HashMap<String, HashMap<String, Object>> mDownloadExtraInfo;
	private HashMap<String, String> mIconCache;
	private HashMap<String, HashMap<String, Object>> mCheckedList;
	private HashMap<String, UpgradeInfo> mUpdateList;
	private boolean mIsRankList;
	private String mPageType = Constants.GROUP_14;

    /**
	 * Application list adapter<br>
	 * �����ϣ�������View��ʾ������Key��Ӧ��ValueΪNull����
	 * 
	 * @param context
	 *            application context
	 * @param data
	 *            the datasource behind the listview
	 * @param resource
	 *            list item view layout resource
	 * @param from
	 *            the keys array of data source which you want to bind to the
	 *            view
	 * @param to
	 *            array of according view id
	 * @param hasGroup
	 *            whether has place holder
	 */
    public AppListAdapter(Context context, ArrayList<HashMap<String, Object>> data, int resource,
            String[] from, int[] to) {
        if (data == null) {
            mDataSource = new ArrayList<HashMap<String, Object>>();
        } else {
            mDataSource = data;
        }
        mContext = context;
        mResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCheckedList = new HashMap<String, HashMap<String, Object>>();
        mIconCache = new HashMap<String, String>();
    }

	/**
     * @return the mCheckedList
     */
    public HashMap<String, HashMap<String, Object>> getCheckedList() {
        return mCheckedList;
    }

    /**
	 * �����Ƿ�����ָ���
	 * 
	 * @param flag
	 *            Ĭ����false, �������ָ���
	 */
	public void setContainsPlaceHolder(boolean flag) {
		mHasGroup = flag;
	}

	/**
	 * ���÷ָ������ԴID
	 */
	public void setPlaceHolderResource(int id) {
		mDividerResource = id;
	}
	
	/**
	 * ���а���Ҫʶ����λ
	 */
	public void setRankList() {
	    mIsRankList = true;
	}
	
    /**
     * ����ͳ��Lable
     */
    public void setmPageType(String mPageType) {
        this.mPageType = mPageType;
    }

	/**
	 * ��Ʒ�б���Ҫˢ�²�Ʒ״̬
	 */
	public void setProductList() {
	    mIsProductList = true;
	    Session session = Session.get(mContext);
	    session.addObserver(this);
	    mDownloadManager = session.getDownloadManager();
	    mInstalledList = session.getInstalledApps();
	    mDownloadingTask = session.getDownloadingList();
	    mUpdateList = session.getUpdateList();
	    mDownloadExtraInfo = new HashMap<String, HashMap<String,Object>>();
	}
	
	/*
	 * How many items are in the data set represented by this Adapter.
	 */
	@Override
	public int getCount() {
		if (mDataSource == null) {
			return 0;
		}
		return mDataSource.size();
	}

	@Override
	public Object getItem(int position) {

		if (mDataSource != null && position < getCount()) {
			return mDataSource.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEmpty() {
		if (mDataSource == null || mDataSource.size() == 0) {
			return true;
		}
		return super.isEmpty();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	/*
	 * Clear all the data
	 */
	public void clearData() {
		if (mDataSource != null) {
			mDataSource.clear();
			notifyDataSetChanged();
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (mHasGroup && isPlaceHolder(position)) {
			// place holder for group
			return 1;
		}
		// normal item
		return 0;
	}

	/*
	 * Return the view types of the list adapter
	 */
	@Override
	public int getViewTypeCount() {
		if (mHasGroup) {
			return 2;
		} else {
			return 1;
		}
	}

	/**
	 * Lazyload web data
	 * 
	 * @param newData
	 */
	public void addData(ArrayList<HashMap<String, Object>> newData) {
		if (newData != null && newData.size() > 0) {
			mDataSource.addAll(getCount(), newData);
			notifyDataSetChanged();
		}
	}

	public void addData(HashMap<String, Object> newData) {
		if (newData != null) {
			mDataSource.add(getCount(), newData);
			notifyDataSetChanged();
		}
	}
	
    public void removeData(HashMap<String, Object> oldData) {
        if (mDataSource != null) {
            mDataSource.remove(oldData);
            notifyDataSetChanged();
        }
    }
    
    public void removeData(int position) {
        if (mDataSource != null) {
            mDataSource.remove(position);
            notifyDataSetChanged();
        }
    }

	public void insertData(HashMap<String, Object> newData) {
		if (newData != null) {
			mDataSource.add(0, newData);
			notifyDataSetChanged();
		}
	}

	public void setLazyloadListener(LazyloadListener listener) {
		mIsLazyLoad = true;
		mLazyloadListener = listener;
	}

	@Override
	public boolean isEnabled(int position) {
		if (mHasGroup) {
			return !isPlaceHolder(position);
		} else {
			return true;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// last 4 item trigger the lazyload event
		if (mIsLazyLoad && !mLazyloadListener.isEnd()
				&& (position == getCount() - 4)) {
			// fix the multi-load situation
			synchronized (this) {
				if (mLazyloadListener.isLoadOver()) {
					mLazyloadListener.lazyload();
                    Utils.trackEvent(mContext, mPageType, Constants.PRODUCT_LAZY_LOAD);
				}
			}
		}

		// reach here when list is not at the end
		assert (position < getCount());
		View v;
        if (convertView == null) {
            v = newView(position, parent);
        } else {
            v = convertView;
        }
        
        if (mIsProductList && mDownloadingTask != null) {
            
            // �б��д��������б�����񣬸���״̬
            HashMap<String, Object> item = mDataSource.get(position);
            String packageName = (String) item.get(Constants.KEY_PRODUCT_PACKAGE_NAME);
            if (mDownloadingTask.containsKey(packageName)) {
                DownloadInfo info = mDownloadingTask.get(packageName);
                // ���ع����У�ˢ�½���
                item.put(Constants.KEY_PRODUCT_INFO, info.mProgress);
                item.put(Constants.KEY_PRODUCT_DOWNLOAD, info.mProgressLevel);
            } else if (mInstalledList.contains(packageName)) {
                // �Ѿ���װ��Ӧ��
                if (mUpdateList.containsKey(packageName)) {
                    // ���Ը���
                    item.put(Constants.KEY_PRODUCT_DOWNLOAD, Constants.STATUS_UPDATE);
                } else {
                    item.put(Constants.KEY_PRODUCT_DOWNLOAD, Constants.STATUS_INSTALLED);
                }
            } else {
                Object result = item.get(Constants.KEY_PRODUCT_DOWNLOAD);
                if (result != null) {
                    int status = (Integer) result;
                    if (status == Constants.STATUS_PENDING) {
                        // ׼����ʼ���أ����账��
                    } else if (status != Constants.STATUS_DOWNLOADED) {
                        // Ĭ�ϵ�״̬��δ��װ
                        item.put(Constants.KEY_PRODUCT_DOWNLOAD, Constants.STATUS_NORMAL);
                    }
                }
            }
        }
		
		bindView(position, v);
		return v;
	}

	/*
	 * Create new view object and cache some views associated with it
	 */
	private View newView(int position, ViewGroup parent) {
		View v;
		if (mHasGroup && isPlaceHolder(position)) {
			v = mInflater.inflate(mDividerResource, parent, false);
		} else {
			v = mInflater.inflate(mResource, parent, false);
		}

		final int[] to = mTo;
		final int count = to.length;
		final View[] holder = new View[count];

        for (int i = 0; i < count; i++) {
            holder[i] = v.findViewById(to[i]);

/*            if (R.id.cb_install == to[i]) {
                if (holder[i] != null) {
                    ((CheckBox) holder[i]).setOnCheckedChangeListener(mCheckChangeListener);
                }
            }*/
        }

		v.setTag(holder);
		return v;
	}
	
	/*
	 * װ���ر���װѡ��
	 */
    private OnCheckedChangeListener mCheckChangeListener = new OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (Integer) buttonView.getTag();
            
            HashMap<String, Object> item =(HashMap<String, Object>) mDataSource.get(position) ;
            item.put(Constants.INSTALL_APP_IS_CHECKED, isChecked);
            
            if (isChecked) {
                mCheckedList.put((String) item.get(Constants.KEY_PRODUCT_ID), item);
            } else {
                mCheckedList.remove((String) item.get(Constants.KEY_PRODUCT_ID));
            }
            buttonView.setChecked(isChecked);
        }
    };

	/*
	 * bind the background data to the view
	 */
	private void bindView(int position, View view) {

		final HashMap<String, Object> dataSet = mDataSource.get(position);
		if (dataSet == null) {
			return;
		}

		final View[] holder = (View[]) view.getTag();
		final String[] from = mFrom;
		final int[] to = mTo;
		final int count = to.length;

        for (int i = 0; i < count; i++) {
            
            final View v = holder[i];
            
            if (v != null) {
               
                final Object data = dataSet.get(from[i]);
                if (data == null) {
                    // make this view invisible if value is empty
                    v.setVisibility(View.GONE);
                    continue;
                } else {
                    v.setVisibility(View.VISIBLE);
                }
                
                 if (v instanceof Checkable) {
                     
                     // Ϊװ���ر���¼�б�λ��
                     v.setTag(position);
                    if (data instanceof Boolean) {
                        ((Checkable) v).setChecked((Boolean) data);
                    } else {
                        throw new IllegalStateException(v.getClass().getName()
                                + " should be bound to a Boolean, not a " + data.getClass());
                    }
                } else if(v instanceof Button) {
                   
                    v.setTag(data);
                    
                } else if(v instanceof ImageButton) {
                    
                    // Note: keep the instanceof ImageButton be Top of ImageView 
                    // since ImageButton is ImageView
// TODO                   setButtonImage((ImageButton) v, text);
                } else if (v instanceof ImageView) {
                    
                    setViewImage(position, (ImageView) v, data);
                    
                } else if (v instanceof RatingBar) {
                  
                    setViewRating((RatingBar)v, data);
                    
                } else if (v instanceof TextView) {
                    
                    // Note: keep the instanceof TextView check at the bottom of
                    // these if since a lot of views are TextViews (e.g. CheckBoxes).
                    setViewText(position, (TextView) v, data);
                    
                } else {
                    throw new IllegalStateException(v.getClass().getName() + " is not a "
                            + " view that can be bounds by this SimpleAdapter");
                }
            }
        }
    }
	
	protected void setViewResource(View v, int position, int[] bitmaps) {
		if (v instanceof ImageView) {
			ImageView view = (ImageView) v;
			HashMap<String, Object> map = mDataSource.get(position);

			int flag = (Integer) map.get(String.valueOf(position));
			view.setImageResource(bitmaps[flag]);
		}
	}

	/*
	 * Set the value for RatingBar
	 */
	private void setViewRating(RatingBar v, Object rating) {
		if (rating instanceof Integer) {
			float ratingLevel = ((Integer) rating) / (float) 10;
			v.setRating(ratingLevel);
		}
	}

	/*
	 * Set text value for TextView
	 */
	private void setViewText(int position, TextView v, Object text) {

		if (text instanceof byte[]) {

			v.setText(Utils.getUTF8String((byte[]) text));

		} else if (text instanceof CharSequence) {

            if (mIsRankList && v.getId() == R.id.tv_name) {
                // ���а�
                v.setText((position + 1) + ". " + (CharSequence) text);
            } else {
                v.setText((CharSequence) text);
            }

		} else if(text instanceof Integer) {
		    
		    // Ӧ��״ָ̬ʾ��
		    v.setTag(position);
		    final int level = (Integer) text;
		    Drawable indicatorDrawble = v.getCompoundDrawables()[1];
		    indicatorDrawble.setLevel(level);
		    if(Constants.STATUS_NORMAL == level) {
		        // δ����
                v.setText((String) mDataSource.get(position).get(Constants.KEY_PRODUCT_PRICE));
                
		    } else if(Constants.STATUS_PENDING == level) {
		        // ׼����ʼ����
                v.setText(mContext.getString(R.string.download_status_downloading));
		        
		    } else if(Constants.STATUS_DOWNLOADED == level) {
		        // �Ѿ����أ�δ��װ
		        v.setText(mContext.getString(R.string.download_status_downloaded));
		        
		    } else if(Constants.STATUS_INSTALLED == level) {
		        // �Ѿ���װ
		        v.setText(mContext.getString(R.string.download_status_installed));
		        
		    } else if(Constants.STATUS_UPDATE == level) {
		        
		        // �и���
		        v.setText(mContext.getString(R.string.operation_update));
		        
		    } else {
		        // ������
		        v.setText((String) mDataSource.get(position).get(Constants.KEY_PRODUCT_INFO));
		        
		    }
	        // Ϊ���ذ�ť���¼�
            v.setOnClickListener(mDownloadListener);
		}
	}

	/*
	 * Set drawable value for ImageView
	 */
	private void setViewImage(int position, ImageView v, Object obj) {
        
	    Drawable oldDrawable = v.getDrawable();
        if (oldDrawable != null) {
            // clear the CALLBACK reference to prevent of OOM error
            oldDrawable.setCallback(null);
        }
        
        if(obj instanceof Drawable) {
            // here is one drawable object
            v.setImageDrawable((Drawable) obj);
            
        } else if(obj instanceof String) {
            // here is one remote object (URL)
            ImageUtils.download(mContext, (String) obj, v);
        } else if(obj instanceof Boolean) {
            
            if ((Boolean) obj) {
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.INVISIBLE);
            }
        }
	}

    private OnClickListener mDownloadListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();
            HashMap<String, Object> item = mDataSource.get(position);
            int status = (Integer) item.get(Constants.KEY_PRODUCT_DOWNLOAD);
            int payType = (Integer) item.get(Constants.KEY_PRODUCT_PAY_TYPE);
            if (Constants.STATUS_NORMAL == status
                    || Constants.STATUS_UPDATE == status) {
                
                if (Constants.PAY_TYPE_PAID == payType) {
                    if (Session.get(mContext).isLogin()) {
                        Intent intent = new Intent(mContext, PreloadActivity.class);
                        intent.putExtra(Constants.EXTRA_PRODUCT_ID,
                                (String) item.get(Constants.KEY_PRODUCT_ID));
                        intent.putExtra(Constants.IS_BUY, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } else {
                       /* Intent loginIntent = new Intent(mContext, LoginActivity.class);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(loginIntent);*/
                    }
                } else {
                    
                    Utils.trackEvent(mContext, mPageType, Constants.DIRECT_DOWNLOAD);
                    
                    String pid = (String) item.get(Constants.KEY_PRODUCT_ID);
                    String iconUrl = (String) item.get(Constants.KEY_PRODUCT_ICON_URL_LDPI);
                    String pkgName = (String) item.get(Constants.KEY_PRODUCT_PACKAGE_NAME);
                    // ��ʼ���أ������û���ε��
                    item.put(Constants.KEY_PRODUCT_DOWNLOAD, Constants.STATUS_PENDING);
                    mIconCache.put(pkgName, iconUrl);
                    MarketAPI.getDownloadUrl(mContext, AppListAdapter.this, pid,
                            Constants.SOURCE_TYPE_GFAN);
                    mDownloadExtraInfo.put(pid, item);
                    notifyDataSetChanged();
                }
                
            } else if (Constants.STATUS_DOWNLOADED == status) {

                // ��װӦ��
                String packageName = (String) item.get(Constants.KEY_PRODUCT_PACKAGE_NAME);
                String filePath = (String) item.get(Constants.KEY_PRODUCT_INFO);
                DownloadInfo info = mDownloadingTask.get(packageName);
                if (info != null) {
                    Utils.installApk(mContext, new File(info.mFilePath));
                } else if (!TextUtils.isEmpty(filePath)) {
                    Utils.installApk(mContext, new File(filePath));
                }
            } else if(Constants.STATUS_INSTALLED == status) {
                
                // �Ѿ���װ��ȥ��Ʒ��ϸҳ
                String packageName = (String) item.get(Constants.KEY_PRODUCT_PACKAGE_NAME);
                Intent detailIntent = new Intent(mContext, PreloadActivity.class);
                detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                detailIntent.putExtra(Constants.EXTRA_PACKAGE_NAME, packageName);
                mContext.startActivity(detailIntent);
            }
        }
    };

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
        case  MarketAPI.ACTION_GET_DOWNLOAD_URL:
            
            DownloadItem info = (DownloadItem) obj;
            HashMap<String, Object> downloadItem = mDownloadExtraInfo.get(info.pId);
            Request request = new Request(Uri.parse(info.url));
            request.setTitle((String)downloadItem.get(Constants.KEY_PRODUCT_NAME));
            request.setPackageName(info.packageName);
            request.setIconUrl(mIconCache.get(info.packageName));
            request.setSourceType(com.example.download.Constants.DOWNLOAD_FROM_MARKET);
            request.setMD5(info.fileMD5);
            mDownloadManager.enqueue(request);
           // Utils.makeEventToast(mContext, mContext.getString(R.string.download_start), false);
            break;

        default:
            break;
        }
    }

    @Override
    public void onError(int method, int statusCode) {
        //Utils.makeEventToast(mContext, mContext.getString(R.string.alert_dialog_error), false);
    }
	
	/**
	 * ����״̬���£�ˢ���б�״̬
	 */
    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable arg0, Object arg1) {
        
        if (arg1 instanceof HashMap) {
            mDownloadingTask = (HashMap<String, DownloadInfo>) arg1;
            notifyDataSetChanged();
        } else if(arg1 instanceof Integer) {
            notifyDataSetChanged();
        }
    }
	
	/*
	 * �Ƿ�ָ���
	 */
	private boolean isPlaceHolder(int position) {
	    HashMap<String, Object> item = mDataSource.get(position);
        return (Boolean) item.get(Constants.KEY_PLACEHOLDER);
	}

	/**
	 * Lazyload linstener If you want use the lazyload function, must implements
	 * this interface
	 */
	public interface LazyloadListener {

		/**
		 * You should implements this method to justify whether should do
		 * lazyload
		 * 
		 * @return
		 */
		boolean isEnd();

		/**
		 * Do something that process lazyload
		 */
		void lazyload();

		/**
		 * Indicate whether the loading process is over
		 * 
		 * @return
		 */
		boolean isLoadOver();
	}

}