package com.zachaxy.safedefender.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.bean.BlackItemInfo;
import com.zachaxy.safedefender.dao.BlackListDao;

import java.util.List;

public class CallMsgSafeActivity extends AppCompatActivity {

    private ListView mBlackListView;
    private LinearLayout mWaitBlackProgress;
    private EditText mJumpPage;
    private TextView mPageNumbers;
    private List<BlackItemInfo> mBlackList;

    //dao也只在当前页面第一次加载的时候进行一次初始化
    private BlackListDao dao;
    private String[] mBlackModes = {"拦截电话", "拦截短信", "拦截电话+短信"};  //0,1,2
    private BlackListAdapter adapter;
    private int currentPage = 0;
    private final int pageSize = 20;
    private int itemCount;

    //程序在第一次加载该Activity的时候进行一次读取黑名单总条目的操作,而不是每次翻页的时候读取
    private boolean firstLoad = true;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter = new BlackListAdapter();
                    mBlackListView.setAdapter(adapter);
                    mWaitBlackProgress.setVisibility(View.INVISIBLE);
                    mJumpPage.setText(currentPage + 1 + "");
                    mPageNumbers.setText("/" + getTotalPage());
                    break;
                case 1:
                    adapter.notifyDataSetChanged();
                    mJumpPage.setText(currentPage + 1 + "");
                    mPageNumbers.setText("/" + getTotalPage());
                    break;
                default:
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_msg_safe);

        initView();
        initData();

    }

    private void initView() {
        mBlackListView = (ListView) findViewById(R.id.lv_cm_backlist);
        mWaitBlackProgress = (LinearLayout) findViewById(R.id.ll_wait_balcklist);
        mPageNumbers = (TextView) findViewById(R.id.tv_black_pages);
        mJumpPage = (EditText) findViewById(R.id.et_black_page);

        //设置键盘按下回车键后的响应动作
        mJumpPage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.d("###", "onKey: 按下确认键");
                    jumpPage(v);
                    return true;
                }
                return false;
            }
        });

        //设置键盘确认键的上文字
        mJumpPage.setImeOptions(EditorInfo.IME_ACTION_GO);
    }


    private void initData() {
        new Thread() {
            @Override
            public void run() {
                if (firstLoad) {
                    dao = new BlackListDao(CallMsgSafeActivity.this);
                    itemCount = dao.getTotalItem();
                    firstLoad = false;
                }
                mBlackList = dao.findPart(currentPage, pageSize);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private class BlackListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBlackList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            BlackItemInfo blackItemInfo = mBlackList.get(position);

            final String number = blackItemInfo.getNumber();

            View view;
            BlackItemViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(CallMsgSafeActivity.this, R.layout.black_item, null);
                viewHolder = new BlackItemViewHolder();
                viewHolder.number = (TextView) view.findViewById(R.id.tv_black_number);
                viewHolder.mode = (TextView) view.findViewById(R.id.tv_black_mode);
                viewHolder.delete = (ImageView) view.findViewById(R.id.img_delete_black);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (BlackItemViewHolder) view.getTag();
            }
            viewHolder.number.setText(number);
            viewHolder.mode.setText(mBlackModes[Integer.valueOf(blackItemInfo.getMode())]);

            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean result = dao.delete(number);
                    if (result) {
                        Toast.makeText(CallMsgSafeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        itemCount--;
                        mBlackList.remove(position);
                        //TODO:验证这里是否可以通过handler来设置...handelr可以设置/直接在这里执行相应逻辑也可以.
                        //handler.sendEmptyMessage(1);
                        adapter.notifyDataSetChanged();
                        mJumpPage.setText(currentPage + 1 + "");
                        mPageNumbers.setText("/" + getTotalPage());
                        if (currentPage == getTotalPage()) {
                            currentPage--;
                            initData();
                        }
                    } else {
                        Toast.makeText(CallMsgSafeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return view;
        }
    }

    class BlackItemViewHolder {
        TextView number;
        TextView mode;
        ImageView delete;
    }

    public void prePage(View v) {
        if (currentPage == 0) {
            Toast.makeText(this, "已经是第一页了", Toast.LENGTH_SHORT).show();
            return;
        }
        mWaitBlackProgress.setVisibility(View.VISIBLE);
        currentPage--;
        initData();
    }

    public void nextPage(View v) {
        if (currentPage == getTotalPage() - 1) {
            Toast.makeText(this, "已经是最后一页了", Toast.LENGTH_SHORT).show();
            return;
        }
        mWaitBlackProgress.setVisibility(View.VISIBLE);
        currentPage++;
        initData();
    }

    public void jumpPage(View v) {
        if (TextUtils.isEmpty(mJumpPage.getText())) {
            Toast.makeText(this, "请输入您要跳转的页面号", Toast.LENGTH_SHORT).show();
            mJumpPage.setText(currentPage + 1 + "");
            return;
        }

        int pageIndex = Integer.valueOf(mJumpPage.getText().toString());
        if (pageIndex < 1 || pageIndex > getTotalPage()) {
            Toast.makeText(this, "您要跳转的页面号不在范围内", Toast.LENGTH_SHORT).show();
            mJumpPage.setText(currentPage + 1 + "");
            return;
        }
        mWaitBlackProgress.setVisibility(View.VISIBLE);
        currentPage = pageIndex - 1;
        initData();
    }

    /***
     * 获取到数据库中数据一共有多少页,可以在ui线程中执行!!!
     *
     * @return 返回总页数
     */
    public int getTotalPage() {
        int totalPage = itemCount / pageSize;
        if (itemCount % pageSize == 0) {
            return totalPage;
        } else {
            return totalPage + 1;
        }
    }

}
