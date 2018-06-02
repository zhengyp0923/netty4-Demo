package cn.zyp.netty.demo1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

public class DemoServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup bossGroup=new NioEventLoopGroup();
        NioEventLoopGroup workGroup=new NioEventLoopGroup();

       try{
           ServerBootstrap b=new ServerBootstrap();

           b.group(bossGroup,workGroup)
                   .channel(NioServerSocketChannel.class)
                   .option(ChannelOption.SO_KEEPALIVE,true)
                   .childHandler(new ChannelInitializer<SocketChannel>() {
                       @Override
                       protected void initChannel(SocketChannel ch) throws Exception {
                           ch.pipeline()
                                   /**
                                    *心跳检测
                                    *  如果Channel空闲6秒  服务端发送心跳检测包
                                    *  如果客户端收不到消息   关闭与客户端的Channel
                                    */
                                   .addLast(new IdleStateHandler(0,0,10, TimeUnit.SECONDS))
                                   .addLast(new ChannelInboundHandlerAdapter(){
                                       @Override
                                       public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                          if(evt instanceof IdleStateEvent){
                                              ctx.writeAndFlush(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("heartbeat", CharsetUtil.UTF_8)))
                                                      .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                                          }
                                       }
                                   });
                           ch.pipeline().addLast(new StringDecoder());
                           ch.pipeline().addLast(new StringEncoder());
                           ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                               @Override
                               protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
                                   System.out.println("接收到客户端消息  "+s);
                                   ctx.writeAndFlush("success "+s);
                               }
                               @Override
                               public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                 cause.printStackTrace();
                                 ctx.close();
                               }
                           });
                       }
                   });

           ChannelFuture channelFuture = b.bind(8080);


           channelFuture.addListener(new ChannelFutureListener() {
               @Override
               public void operationComplete(ChannelFuture channelFuture) throws Exception {
                   if(channelFuture.isSuccess()){
                       System.out.println("server bind 8080");
                   }else {
                       System.out.println("server bind fail");
                   }
               }
           });

           //阻塞等待channel关闭
           channelFuture.channel().closeFuture().sync();

       }finally {
           bossGroup.shutdownGracefully();
           workGroup.shutdownGracefully();
       }
    }
}
