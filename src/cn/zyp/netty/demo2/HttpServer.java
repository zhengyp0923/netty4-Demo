package cn.zyp.netty.demo2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

/**
 * 接收Http请求
 */
public class HttpServer {
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
                                     //Http编解码器
                                     .addLast(new HttpServerCodec())
                                     .addLast(new HttpClientCodec())
                                     //Http压缩与解压
                                     .addLast(new HttpContentCompressor())
                                     .addLast(new HttpContentDecompressor())
                                     //512KB
                                     .addLast(new HttpObjectAggregator(512*1024))
                                     .addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                                         @Override
                                         protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
                                             System.out.println("FullHttpRequest "+fullHttpRequest);
                                             System.out.println("-----------------------------");
                                             ByteBuf byteBuf = fullHttpRequest.content();
                                             String s = byteBuf.toString();
                                             System.out.println("content: "+s);
                                             System.out.println("-----------------------------");
                                             channelHandlerContext.writeAndFlush("success");
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
