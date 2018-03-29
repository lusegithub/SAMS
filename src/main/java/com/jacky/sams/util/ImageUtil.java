package com.jacky.sams.util;

import com.jacky.sams.entity.SysUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

public class ImageUtil {

    /**
     * 保存文件，直接以multipartFile形式
     * @param multipartFile
     * @param path 文件保存绝对路径
     * @return 返回文件名
     * @throws IOException
     */
    public static String saveImg(MultipartFile multipartFile, String path) throws IOException {
//        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        path+=user.getUsername();
//        File file = new File(path);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
//        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path + File.separator + fileName));
//        byte[] bs = new byte[1024];
//        int len;
//        while ((len = fileInputStream.read(bs)) != -1) {
//            bos.write(bs, 0, len);
//        }
//        bos.flush();
//        bos.close();

        SysUser user= (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        path+=user.getUsername();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
        resizeImage(multipartFile.getInputStream(),new File(path + File.separator + fileName),200,180);
        return File.separator +user.getUsername()+File.separator+ fileName;
    }

    public static void resizeImage(InputStream in,File output,
                                   int width, int height) throws IOException {

        Image srcImg = ImageIO.read(in);
        BufferedImage buffImg;
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffImg.getGraphics().drawImage(
                srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,
                0, null);

        ImageIO.write(buffImg, "JPEG", output);

    }
}
