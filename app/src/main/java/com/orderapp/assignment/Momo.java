package com.orderapp.assignment;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.orderapp.assignment.Model.MoMoConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Momo extends AppCompatActivity {

    int environment = 1;//developer default - Production environment = 2
    @BindView(R.id.rdEnvironmentProduction)
    RadioButton rdEnvironmentProduction;
    @BindView(R.id.rdGroupEnvironment)
    RadioGroup rdGroupEnvironment;
    @BindView(R.id.btnPaymentMoMo)
    Button btnPaymentMoMo;
    @BindView(R.id.btnMappingMoMo)
    Button btnMappingMoMo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_momo);
        ButterKnife.bind(this);

        rdGroupEnvironment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
//                if (checkedId == R.id.rdEnvironmentDebug) {
//                    environment = 0;
//                }else if (checkedId == R.id.rdEnvironmentDeveloper) {
//                    environment = 1;
//                }else if (checkedId == R.id.rdEnvironmentProduction) {
//                    environment = 2;
//                }
            }
        });
    }

    @OnClick({R.id.btnPaymentMoMo, R.id.btnMappingMoMo})
    public void onViewClicked(View view) {
        Intent intent;
        Bundle data = new Bundle();
        switch (view.getId()) {
            case R.id.btnPaymentMoMo:
                intent = new Intent(Momo.this, PaymentMomo.class);
                data.putInt(MoMoConstants.KEY_ENVIRONMENT, environment);
                intent.putExtras(data);
                startActivity(intent);
                break;
            case R.id.btnMappingMoMo:
                intent = new Intent(Momo.this, MappingActivity.class);
                data.putInt(MoMoConstants.KEY_ENVIRONMENT, environment);
                intent.putExtras(data);
                startActivity(intent);
                break;
        }
    }
}
