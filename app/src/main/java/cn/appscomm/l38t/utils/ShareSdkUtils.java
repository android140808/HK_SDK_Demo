package cn.appscomm.l38t.utils;

import android.content.Context;

import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/26 16:47
 */
public class ShareSdkUtils {

    /**
     * 演示调用ShareSDK执行分享
     *
     * @param context
     * @param platformToShare 指定直接分享平台名称（一旦设置了平台名称，则九宫格将不会显示）
     * @param showContentEdit 是否显示编辑页
     */
    public  static void showShare(Context context, String platformToShare,String url,String title,String content,String imagePath ,boolean showContentEdit,PlatformActionListener listener) {
        OnekeyShare oks = new OnekeyShare();
        oks.setSilent(!showContentEdit);
        if (platformToShare != null) {
            oks.setPlatform(platformToShare);
        }
        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        // 令编辑页面显示为Dialog模式
        oks.setDialogMode();
        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setText(content);
        oks.setComment(content);
        oks.setImagePath(imagePath);  //分享sdcard目录下的图片
        oks.setUrl(url); //微信不绕过审核分享链接
        oks.setFilePath(imagePath);  //filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供
        oks.setSite(title);  //QZone分享完之后返回应用时提示框上显示的名称
        oks.setSiteUrl(url);//QZone分享参数
        // 将快捷分享的操作结果将通过OneKeyShareCallback回调
        oks.setCallback(listener);
        // 为EditPage设置一个背景的View
        //oks.setEditPageBackground(getPage());
        // 隐藏九宫格中的新浪微博
        // oks.addHiddenPlatform(SinaWeibo.NAME);
        // 启动分享
        oks.show(context);
    }
}
