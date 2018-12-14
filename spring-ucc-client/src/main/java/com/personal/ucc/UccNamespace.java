package com.personal.ucc;

/**
 * @author zhuangqianliao
 */
public class UccNamespace implements UccNamespaceHolder {

    private final String path;
    private final String readToken;
    private boolean isSubscribe;

    public UccNamespace(String path, String readToken) {
        this(path, readToken, false);
    }

    public UccNamespace(String path, String readToken, boolean isSubscribe) {
        this.path = path;
        this.readToken = readToken;
        this.isSubscribe = isSubscribe;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getReadToken() {
        return this.readToken;
    }

    @Override
    public boolean isSubscribe() {
        return this.isSubscribe;
    }
}
