package com.csdn.channelpack.core;

import com.csdn.channelpack.core.utils.FileUtils;
import com.csdn.channelpack.core.utils.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Pack {
    /**
     * apk文件路径
     */
    private final String apkFilePath;
    /**
     * apk文件名称
     */
    private final String apkFileName;
    /**
     * 渠道文件路径
     */
    private final String channelFilePath;
    /**
     * 反编译后的文件夹
     */
    private final String unpackPath;
    /**
     * 当前路径
     */
    private final String curPath;
    /**
     * 已签名APK存放地址
     */
    private final String signFolderPath;
    /**
     * 签名文件路径
     */
    private final String keystorePath;
    /**
     * 签名文件密码
     */
    private final String keystorePassword;
    /**
     * 签名文件别名
     */
    private final String keyAlias;
    /**
     * 签名文件别名密码
     */
    private final String keyPassword;
    /**
     * 渠道列表
     */
    private final ArrayList<String> channelList = new ArrayList<>();

    public Pack(String apkFilePath, String channelFilePath, String keystorePath, String keystorePassword, String keyAlias, String keyPassword) {// 构造函数接受参数
        this.apkFilePath = apkFilePath;
        this.channelFilePath = channelFilePath;
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
        this.keyAlias = keyAlias;
        this.keyPassword = keyPassword;

        File apkFile = new File(apkFilePath);
        this.curPath = apkFile.getParentFile().getAbsolutePath();
        this.apkFileName = apkFile.getName().split("\\.apk")[0];
        this.unpackPath = curPath + "/" + apkFileName + "_temp";
        this.signFolderPath = curPath + "/[" + apkFileName + "]ChannelApk";
    }

    public void start() {
        try {
            // 1.解析渠道文件，获取渠道号
            getChannelList();
            // 2.使用apktool，反编译apk
            unpackApk();
            // 3.遍历渠道，修改manifest文件，并生成签名apk
            modifyChannelAndBuildApk();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("|==@Error==打包错误，结束==|");
        }
    }

    /**
     * 1.解析渠道文件，获取渠道号
     */
    private void getChannelList() throws Exception {
        System.out.println("|== Step.1 ==开始解析渠道文件==|");
        File file = new File(channelFilePath);
        if (file.exists() && file.isFile()) {
            BufferedReader br = null;
            FileReader fr = null;
            try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    channelList.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("|== Step.1 ==@Error==读取渠道失败==|");
            } finally {
                try {
                    if (fr != null) {
                        fr.close();
                    }
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (channelList.size() > 0) {
                System.out.println("|== Step.1 ==读取渠道成功==" + channelList.size() + "个渠道：" + channelList.toString() + "==|");
            } else {
                throw new Exception("|== Step.1 ==@Error==渠道列表为空==|");
            }
        } else {
            throw new Exception("|== Step.1 ==@Error==渠道文件不存在==|");
        }
    }

    /**
     * 2.使用apktool，反编译apk
     */
    private void unpackApk() throws Exception {
        System.out.println("|== Step.2 ==开始反编译APK==|");
        runCmd("apktool d -f -s " + apkFilePath + " -o " + unpackPath + " --only-main-classes");
        File unpackDir = new File(unpackPath);
        if (unpackDir.exists()) {
            System.out.println("|== Step.2 ==反编译APK成功==|");
        } else {
            throw new Exception("|== Step.2 ==@Error==反编译APK失败，未生成文件夹==|");
        }
    }

    /**
     * 3.遍历渠道，修改manifest文件，并生成签名apk
     */
    private void modifyChannelAndBuildApk() throws Exception {
        System.out.println("|== Step.3 ==开始生成渠道包==|");

        // Apk存放路径结果的目录
        File folderFile = new File(signFolderPath);
        // 清单文件
        File manifestFile = new File(unpackPath + "/AndroidManifest.xml");
        // 临时清单文件
        File tempManifestFile = new File(curPath + "/AndroidManifest.xml");
        // 移动清单文件到临时清单文件
        FileUtils.copyFile(manifestFile, tempManifestFile);

        // 遍历渠道，修改manifest文件，并生成未签名apk
        for (int i = 0; i < channelList.size(); i++) {
            String channel = channelList.get(i);
            System.out.println("|== Step 3.1 ==修改" + channel + "渠道的Manifest==|");
            BufferedReader br = null;
            FileReader fr = null;
            FileWriter fw = null;
            try {
                // 先删除被修改过的清单文件
                if (manifestFile.exists()) {
                    manifestFile.delete();
                }
                // 复制临时清单文件到文件夹内
                FileUtils.copyFile(tempManifestFile, manifestFile);
                // 开始修改清单文件
                fr = new FileReader(manifestFile);
                br = new BufferedReader(fr);
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    // 修改渠道，渠道必须使用[package_channel]占位
                    if (line.contains("package_channel")) {
                        line = line.replaceAll("package_channel", channel);
                    }
                    sb.append(line + "\n");
                }

                // 修改完成
                fw = new FileWriter(manifestFile);
                fw.write(sb.toString());
                System.out.println("|== Step 3.2 ==修改" + channel + "渠道的Manifest成功==|");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("|== Step.3 ==@Error==修改" + channel + "渠道的Manifest失败==|");
            } finally {
                try {
                    if (fr != null) {
                        fr.close();
                    }
                    if (br != null) {
                        br.close();
                    }
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 生成未签名的包
            System.out.println("|== Step 3.3 ==生成" + channel + "未签名渠道包==|");
            String unsignApk = folderFile.getAbsolutePath() + "/" + apkFileName + "_" + channel + "_unsign.apk";
            String cmdPack = String.format("apktool b %s -o %s", unpackPath, unsignApk);
            runCmd(cmdPack);
            System.out.println("|== Step 3.4 ==生成" + channel + "未签名渠道包成功==|");

            // 生成签名包
            if (!TextUtils.isEmpty(keystorePath) && !TextUtils.isEmpty(keystorePassword) && !TextUtils.isEmpty(keyAlias) && !TextUtils.isEmpty(keyPassword)) {
                System.out.println("|== Step 3.5 ==签名" + channel + "渠道包==|");
                String signApk = folderFile.getAbsolutePath() + "/" + apkFileName + "_" + channel + ".apk";

                String cmdKey = String.format("apksigner sign --ks %s --ks-pass pass:%s --ks-key-alias %s --key-pass pass:%s --out %s %s",
                        keystorePath, keyPassword, keyAlias, keyPassword, signApk, unsignApk);

//                String cmdKey = String.format("jarsigner -verbose -keystore %s -signedjar %s %s %s -storepass %s",
//                        keyFilePath, signApk, unsignApk, keyAlias, keyPassword);
                runCmd(cmdKey);
                System.out.println("|== Step 3.6 ==签名" + channel + "渠道包成功==|");
                // 删除未签名的包
                File unApk = new File(unsignApk);
                unApk.delete();
            }
        }
        // 删除临时清单文件
        if (tempManifestFile.exists()) {
            tempManifestFile.delete();
        }
        // 删除生成的临时文件夹
        runCmd("rm -r -f " + unpackPath);
    }

    /**
     * 执行cmd指令
     */
    public void runCmd(String cmd) throws Exception {
        System.out.println("[cmd]" + cmd);
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            isr = new InputStreamReader(process.getInputStream());
            br = new BufferedReader(isr);
            String msg;
            while ((msg = br.readLine()) != null) {
                System.out.println(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("|==@Error==执行cmd命令出错==|");
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
