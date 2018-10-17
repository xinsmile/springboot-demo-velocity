package com.xingy.framework.directive;

/**
 * velocity LinkTool 指令重写
 * @author xinguiyuan
 */
public class LinkTool extends org.apache.velocity.tools.view.tools.LinkTool {

    @Override
    public String getContextURL() {
        String url = super.getContextURL();//项目地址
        String result = "";
        if (url.indexOf(";JSESSIONID") >= 0) {
            result = url.substring(0, url.indexOf(";JSESSIONID"));
        } else {
            result = super.getContextURL();
        }
        if (result.endsWith("/")) {//去掉结尾的分隔符
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
