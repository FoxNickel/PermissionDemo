# PermissionDemo
一个简单封装了6.0权限请求的库

- 用法：
```
Activity或Fragment继承自PermissionGen.OnPermissionCallback接口
// 申请
PermissionGen
            .with(this)
            .addRequestCode(REQUEST_PERMISSIONS)
            .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .request();
// 重写以下回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        initLocation();
    }

    @Override
    public void onPermissionDenied(int requestCode, String[] deniedPermissions) {
        if (!PermissionGen.shouldShowRequestPermissionRationale(this, deniedPermissions)) {
            // 用户选择了不再提示
            PermissionGen.showReasonDialog(this,
                    "日历需要使用你的定位权限以确定你当前的位置，从而为你提供当前位置的天气信息",
                    new PermissionGen.OnDecideListener() {
                        @Override
                        public void onGrant() {
                            PermissionGen.openPermissionSetting(WeatherLocationActivity.this);
                        }

                        @Override
                        public void onDeny() {
                            finish();
                        }
                    });
        } else {
            showToast("日历需要使用你的定位权限以确定你当前的位置，从而为你提供当前位置的天气信息");
            finish();
        }
    }
```
