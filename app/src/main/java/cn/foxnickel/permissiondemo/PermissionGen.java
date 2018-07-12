package cn.foxnickel.permissiondemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickelFox
 * @date 2018/7/12.
 */
public class PermissionGen {
    private String[] mPermissions;
    private int mRequestCode;
    private Object mObject;
    private static final String TAG = PermissionGen.class.getSimpleName();

    private PermissionGen(Object object) {
        mObject = object;
    }

    public static PermissionGen with(Activity activity) {
        return new PermissionGen(activity);
    }

    public static PermissionGen with(Fragment fragment) {
        return new PermissionGen(fragment);
    }

    public PermissionGen permissions(String... permissions) {
        mPermissions = permissions;
        return this;
    }

    public PermissionGen addRequestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }

    public void request() {
        // 查找未获得的权限
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : mPermissions) {
            if (!hasPermission(permission)) {
                deniedPermissions.add(permission);
            }
        }
        // 请求未获得的权限
        requestPermissions(mObject, mRequestCode,
                deniedPermissions.toArray(new String[deniedPermissions.size()]));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions(Object o, int requestCode, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        if (o instanceof Activity) {
            ((Activity) o).requestPermissions(permissions, requestCode);
        } else if (o instanceof Fragment) {
            ((Fragment) o).requestPermissions(permissions, requestCode);
        }
    }

    public static void onRequestPermissionsResult(OnPermissionCallback callback, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 判断用户权限选择的结果，如果有未授权的。弹出提示
        Log.i(TAG, "onRequestPermissionsResult: reqCode " + requestCode);
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        if (deniedPermissions.size() != 0) {
            callback.onPermissionDenied(requestCode, deniedPermissions.toArray(new String[deniedPermissions.size()]));
        } else {
            callback.onPermissionGranted(requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String permission) {
        if (mObject instanceof Activity) {
            return ((Activity) mObject).checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        } else if (mObject instanceof Fragment) {
            return ((Fragment) mObject).getContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public interface OnPermissionCallback {
        /**
         * 用户授予了所有权限
         *
         * @param requestCode 请求码
         */
        void onPermissionGranted(int requestCode);

        /**
         * 用户有未授予的权限
         *
         * @param requestCode       请求码
         * @param deniedPermissions 未授予的权限
         */
        void onPermissionDenied(int requestCode, String[] deniedPermissions);
    }

    public static boolean shouldShowRequestPermissionRationale(Object o, String... permissions) {
        for (String permission : permissions) {
            if (!shouldShowRequestPermissionRationale(o, permission)) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean shouldShowRequestPermissionRationale(Object o, String permission) {
        if (o instanceof Activity) {
            return ((Activity) o).shouldShowRequestPermissionRationale(permission);
        } else if (o instanceof Fragment) {
            return ((Fragment) o).shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    public static void showReasonDialog(Context context, String message, final OnDecideListener listener) {
        new AlertDialog
                .Builder(context)
                .setTitle("权限说明")
                .setMessage(message)
                .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onGrant();
                    }
                })
                .setNegativeButton("拒绝授权", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDeny();
                    }
                })
                .create()
                .show();
    }

    /**
     * 用户决定是否去设置界面授权的回调
     */
    public interface OnDecideListener {
        /**
         * 用户允许去设置界面授权，一般在此调转到设置界面
         */
        void onGrant();

        /**
         * 用户拒绝去设置界面授权，一般在此结束应用
         */
        void onDeny();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void openPermissionSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }
}
