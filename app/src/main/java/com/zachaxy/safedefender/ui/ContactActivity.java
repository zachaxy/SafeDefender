package com.zachaxy.safedefender.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.bean.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends Activity {

    private ListView mContactListView;
    private ContactAdapter mContactAdapter;
    private List<ContactInfo> mContactList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        mContactListView = (ListView) findViewById(R.id.lv_contacts);
        mContactAdapter = new ContactAdapter();
        mContactListView.setAdapter(mContactAdapter);
        readContact();

        mContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ContactActivity.this,mContactList.get(position).getName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("number",mContactList.get(position).getPhoneNumber());
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    private void readContact() {
        Cursor cursor = null;
        try {
            // 查询联系人数据
            cursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                // 获取联系人姓名
                String displayName = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                // 获取联系人手机号
                String number = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                mContactList.add(new ContactInfo(displayName, number));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    class ContactAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mContactList.size();
        }

        @Override
        public Object getItem(int position) {
            return mContactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ContactInfo contactInfo = mContactList.get(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(ContactActivity.this, R.layout.contact_item, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) view.findViewById(R.id.tv_contact_name);
                viewHolder.number = (TextView) view.findViewById(R.id.tv_contact_number);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.name.setText(contactInfo.getName());
            viewHolder.number.setText(contactInfo.getPhoneNumber());
            return view;
        }
    }

    class ViewHolder {
        TextView name;
        TextView number;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("number", "");
        setResult(RESULT_OK, intent);
        finish();
    }
}



