package com.tian.nettystudy.netty.protocol;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2018/7/26 0026.
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter {
    /**
     * 定义重复登录保护
     */
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();
    /**
     * 定义IP白名单
     */
    private String[] whiteList = {"127.0.0.1", "192.168.30.123"};

    public void channelRead(ChannelHandlerContext ctx, Object msg){
        NettyMessage message = (NettyMessage)msg;
        /*
        接入认证, 首先根据客户端源地址, 进行重复登录判断, 如果客户端已经登录成功, 则拒绝重复登录.
        以防止由于客户端重复登录造成句柄泄漏.之后通过ChannelHandlerContext的Channel接口获取客户
        端的InetSocketAddress地址, 从中取得发送方的源地址信息, 通过源地址进行白名单校验, 校验通过
        握手成功, 否则握手失败. 最后创建握手应答消息返回给客户端.

        当发生异常关闭链路的时候, 需要将客户端的信息从登录注册表中去注册, 以保证后续客户端可以重连
        成功.

         */
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()){
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            // 重复登录, 拒绝
            if(nodeCheck.containsKey(nodeIndex)){
                loginResp = buildResponse((byte)-1);
            }else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for(String s : whiteList){
                    if(s.equals(ip)){
                        isOK = true;
                        break;
                    }
                }
                loginResp = isOK?buildResponse((byte)0):buildResponse((byte)-1);
                if(isOK){
                    nodeCheck.put(nodeIndex, true);
                }

            }
            System.out.println("The login response is : "+loginResp+" body ["+loginResp.getBody()+" ]");
            ctx.writeAndFlush(loginResp);
        }else {
            ctx.fireChannelRead(msg);
        }



    }

    private NettyMessage buildResponse(byte result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(result +"");
        return message;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }



}
