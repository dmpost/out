package com.webstunning.out;

import android.os.Build;
import android.support.compat.BuildConfig;
import android.util.Log;



final public class Out {

    private static final String THIS_CLASS_NAME = "Out";
    private static final String LOGCAT_FILTER_TAG = "Out";
    private static final String SPACE = " ";
    private static final String ARROW = SPACE+'\u2190'+SPACE;
    private static final char DOT = '.';
    private static final char SEP = '\u2022';
    private static final String TABS = "\t\t\t\t\t";

    private static boolean isRoboUnitTest() {
        return "robolectric".equals(Build.FINGERPRINT);
    }

    private static void output(String msg, boolean isError){
        if(isRoboUnitTest()){
            System.out.println(msg);
        }else{
            if(isError)Log.e(LOGCAT_FILTER_TAG, msg);
            else Log.d(LOGCAT_FILTER_TAG, msg);
        }
    }

    public static void d(Object args) {
        StackTraceElement[] traceEl = Thread.currentThread().getStackTrace();

        String methodsList = getMethodsList(traceEl, findStartInt(traceEl));

        output(args.toString() + TABS+SEP+SPACE + methodsList, false);
    }

    public static void e(Object args) {
        StackTraceElement[] traceEl = Thread.currentThread().getStackTrace();

        String methodsList = getMethodsList(traceEl, findStartInt(traceEl));

        output(args.toString() + TABS+SEP+SPACE + methodsList, true);
    }

    private static int findStartInt(StackTraceElement[] traceEl){
        boolean foundOut = false;
        for (int i = 0; i < traceEl.length; i++) {
            StackTraceElement e = traceEl[i];
            String className = getShortClassName(e.getClassName());
            if(!foundOut && className.equals(THIS_CLASS_NAME))foundOut = true;
            if(foundOut && !className.equals(THIS_CLASS_NAME))return i;
        }
        return 0;
    }


    private static String getMethodsList(StackTraceElement[] traceEl, int start) {
        String methodsList = "";
        StackTraceElement e = traceEl[start];
        String className = getShortClassName(e.getClassName());
        methodsList += className + DOT + e.getMethodName() + '(' + e.getFileName() + ':' + e.getLineNumber() + ')';

        start++;
        String lastClassName = className;
        for (int i = start; i < traceEl.length; i++) {
            e = traceEl[i];
           if (e.getMethodName().startsWith("access$dispatch")){
               i++;
               continue;
           }

            if(e.getClassName().matches("^(android\\.|com\\.android\\.|java\\.lang\\.).*")){
                break;
            }
            className = getShortClassName(e.getClassName());
            if(className.equals(lastClassName)){
                className = "";
            } else {
                lastClassName = className;
                className += DOT;
            }
            methodsList += ARROW + className + e.getMethodName()+"()";
        }


        return methodsList;
    }

    private static String getShortClassName(String fullClassName) {
        String className = fullClassName.replace("$override","");
        //Log.d(LOGCAT_FILTER_TAG, fullClassName);
        className = className.substring(className.lastIndexOf(DOT) + 1);
        return className;
    }

}
