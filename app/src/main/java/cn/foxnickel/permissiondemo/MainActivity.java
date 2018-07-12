package cn.foxnickel.permissiondemo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements PermissionGen.OnPermissionCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionGen
                .with(this)
                .addRequestCode(100)
                .permissions(Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(int requestCode) {

    }

    @Override
    public void onPermissionDenied(int requestCode, String[] deniedPermissions) {
        if (!PermissionGen.shouldShowRequestPermissionRationale(this, deniedPermissions)) {
            // 用户选择了不再提示
            // todo 显示一个dialog，然后用户确定之后跳转到权限控制界面
            PermissionGen.showReasonDialog(this, "不授权没法用啊大哥，去授权吧", new PermissionGen.OnDecideListener() {
                @Override
                public void onGrant() {
                    PermissionGen.openPermissionSetting(MainActivity.this);
                }

                @Override
                public void onDeny() {
                    finish();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "请授予相关权限，否则应用将无法运行", Toast.LENGTH_SHORT).show();
            PermissionGen.with(this).addRequestCode(requestCode).permissions(deniedPermissions).request();
        }
    }
}
