package com.hk.test.helper;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 创建class的类
 * Created by changfeng on 2016/11/21
 */
public class ClassCreateHelper {
    //上层目录
    public static final int VIEWMODEL = 0;
    public static final int LISTENER = 1;
    public static final int ENTITY = 2;
    public static final int REQ = 3;
    public static final int RSP = 4;
    //当前目录
    public static final int DATACENTERMANAGER = 5;
    public static final int NETMANAGER = 6;




    public static void createClassInCurrentPackage(String path,String className,int mode) throws IOException {
        String type = null;
        String content = "";
        switch (mode) {
            case DATACENTERMANAGER:
                type = "DataCenterManager";
                content = setDataCenterMangerContent(className,type);
                break;
            case NETMANAGER:
                type = "NetManager";
                content = setNetManagerContent(className,type);
                break;
        }
        File file = new File(path+className+type+".java");
        Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        BufferedWriter writer = new BufferedWriter(w);
        String packageName = getCurrentPackge(path);//为了共用下面方法。加2个斜杠
        writer.write("package " + packageName);
        writer.newLine();
        writer.newLine();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        writer.write("/**\n* Created by " + System.getProperty("user.name") + " on " + sdf.format(date) + "\n*/");
        writer.write(content);
        writer.flush();
        writer.close();

    }

