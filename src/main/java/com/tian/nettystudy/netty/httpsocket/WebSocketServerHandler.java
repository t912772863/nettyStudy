package com.tian.nettystudy.netty.httpsocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;

/**
 * Created by Administrator on 2018/7/26 0026.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketServerHandshaker handshaker;

    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 传统的http接入. 第一次握手请求由HTTP协议承载, 所以它是一个http消息, 执行handleHttpRequest方法来处理WebSocket握手请求
        if(msg instanceof FullHttpRequest){
            handleHttpRequest(ctx, (FullHttpRequest)msg);
        }else {
            // webSocket接入
            handlerWebSocketFrame(ctx, (WebSocketFrame)msg);

        }


    }

    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 如果http解码失败, 返回http异常. 如果请求消息头中没有Upgrade属性, 或者其值不是websoceket, 则返回400异常
        if(!req.getDecoderResult().isSuccess() || !"websocket".equalsIgnoreCase(req.headers().get("Upgrade"))){
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        /*
          构造握手响应返回, 本机测试.
          握手请求装箱单校验通过以后, 开始构造握手工厂, 创建握手处理类WebSocketServerHandshaker, 通过它构造握手响应消息
          返回给客户端, 同时将WebSocket相关的编码和解码类动态添加到ChannelPipeline中, 用于WebSocket的编解码.

           */
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if(handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        }else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否是关闭链路的指令
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否是ping消息
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本示例仅支持文本消息, 不支持二进制消息
        if(!(frame instanceof  TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame type not supported", frame.getClass().getName()));
        }
        // 返回应答消息
        String request = ((TextWebSocketFrame)frame).text();
        System.out.println("receive message: "+ request);
        ctx.channel().write(new TextWebSocketFrame(request + ", welcome to use webSocket service. now is: "+ new Date().toString()));

    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if(res.getStatus().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            setContentLength(res, res.content().readableBytes());
        }

        // 如果是非keep-alive, 则关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if(!isKeepAlive(req) || res.getStatus().code() != 200){
            f.addListener(ChannelFutureListener.CLOSE);
        }



    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

}
