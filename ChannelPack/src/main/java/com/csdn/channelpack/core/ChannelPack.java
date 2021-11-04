package com.csdn.channelpack.core;

import com.csdn.channelpack.core.utils.TextUtils;

import java.io.File;

public class ChannelPack {

    public static void main(String[] args) {
        System.out.println("|================ChannelPack================|");
        System.out.println("|==@Start==|");

        if (args == null || args.length != 5) {
            // 传入4个参数
            // apk路径、渠道文件路径、签名文件路径、签名密码
            System.out.println("|==@Error==需要输入5个参数：apk路径、渠道文件路径、签名文件路径、签名别名、签名密码==|");
            return;
        }

        String apkFilePath = args[0];
        String channelFilePath = args[1];
        String keyFilePath = args[2];
        String keyAlias = args[3];
        String keyPassword = args[4];

        if (TextUtils.isEmpty(apkFilePath)) {
            System.out.println("|==@Error==apk路径不能为空==|");
            return;
        }

        File apkFile = new File(apkFilePath);
        if (!apkFile.exists()) {
            System.out.println("|==@Error==apk文件不存在==|");
            return;
        }

        if (TextUtils.isEmpty(channelFilePath)) {
            System.out.println("|==@Error==渠道文件路径不能为空==|");
            return;
        }

        File channelFile = new File(channelFilePath);
        if (!channelFile.exists()) {
            System.out.println("|==@Error==渠道文件不存在==|");
            return;
        }

        System.out.println("|==@读取参数成功==|");

        Pack pack = new Pack(apkFilePath, channelFilePath, keyFilePath, keyAlias, keyPassword);
        pack.start();
    }
}