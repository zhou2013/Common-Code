/*
 * @(#) SocketProxyChecker.java 2016年2月2日
 * 
 * Copyright 2016 NetEase.com, Inc. All rights reserved.
 */
package zzhao.code.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 检查对应socket协议
 * @author zzhao
 * @version 2016年2月2日
 */
public class SocketProxyChecker {

    private final static byte[] socket5InitBytes = {0x05, 0x03, 0x00, 0x01, 0x02}; // 支持3种认证方式
    private final static byte[] socket4InitBytes = {0x04, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    private final static int CONNECT_TIMEOUT = 3000;
    private final static int READ_TIMEOUT = 3000;

    public static void main(String[] args) {
        System.out.println("It is proxy: " + checkSocketProxy("120.234.6.50", 1080));
    }

    public static String checkSocketProxy(String host, int port) {
        if (checkSocket5Proxy(host, port)) {
            return "Socket5";
        }

        if (checkSocket4Proxy(host, port)) {
            return "Socket4";
        }

        return null;
    }

    private static boolean checkSocket5Proxy(String host, int port) {
        boolean ret = false;
        Socket socket = null;
        try {
            socket = new Socket();
            socket.setSoTimeout(READ_TIMEOUT);
            socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT);
            if (socket.isConnected()) {
                OutputStream os = socket.getOutputStream();
                os.write(socket5InitBytes);
                os.flush();
                InputStream is = socket.getInputStream();
                byte[] tmp = new byte[256];
                int count = is.read(tmp, 0, 10);
                if (count == 2 && tmp[0] == 0x05) {
                    ret = true;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        return ret;
    }

    private static boolean checkSocket4Proxy(String host, int port) {
        boolean ret = false;
        Socket socket = null;
        try {
            socket = new Socket();
            socket.setSoTimeout(READ_TIMEOUT);
            socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT);
            if (socket.isConnected()) {
                OutputStream os = socket.getOutputStream();
                os.write(socket4InitBytes);
                os.flush();
                InputStream is = socket.getInputStream();
                byte[] tmp = new byte[256];
                int count = is.read(tmp, 0, 10);
                if (count == 8 && tmp[0] == 0x00 && tmp[1] >= 0x5A && tmp[1] <= 0x5D) {
                    ret = true;
                }
            }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        return ret;
    }
}
