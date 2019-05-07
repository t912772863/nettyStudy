package com.tian.nettystudy.netty.shangxuetang.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by tianxiong on 2019/5/6.
 */
public class GzipUtils {
    /**
     * 解压缩
     * @param source 需要解析的数据
     * @return 解析后的数据
     * @throws IOException
     */
    public static byte[] unzip(byte[] source) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(source);
        GZIPInputStream zipIn = new GZIPInputStream(in);
        byte[] temp = new byte[256];
        int length = 0;
        while ((length = zipIn.read(temp, 0, temp.length))!= -1){
            out.write(temp, 0, length);
        }
        byte[] target = out.toByteArray();
        zipIn.close();
        out.close();
        return target;

    }

    /**
     * 压缩
     * @param source
     * @return
     * @throws IOException
     */
    public static byte[] zip(byte[] source) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream zipOut = new GZIPOutputStream(out);
        zipOut.write(source);
        zipOut.finish();
        byte[] target = out.toByteArray();
        zipOut.close();
        return target;
    }

}
