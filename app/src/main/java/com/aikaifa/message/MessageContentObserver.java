package com.aikaifa.message;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 短信验证码自动填充
 * 微信公众号 aikaifa
 */
public class MessageContentObserver extends ContentObserver {

    private Context mContext; // 上下文
    private Handler mHandler; // 更新UI线程
    private String code; // 验证码

    public MessageContentObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
    }

    /**
     * 回调函数, 当监听的Uri发生改变时，会回调该方法
     * 需要注意的是当收到短信的时候会回调两次
     * 收到短信一般来说都是执行了两次onchange方法.第一次一般都是raw的这个.
     * 这时虽然收到了短信.但是短信还没有真正写入到收件箱里面
     */
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.e("tag", uri.toString());
        if (uri.toString().equals("content://sms/raw")) {        // 第一次回调
            return;
        }
        Uri inboxUri = Uri.parse("content://sms/inbox");        // 第二次回调 查询收件箱里的内容
        Cursor c = mContext.getContentResolver().query(inboxUri, null, null, null, "date desc");  // 按时间顺序排序短信数据库
        if (c != null) {
            if (c.moveToFirst()) {
                String address = c.getString(c.getColumnIndex("address"));//发送方号码
                String body = c.getString(c.getColumnIndex("body")); // 短信内容
                if (!address.equals("10086")) {
                    return;
                }
                Pattern pattern = Pattern.compile("(\\d{6})");//正则表达式匹配验证码
                Matcher matcher = pattern.matcher(body);
                if (matcher.find()) {
                    code = matcher.group(0);
                    Message msg = Message.obtain();
                    msg.what = MainActivity.MSG_RECEIVE_CODE;
                    msg.obj = code;
                    mHandler.sendMessage(msg);
                }
            }
            c.close();
        }
    }
}
