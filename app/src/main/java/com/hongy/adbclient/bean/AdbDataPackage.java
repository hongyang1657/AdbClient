package com.hongy.adbclient.bean;

public class AdbDataPackage {

    private String pathAndAuthority;         //文件路径和权限

    private byte[] data;       //数据

    private int progress;      //进度 0-100

   public String getPathAndAuthority() {
        return pathAndAuthority;
    }

    public void setPathAndAuthority(String pathAndAuthority) {
        this.pathAndAuthority = pathAndAuthority;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
