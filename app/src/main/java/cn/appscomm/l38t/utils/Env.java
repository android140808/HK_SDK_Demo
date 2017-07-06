package cn.appscomm.l38t.utils;

import java.io.File;

/**
 * Created by weiliu on 2016/7/21.
 */
public class Env {
    public static boolean bIsMultiProc = IsMultiProcessor();
    private static boolean IsMultiProcessor(){
        File cpu = new File("/sys/devices/system/cpu/cpu1");
        if (cpu.isDirectory())
            return true;

        return false;
    }

}
