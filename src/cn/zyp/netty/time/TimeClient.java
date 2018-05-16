package cn.zyp.netty.time;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;

public class TimeClient {
	public static void main(String[] args) throws Exception {
		String host = "localhost";
		int port = 8080;

		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap(); 
			b.group(workerGroup); 
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
						/**
						 * 建立tcp连接时 调用
						 * @param ctx
						 * @throws Exception
						 */
						@Override
						public void channelActive(ChannelHandlerContext ctx) throws Exception {
							super.channelActive(ctx);
						}
						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							System.out.println("read channel");
							ByteBuf m = (ByteBuf) msg;
							try {
								long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
								System.out.println(new Date(currentTimeMillis));
								ctx.close();
							} finally {
								m.release();
							}
						}

						@Override
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

							cause.printStackTrace();
							ctx.close();
						}

					});
				}
			});
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
		}

	}

}
