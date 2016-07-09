package com.zachaxy.safedefender.ui;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        //设置动态改变自动查询
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String address = AddressDao.getAddress(s.toString());
                tvResult.setText(address);
            }
        });
        tvResult = (TextView) findViewById(R.id.tv_addr_result);
    }

    public void query(View v) {
        String number = etPhoneNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(number)) {
            String address = AddressDao.getAddress(number);
            tvResult.setText(address);
        } else {
            Animation shake = new ScaleAnimation(0.95f,1f,0.95f,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            shake.setDuration(80);
            shake.setRepeatCount(3);
            etPhoneNumber.startAnimation(shake);
            Toast.makeText(this,"查询号码不能为空",Toast.LENGTH_SHORT).show();
            viberate();
        }
    }

    private void viberate(){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //直接震动两秒
        //vibrator.vibrate(2000);

        //周期性的震动:现等待1s,在震动2s,在等待1s,在震动2s, -1表示不循环,只执行一次,0表示从头开始循环,>0表示从第i个位置开始循环
        vibrator.vibrate(new long[]{1000,2000,1000,2000},-1);

        //取消震动
        //vibrator.cancel();
    }
}
