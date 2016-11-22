package com.hk.test.action;

import com.hk.test.entity.ClassModel;
import com.hk.test.helper.ClassCreateHelper;
import com.hk.test.helper.MessageUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import jdk.internal.util.xml.impl.Input;
import org.apache.http.util.TextUtils;

import java.io.IOException;

/**
 * Created by Administrator on 2016/11/21.
 */
public class BindHelperAnction extends AnAction {

    private ClassModel _classModel;
    private Editor _editor;
    private String _content;
    private String _path;

    @Override
    public void actionPerformed(AnActionEvent e) {
        init(e);
        if (isLocateActivity()) {
            _path= ClassCreateHelper.getCurrentPath(e,_classModel.get_classFullName());
            try {
                ClassCreateHelper.createClass(_path,_classModel.get_className(),_classModel.get_classFullName(),ClassCreateHelper.VIEWMODEL);
               ClassCreateHelper.createClass(_path,_classModel.get_className(),_classModel.get_classFullName(),ClassCreateHelper.LISTENER);
               ClassCreateHelper.createClass(_path,_classModel.get_className(),_classModel.get_classFullName(),ClassCreateHelper.ENTITY);
               ClassCreateHelper.createClass(_path,_classModel.get_className(),_classModel.get_classFullName(),ClassCreateHelper.REQ);
               ClassCreateHelper.createClass(_path,_classModel.get_className(),_classModel.get_classFullName(),ClassCreateHelper.RSP);
              createActivity();
                ClassCreateHelper.createClassInCurrentPackage(_path,_classModel.get_className(),ClassCreateHelper.DATACENTERMANAGER);
                ClassCreateHelper.createClassInCurrentPackage(_path,_classModel.get_className(),ClassCreateHelper.NETMANAGER);
                refreshProject(e);
            } catch (IOException e1) {
                e1.printStackTrace();
                MessageUtil.showErrorMessage("创建文件失败","信息提示：");
            }
            finally {

            }
        }else {

        }
    }




    private void refreshProject(AnActionEvent e) {
        e.getProject().getBaseDir().refresh(false,true);
    }

    private boolean isLocateActivity(){
        String text = _editor.getDocument().getText();
        String[] strs = text.split(" ");
        boolean flag =false;
        for (String str : strs) {
            if (str.equals("class")) {
                flag = true;
            }
            if (str.contains("Activity")&&flag) {
                String className = str.substring(0, str.indexOf("Activity"));
                if (TextUtils.isEmpty(className)){
                    MessageUtil.showErrorMessage("类名要以Activity结尾","错误提示：");
                    return false;
                }
                //判断是否在activity类中。把类名保存下来
                _classModel.set_className(className);
                _classModel.set_classFullName(str);
                return true;
            }
        }
        MessageUtil.showErrorMessage("当前不在Activity类中不能使用","信息提示：");
        return false;
    }

    private void init(AnActionEvent e) {
        _editor = e.getData(PlatformDataKeys.EDITOR);
        _classModel = new ClassModel();
    }

    private void createActivity(){
        String text = _editor.getDocument().getText();
        int insertIndex = text.lastIndexOf("}")-1;
        WriteCommandAction.runWriteCommandAction(_editor.getProject(),
                new Runnable() {
                    @Override
                    public void run() {
                        _editor.getDocument().insertString(insertIndex,"        private Activity" + _classModel.get_className() + "Binding mBinding;\n" +
                                "        private " + _classModel.get_className() + "Listener mListener;\n" +
                                "        private " + _classModel.get_className() + "ViewModel mModel;\n" +
                                "\n" +
                                "        @Override\n" +
                                "        protected void bindView() {\n" +
                                "            mBinding = DataBindingUtil.setContentView(this, R.layout.activity_" + _classModel.get_className().toLowerCase() + ");\n" +
                                "        }\n" +
                                "\n" +
                                "        @Override\n" +
                                "        protected void init() {\n" +
                                "            mModel = new " + _classModel.get_className() + "ViewModel();\n" +
                                "            mListener = new " + _classModel.get_className() + "Listener(mBinding, mModel);\n" +
                                "            mBinding.setListener(mListener);\n" +
                                "            mBinding.setModel(mModel);\n" +
                                "        } ");
                    }
                });
    }
}
