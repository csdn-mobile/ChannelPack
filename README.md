# ChannelPack

使用ApkTool实现的多渠道打包工具。

### [点击下载JAR包](https://csdn-app.csdn.net/ChannelPack.jar)

## 注意
* 目前仅支持mac
* 需要提前安装ApkTool
* 第一个版本仅支持v1签名

## 使用步骤
### Step 1. 安装Apktool
[官方文档](https://ibotpeaches.github.io/Apktool/install)
1. 按照步骤下载 **apktool.jar** 和 **apktool** 文件
2. 将 **apktool.jar** 和 **apktool** 文件放到 /usr/local/bin 文件夹下
3. 执行chmod +x赋予文件执行权限
```
 chmod +x /usr/local/bin/apktool.jar 
 chmod +x /usr/local/bin/apktool 
```
4. 执行apktool指令，可获取内容则为安装成功

### Step 2. 下载ChannelPack.jar
[点击下载JAR包](https://csdn-app.csdn.net/ChannelPack.jar)

### Step 3. 准备渠道列表txt文件
以换行符分割，保存为txt文件
```
oppo
xiaomi
vivo
baidu
huawei
qq
```

### Step 4. 生成apk文件
将apk中的AndroidManifest.xml的渠道修改为**package_channel**
```
<meta-data
    android:name="UMENG_CHANNEL"
    android:value="package_channel" />
<meta-data
    android:name="ANALYSYS_CHANNEL"
    android:value="package_channel" />
```

### Step 5. 在mac终端输入指令
一共6个参数，不需要添加[]，每个参数以空格分割（文件直接拖入终端即可）
```
java -jar [ChannelPack.jar路径] [原APK路径] [渠道txt路径] [keystore路径] [keyAlias] [keyPassword] 
```
1. Step2下载的ChannelPack.jar
2. 需要生成渠道的APK
3. Step3生成的渠道txt文件
4. 签名需要的keystore文件
5. keystore的alias（别名）
6. keystore的密码

## 原理浅解
1. Apktool是一个apk逆向工具，可以反编译apk、生成未签名apk。
2. 反编译apk，修改AndroidManifest的渠道信息
3. 生成未签名apk
4. 通过签名文件生成签名包

## 版本记录
|版本号|更新内容|
|---|---|
|1.0.0|First Version|
