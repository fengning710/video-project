package com.example.videoapp.testDemo.io;

import java.io.*;

public class FileCopyDemo {
    public static void main(String[] args) {
        //源文件准备
        File sourceFile = new File("D:\\document\\JiJiDownload\\小灰毛不许摇了！ - 1.小灰毛不许摇了！(Av115393540654529,P1).mp4");
        //复制后的文件
        File afterCopyFile = new File("D:\\study\\Java\\video-project\\test-resources\\copy.mp4");

        //读/写文件(使用try-with-resources语法自动关闭流)
        try(
                //输入输出流
                InputStream in = new FileInputStream(sourceFile);
                OutputStream out = new FileOutputStream(afterCopyFile);

                //输入输出缓冲流
                BufferedInputStream bis = new BufferedInputStream(in);
                BufferedOutputStream bos = new BufferedOutputStream(out)
                ){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            System.out.println("文件复制完毕");
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("复制失败");
        }
    }
}
