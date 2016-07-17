package com.zachaxy.safedefender.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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


    //用于在添加黑名单时,选择拦截模式的索引,这里设置为全局变量.
    private int modeIndex;

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
                    jumpPage(v);
                    return true;
                }
                return false;
            }
        });

        //设置键盘确认键的上文字
        mJumpPage.setImeOptions(EditorInfo.IME_ACTION_GO);

        //界面初始化时,不展示键盘
        //closeInputWindown();
        //TODO:为listview添加分批处理逻辑,当下滑到没有数据后,继续从数据库添加数据库
        /*mBlackListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        break;
                }
            }

            //只要listview滑动,就会调用该方法.
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });*/
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
        if (pageIndex == currentPage + 1) {
            return;
        }

        if (pageIndex < 1 || pageIndex > getTotalPage()) {
            Toast.makeText(this, "您要跳转的页面号不在范围内", Toast.LENGTH_SHORT).show();
            mJumpPage.setText(currentPage + 1 + "");
            return;
        }


        mWaitBlackProgress.setVisibility(View.VISIBLE);
        currentPage = pageIndex - 1;
        initData();
    }


    public void addBlack(View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_add_black, null);
        mJumpPage.clearFocus();


        final EditText addBlackNumber = (EditText) view.findViewById(R.id.et_add_black_number);


        Spinner checkBlackMode = (Spinner) view.findViewById(R.id.sp_black_mode);

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(CallMsgSafeActivity.this, android.R.layout.simple_spinner_item, mBlackModes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        checkBlackMode.setAdapter(spinnerAdapter);


        //checkBlackMode.setSelection(2, true);
        checkBlackMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modeIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        builder.setView(view);

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(addBlackNumber.getText().toString())) {
                    Toast.makeText(CallMsgSafeActivity.this, "黑名单号码不能为空", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                //TODO:改变逻辑,新添加的黑名单显示在第一个条目.
                boolean addFlag = dao.add(addBlackNumber.getText().toString(), String.valueOf(modeIndex));
                if (!addFlag) {
                    Toast.makeText(CallMsgSafeActivity.this, "黑名单号码添加失败", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                itemCount++;
                //当前页不是最后一页,直接跳转到最后一页
                if (currentPage != lastPage()) {
                    currentPage = lastPage();
                    initData();
                    mBlackListView.setSelection(mBlackList.size() - 1);
                } else {
                    //正好在当前页,并且当前页不满20个,直接在view中显示即可.
                    mBlackList.add(new BlackItemInfo(addBlackNumber.getText().toString(), String.valueOf(modeIndex)));
                    adapter.notifyDataSetChanged();
                    mBlackListView.setSelection(mBlackListView.getLastVisiblePosition());
                }


                Toast.makeText(CallMsgSafeActivity.this, "黑名单添加成功", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();


        //-------------
       /* addBlackNumber.requestFocus();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(addBlackNumber, InputMethodManager.SHOW_FORCED);*/
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

    private int lastPage() {
        return getTotalPage() - 1;
    }


    /*private void closeInputWindown() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private void openInputWindown() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mJumpPage, InputMethodManager.SHOW_FORCED);
    }*/
   /* public void lostFocus(View v) {
        switch (v.getId()) {
            case R.id.et_black_page:
                break;
            *//*case R.id.rl_cm_losefocus:
                System.out.println("down");
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;*//*
            default:
                mJumpPage.clearFocus();
                mWaitBlackProgress.requestFocus();
                break;
        }
    }*/

}
