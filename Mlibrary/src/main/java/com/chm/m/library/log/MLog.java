package com.chm.m.library.log;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * @Desc : log打印类
 * @Author : chenhongmou
 * @Time : 2022/04/19 22:12
 */
public class MLog {

    private static final String M_LOG_PACKAGE;

    static{
        //获取包名
        String className = MLog.class.getName();
        //获取包名前缀
        M_LOG_PACKAGE = className.substring(0,className.lastIndexOf('.')+1);
    }

    public static void v(Object... contents){
        log(MLogType.V,contents);
    }
    public static void vt(String tag,Object... contents){
        log(MLogType.V,tag,contents);
    }


    public static void d(Object... contents){
        log(MLogType.D,contents);
    }
    public static void dt(String tag,Object... contents){
        log(MLogType.D,tag,contents);
    }


    public static void i(Object... contents){
        log(MLogType.I,contents);
    }
    public static void it(String tag,Object... contents){
        log(MLogType.I,tag,contents);
    }


    public static void w(Object... contents){
        log(MLogType.W,contents);
    }
    public static void wt(String tag,Object... contents){
        log(MLogType.W,tag,contents);
    }


    public static void e(Object... contents){
        log(MLogType.E,contents);
    }
    public static void et(String tag,Object... contents){
        log(MLogType.E,tag,contents);
    }


    public static void a(Object... contents){
        log(MLogType.A,contents);
    }
    public static void at(String tag,Object... contents){
        log(MLogType.A,tag,contents);
    }

    //没TAG就用全局TAG
    public static void log(@MLogType.TYPE int type, Object... contents){
        log(type,MLogManager.getInstance().getConfig().getGlobalTag(),contents);
    }

    public static void log(@MLogType.TYPE int type,@NonNull String tag, Object... contents){
        log(MLogManager.getInstance().getConfig(),type,tag,contents);
    }

    public static void log(@NonNull MLogConfig config,@MLogType.TYPE int type,@NonNull String tag, Object... contents){
        if (!config.enable()){
            return;
        }

        StringBuilder sb = new StringBuilder();
        //判断是否要添加线程
        if (config.includeTread()){
            String threadInfo = MLogConfig.M_THREAD_FORMATTER.format(Thread.currentThread());
            sb.append(threadInfo).append("\n");
        }

        //判断是否添加堆栈信息
        if (config.stackTreceDepth() > 0){
            String stackTreace = MLogConfig.M_STACK_TRACE_FORMATTER.format(MStackTraceUtil.getCroppedRealStackTrack(new Throwable().getStackTrace(),M_LOG_PACKAGE, config.stackTreceDepth()));
            sb.append(stackTreace).append("\n");
        }

        String body = parseBody(contents,config);
        sb.append(body);

        //调用打印器打印
        List<MLogPrinter>printers = config.printers() != null ? Arrays.asList(config.printers()) : MLogManager.getInstance().getPrinters();
        if (printers==null){
            return ;
        }
        for (MLogPrinter printer:printers){
            printer.print(config,type,tag,sb.toString());
        }
//        Log.println(type,tag,body);

    }

    private static String parseBody(@NonNull Object[] contents,@NonNull MLogConfig config){

        if (config.injectJsonParser()!= null){
            return config.injectJsonParser().toJsonn(contents);
        }

        StringBuffer sb = new StringBuffer();
        for(Object o:contents){
            sb.append(o.toString()).append(";");
        }
        //删除最后一个分号
        if (sb.length()>0){
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }



}
