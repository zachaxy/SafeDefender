package com.zachaxy.safedefender.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zachaxy.safedefender.R;
import com.zachaxy.safedefender.dao.AddressDao;

public class AddressActivity extends AppCompatActivity {

    private EditText etPhoneNumber;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        etPhoneNumber = (EditText) findViewById(R.id.et_addr_number);
        tvResult = (TextView) findViewById(R.id.tv_addr_result);
    }

    public void query(View v) {
        String number = etPhoneNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(number)) {
            String address = AddressDao.getAddress(number);
            tvResult.setText(address);
        }
    }
}
