package com.nexless.devicecollect.util;

import java.io.File;
import java.io.IOException;

/**
 * @date: 2019/4/11
 * @author: su qinglin
 * @description:
 */
public class FileHelper {

    public static File resetFile(String name){
        String sdcardPath = System.getenv("EXTERNAL_STORAGE");      //获得sd卡路径
        String dir = sdcardPath + "/nexless/";                    //图片保存的文件夹名
        File file = new File(dir);                                 //已File来构建
        if (!file.exists()) {                                     //如果不存在  就mkdirs()创建此文件夹
            file.mkdirs();
        }
        File mFile = new File(dir + name);                        //将要保存的图片文件
        if (mFile.exists()) {
            mFile.delete();
        }else {
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mFile;
    }
}