    public static String getCurrentPackge(String path){
        String[] strings = path.split("/");
        StringBuilder packageName = new StringBuilder();
        int index = 0;
        int length = strings.length;
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].equals("com")
                    || strings[i].equals("org")
                    || strings.equals("cn")
                    || strings[i].equals("net")
                    || strings[i].equals("me")
                    || strings[i].equals("io")
                    || strings[i].equals("tech")
                    ) {
                index = i;
                break;
            }

        }
        for (int j = index; j < length; j++) {
            packageName.append(strings[j] + ((j==length-1)?";":"."));
        }
        return packageName.toString();

    }

    private static String setNetManagerContent(String className, String type) {
        String temp = "public class "+className+type+" {\n" +
                " \n" +
                "    public static Observable<"+className+"Rsp"+"> req"+className+"(NetworkCallback<"+className+"Rsp"+"> callBack, boolean isNeedCache) {\n" +
                "        "+className+"Req"+"Param param = new "+className+"Req"+"Param();\n" +
                "        //给参数对象赋值\n" +
                "\n" +
                "\t\n" +
                "        return HttpWork.getInstace(Global.getContext()).post(param, "+className+"Rsp"+".class, callBack, isNeedCache);\n" +
                "    }\n" +
                "}";

        return temp;
    }

    private static String setDataCenterMangerContent(String className, String type) {
        String temp = "public class "+className+type+" {\n" +
                "\n" +
                "    private volatile static "+className+type+" instance;\n" +
                "\n" +
                "    public static "+className+type+" getInstance() {\n" +
                "        if (instance == null) {\n" +
                "            synchronized ("+className+type+".class) {\n" +
                "                if (instance == null) {\n" +
                "                    instance = new "+className+type+"();\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return instance;\n" +
                "    }\n" +
                "\n" +
                "    public Observable<"+className+"Rsp"+"> req"+className+"(NetworkCallback<"+className+"Rsp"+"> callback) {\n" +
                "        return "+className+"NetManager.req"+className+"(callback, false)\n" +
                "                .doOnNext(new Action1<"+className+"Rsp"+">() {\n" +
                "                    @Override\n" +
                "                    public void call("+className+"Rsp"+" "+className.toLowerCase()+"Rsp"+") {\n" +
                "                        \n" +
                "                    }\n" +
                "                });\n" +
                "    }\n" +
                "}";

        return temp;
    }

    /**
     * 创建当前外层的包和类
     * @param path 当前
     * @param className
     * @param classFullName
     * @param mode
     * @throws IOException
     */
    public static void createClass(String path, String className, String classFullName, int mode) throws IOException {
        String type = null;
        if (mode == VIEWMODEL) {
            type = "ViewModel";
        } else if (mode == LISTENER) {
            type = "Listener";
        } else if (mode == ENTITY) {
            type = "Entity";
        } else if (mode == REQ) {
            type = "Req";
        } else if (mode == RSP) {
            type = "Rsp";
        }
        String dir = path + type.toLowerCase() + "/";
        path = dir + className + type + ".java";
        if (mode==REQ){
            path = dir + className + type +"Param"+ ".java";
        }

        File dirs = new File(dir);

        System.out.println("dirs = " + dir);
        File file = new File(path);
        if (!dirs.exists()) {
            dirs.mkdir();
        }
        file.createNewFile();

        Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        BufferedWriter writer = new BufferedWriter(w);

        String packageName = getPackageName(path);
        writer.write("package " + packageName + type.toLowerCase() + ";");
        writer.newLine();
        writer.newLine();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        writer.write("/**\n* Created by " + System.getProperty("user.name") + " on " + sdf.format(date) + "\n*/");

        writer.newLine();
        writer.newLine();
        if (mode == VIEWMODEL||mode==LISTENER){
        setContentViewAndModel(path, className, mode, type, writer);
        }else{
            if (mode == REQ){
                setReqContent(writer,className);
            }else if (mode==ENTITY){
                setEntityContent(writer,className);
            }else if (mode==RSP){
                setRspContent(writer,className);
            }
        }

        writer.flush();
        writer.close();
    }

    private static void setRspContent(BufferedWriter writer, String className) throws IOException {
        writer.write("public class "+className+"Rsp extends HXResponser {\n" +
                "    @Override\n" +
                "    public void parserResult(JSONObject dataStr) {\n" +
                "    }\n" +
                "}");
    }

    private static void setEntityContent(BufferedWriter writer, String className) throws IOException {
        writer.write("public class "+className+"Entity implements IEntity{\n" +
                "   \n" +
                "\n" +
                "\n" +
                "}");
    }

    private static void setContentViewAndModel(String path, String className, int mode, String type, BufferedWriter writer) throws IOException {
        writer.write("public class " + className + type + "{");
        writer.newLine();

        setContent(mode, writer, className, path);

        writer.newLine();
        writer.write("}");
    }

    private static void setContent(int mode, BufferedWriter writer, String className, String path) throws IOException {
        switch (mode) {
            case VIEWMODEL:
                setViewModelContent(writer,className);
                break;
            case LISTENER:
                setListenerContent(writer, className);
                break;
        }
    }

    private static void setViewModelContent(BufferedWriter writer, String className) throws IOException {
        //viewmodel一般为空，只有个类
    }

    private static void setReqContent(BufferedWriter writer, String className) throws IOException {
        writer.write("@URLBuilder.Path(host = XYConfig.API_HOST, url = ApiDefinition.URL_"+className.toUpperCase()+", builder = HXUrlBuilder.class)\n" +
                "public class "+className+"ReqParam implements ParamEntity {\n" +

                "}");

    }

    private static void setListenerContent(BufferedWriter writer, String className) throws IOException {
       writer.write("    private Activity"+className+"Binding mBinding;\n" +
               "    private "+className+"ViewModel mModel;\n" +
               "\n" +
               "    public "+className+"Listener(Activity"+className+"Binding mBinding, "+className+"ViewModel mModel) {\n" +
               "        this.mBinding = mBinding;\n" +
               "        this.mModel = mModel;\n" +
               "    }");
    }


    private static String getPackageName(String path) {
        String[] strings = path.split("/");
        StringBuilder packageName = new StringBuilder();
        int index = 0;
        int length = strings.length;
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].equals("com")
                    || strings[i].equals("org")
                    || strings.equals("cn")
                    || strings[i].equals("net")
                    || strings[i].equals("me")
                    || strings[i].equals("io")
                    || strings[i].equals("tech")
                    ) {
                index = i;
                break;
            }

        }
        for (int j = index; j < length - 2; j++) {
            packageName.append(strings[j] + ".");
        }
        return packageName.toString();
    }

    public static String getCurrentPath(AnActionEvent e, String classFullName) {

        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());

        String path = currentFile.getPath().replace(classFullName + ".java", "");
        return path;
    }



}

