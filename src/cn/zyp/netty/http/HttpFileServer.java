package cn.zyp.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * HTTP文件服务器
 */
public class HttpFileServer {
    private static final String DEFAULT_URL = "/";//src

    public void run(int port, String url) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //http消息请求解码器
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder())
                                    /**
                                     * http解码器会为每一个Http消息创建多个消息对象
                                     * HttpObjectAggregator 将多个消息转化为单一的FullHttpRequest  FullHttpResponse
                                     */
                                    .addLast("http-aggregator", new HttpObjectAggregator(65536))
                                    //http响应编码器
                                    .addLast("http-encoder", new HttpResponseEncoder())
                                    //支持异步发送大的码流，而不占用太多的内存
                                    .addLast("http-chunked", new ChunkedWriteHandler())
                                    .addLast("fileServerHandler", new HttpFileServerHandler(url));
                        }
                    });
            ChannelFuture f = b.bind("127.0.0.1", port).sync();
            System.out.println("HTTP文件服务器启动  " + port);

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception {
        new HttpFileServer().run(8080, DEFAULT_URL);
    }
}
