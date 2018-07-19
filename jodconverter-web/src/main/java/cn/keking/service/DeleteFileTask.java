package cn.keking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 文件清除任务
 */
@Component
public class DeleteFileTask {

    @Value("${file.dir}")
    private String fileDir;

    @Scheduled(cron = "0 0 0,12 * * ? ")
    public void deleteAllFile() {

        //定时清空所有文件夹下的所有文件
        File file = new File(fileDir);
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            if (file.isDirectory()) {
                //删除这个文件夹下面的所有文件
                for (File f : files) {
                    if (f.isDirectory()) {
                        //如果子文件是文件夹，执行递归操作
                        delDir(f);
                    } else {
                        f.delete();
                    }
                }
            }
//            System.out.println("定时任务执行，文件夹不为空");
        }

//        System.out.println("定时任务" + fileDir);

    }

    /**
     * 递归删除方法
     *
     * @param file
     */
    public static void delDir(File file) {
        if (file.isDirectory()) {
            //获取子目录
            File[] subFiles = file.listFiles();
            for (File f : subFiles) {
                delDir(f);
            }
        }
        //删除空目录或者文件
        file.delete();
    }

}
