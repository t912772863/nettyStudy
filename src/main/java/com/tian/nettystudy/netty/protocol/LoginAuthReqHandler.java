package com.tian.nettystudy.netty.protocol;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 握手和安全认证类
 *
 *
 * Created by Administrator on 2018/7/26 0026.
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {
    /**
     * 当客户端跟服务端三次TCP握手成功之后, 由客户端构造握手请求消息发送给服务端, 由于采用
     * IP白名单认证机制, 因此, 不需要携带消息体, 消息体为空, 消息类型为"3: 握手请求消息"
     * 握手请求消息发送后, 按照协议规范, 服务端需要返回握手应答消息.
     *
     * @param ctx
     */
    public void channelActive(ChannelHandlerContext ctx){
        ctx.writeAndFlush(buildLoginReq());
    }

    /**
     * 对握手应答消息进行处理, 首先判断消息是否是握手应答消息, 如果不是, 直接透传给后面ChannelHandler处理,
     * 如果是握手应答消息, 则对应答结果进行判断, 如果非0, 说明认证失败, 关闭链路, 重新发起连接.
     *
     * @param ctx
     * @param msg
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        NettyMessage message = (NettyMessage)msg;
        // 如果是握手应答消息,需要判断是否认证成功,
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
            byte loginResult = (Byte)message.getBody();
            if(loginResult != (byte)0){
                // 握手失败, 关闭连接
                ctx.close();
            }else {
                System.out.println("Login is ok: "+message);
                ctx.fireChannelRead(msg);
            }

        }else {
            ctx.fireChannelRead(msg);
        }


    }

    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.fireExceptionCaught(cause);
    }


}
