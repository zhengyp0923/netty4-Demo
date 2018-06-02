package cn.zyp.netty.demo;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 测试多个ChannelHandler的调用关系
 */
public class SubReqServer {

	public void bind(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).handler(new LoggingHandler(LogLevel.INFO)).
			channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {

							ch.pipeline().addLast(new ObjectDecoder(
									ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));

							ch.pipeline().addLast(new ObjectEncoder());
							ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

								@Override
								public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
									System.out.println("handler1..");

									SubscribeReq req = (SubscribeReq) msg;
									System.out.println("handler1 接收客户端的请求 "+req);


									SubscribeResp resp = new SubscribeResp();
									resp.setSunReqID(12);
									resp.setDesc("113");
								     resp.setRespCode(200);
									System.out.println("handler1 响应数据...");
									ctx.writeAndFlush(resp);

									/**
									 * 调用下一个ChannelHandler
									 */
									ctx.fireChannelRead(msg+"sss");
								}

								@Override
								public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
										throws Exception {
									cause.printStackTrace();
									ctx.close();
								}
							}).addLast(new ChannelInboundHandlerAdapter(){
								@Override
								public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
									System.out.println("handler2接收数据 "+msg);

									ctx.writeAndFlush("success");
								}

								@Override
								public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
									super.exceptionCaught(ctx, cause);
								}
							});



						}

					});

			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();

		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();

		}
	}

	public static void main(String[] args) throws Exception {
		new SubReqServer().bind(8080);
	}
}
