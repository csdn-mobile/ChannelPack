# ChannelPack

使用ApkTool实现的多渠道打包工具。

## 注意
* 目前仅支持mac

## 使用步骤
### Step 1. 下载集成工具包
[点击下载工具包](https://csdn-app.csdn.net/ChannelPackCore.zip)
1. 解压ChannelPackCore.zip
2. 将/ChannelPackCore/bin文件夹中的文件全部拷贝到 /usr/local/bin 文件夹下
3. 执行chmod +x赋予文件执行权限（复制以下代码到终端中执行）
```
 chmod +x /usr/local/bin/apktool.jar 
 chmod +x /usr/local/bin/apktool 
 chmod +x /usr/local/bin/apksigner.jar 
 chmod +x /usr/local/bin/apksigner 
```
4. 终端输入apktool指令，可获取内容则为集成成功

### Step 2. 准备渠道列表txt文件
以换行符分割，保存为txt文件
```
oppo
xiaomi
vivo
baidu
huawei
qq
```

### Step 3. 生成apk文件
将apk中的AndroidManifest.xml的渠道修改为**package_channel**
```
<meta-data
    android:name="UMENG_CHANNEL"
    android:value="package_channel" />
<meta-data
    android:name="ANALYSYS_CHANNEL"
    android:value="package_channel" />
```

### Step 4. 在mac终端输入指令
一共7个参数，不需要添加[]，每个参数以空格分割（文件直接拖入终端即可）
```
java -jar [ChannelPack_v1.X.X.jar路径] [原APK路径] [渠道txt路径] [keystore路径] [keystorePassword] [keyAlias] [keyPassword] 
```
1. Step1下载的解压ChannelPackCore.zip里面的ChannelPack_v1.X.X.jar
2. 需要生成渠道的APK
3. Step3生成的渠道txt文件
4. 签名需要的keystore文件
4. keystore文件密码
5. key的alias（别名）
6. key的alias密码

## 原理浅解
1. Apktool是一个apk逆向工具，可以反编译apk、生成未签名apk。
2. 反编译apk，修改AndroidManifest的渠道信息
3. 生成未签名apk
4. 通过签名文件生成签名包

## 版本记录
|版本号|更新内容|
|---|---|
|1.1.0|同时支持V1、V2签名|
|1.0.0|First Version|
