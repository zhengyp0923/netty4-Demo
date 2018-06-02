package cn.zyp.netty.demo1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DemoClient {
    public static void main(String[] args) throws InterruptedException, IOException {
        NioEventLoopGroup group=new NioEventLoopGroup();
        Bootstrap b=new Bootstrap();

        try {
            b.group(group)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new SimpleChannelInboundHandler<String>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
                                            System.out.println("接收服务端消息  "+s);
                                        }

                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                            cause.printStackTrace();
                                            ctx.close();
                                        }
                                    });
                        }
                    });

            ChannelFuture channelFuture = b.connect("127.0.0.1", 8080).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        System.out.println("connection 127.0.0.1 8080");
                    }else {
                        System.out.println("connection fail");
                    }
                }
            });

            Channel channel = channelFuture.channel();

            BufferedReader in=new BufferedReader(new InputStreamReader(System.in));

            while (true){
                channel.writeAndFlush(in.readLine()+"\r\n");
            }
        }finally {
            group.shutdownGracefully();
        }
    }
}
