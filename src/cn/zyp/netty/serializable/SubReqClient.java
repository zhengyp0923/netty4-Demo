package cn.zyp.netty.serializable;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


public class SubReqClient {

	public void connect(String host, int port) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							//解码器
							ch.pipeline().addLast(new ObjectDecoder(1024,
									ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
							//编码器
							ch.pipeline().addLast(new ObjectEncoder());
							ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
								@Override
								public void channelActive(ChannelHandlerContext ctx) throws Exception {
									System.out.println("向服务端发送请求....");
									SubscribeReq req = new SubscribeReq(12, "user", "34", "12", "bj");
									ctx.writeAndFlush(req);
								}

								@Override
								public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

									System.out.println("接收服务端数据 "+msg);
								}
							});
						}
					});

			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();

		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		new SubReqClient().connect("127.0.0.1", 8080);
	}

}
