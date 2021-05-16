package com.hugmount.helloboot.util;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

/**
 * @Author： Li HuiMing
 * @Date: 2021/5/14
 */

@Slf4j
public class SFTPUtil {

    private static ChannelSftp channelSftp;

    private static Session session;

    public static void init(String ip, int port, String username, String password) {
        init(ip, port, username, password, null);
    }

    public static void init(String ip, int port, String username, String password, String privateKey) {
        try {
            JSch jsch = new JSch();
            if (privateKey != null) {
                jsch.addIdentity(privateKey); // 设置私钥
            }
            session = jsch.getSession(username, ip, port);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            log.info("SFTP初始化成功");
        } catch (Exception e) {
            log.error("连接SFTP异常");
        }
    }


    public static void uploadFile(String path, String fileName, InputStream inputStream) {
        try {
            channelSftp.cd(path);
            channelSftp.put(inputStream, fileName);
            log.info("上传SFTP文件成功");
        } catch (SftpException e) {
            log.error("上传SFTP文件异常", e);
        }
    }


    public static InputStream downloadFile(String ftpPath, String fileName) {
        try {
            channelSftp.cd(ftpPath);
            InputStream inputStream = channelSftp.get(fileName);
            log.info("下载SFTP文件成功");
            return inputStream;
        } catch (SftpException e) {
            log.error("下载SFTP文件异常", e);
        }
        return null;
    }

}
