package com.hk.test.helper;

import com.intellij.openapi.ui.Messages;

/**
 * Created by Administrator on 2016/11/21.
 */
public class MessageUtil {

        public static void showErrorMessage(String context,String title){
            Messages.showMessageDialog(context,title,Messages.getErrorIcon());
        }
        public static void showMessage(String context,String title){
            Messages.showMessageDialog(context,title,Messages.getInformationIcon());
        }

}
