package com.personal.ucc;

import com.personal.config.NamespaceHolder;

/**
 * @author zhuangqianliao
 */
public interface UccNamespaceHolder extends NamespaceHolder {

    /**
     * Path
     * @return the path
     */
    String getPath();

    /**
     * read token
     * @return the read token
     */
    String getReadToken();

    /**
     * whether subscribe path or not
     * @return boolean
     */
    boolean isSubscribe();

    /**
     * override getNamespace()
     * it's alias for getPath()
     * @return the path
     */
    @Override
    default String getNamespace() {
        return getPath();
    }
}
