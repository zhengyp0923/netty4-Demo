package cn.zyp.netty.demo2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.FileInputStream;

/**
 * 文件的零拷贝
 */
public class FileServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workGroup=new NioEventLoopGroup();

        try {
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {

                                        }
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                           String file="";
                                            FileInputStream in=new FileInputStream(file);
                                            FileRegion fileRegion=new DefaultFileRegion(in.getChannel(),0,file.length());

                                            ctx.writeAndFlush(fileRegion).addListener(new ChannelFutureListener() {
                                                @Override
                                                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                                    if(!channelFuture.isSuccess()){
                                                        throw new RuntimeException(channelFuture.cause());
                                                    }
                                                }
                                            });
                                        }
                                    });
                        }
                    });

            ChannelFuture channelFuture = b.bind(8080).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()){
                        System.out.println("server bind 8080");
                    }else {
                        System.out.println("server  bind fail");
                    }
                }
            });
            channelFuture.channel().closeFuture().sync();
        }finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
