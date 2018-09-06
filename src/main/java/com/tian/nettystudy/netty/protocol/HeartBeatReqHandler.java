package com.tian.nettystudy.netty.protocol;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * 握手成功之后, 由客户端主动发送心跳消息, 服务端接收到心跳消息之后, 返回心跳应答消息,
 * 由于心跳消息的目的是为了检测链路的可用性, 所以不用带消息体.
 *
 * Created by Administrator on 2018/7/26 0026.
 */
public class HeartBeatReqHandler extends ChannelHandlerAdapter {
    private volatile ScheduledFuture<?> heartBeat;

    public void channelRead(ChannelHandlerContext ctx, Object msg){
        NettyMessage message = (NettyMessage) msg;
        /* 握手成功之后, 握手请求Handler会继续将握手消息向下透传, HeartBeatReqHandler接收到之后对消息进行判断,如果是握手成功
        消息, 则启动无限循环定时器用于定期发送心跳消息. 由于NioEventLoop是一个Schedule, 因此它支持定时器的执行,

         */
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);

        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()){
            System.out.println("Client receive server heart beat message: -----> "+message);
        }else {
            ctx.fireChannelRead(msg);
        }


    }


    private class HeartBeatTask implements Runnable {
        private final ChannelHandlerContext ctx;
        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run() {
            NettyMessage heatBeat = buildHeatBeat();
            System.out.println("Client send heart beat message to server: ------>"+heatBeat);
            ctx.writeAndFlush(heatBeat);

        }

        private NettyMessage buildHeatBeat() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEARTBEAT_REQ.value());
            message.setHeader(header);
            return message;
        }
    }



    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        if(heartBeat != null){
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}
