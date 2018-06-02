package cn.zyp.netty.demo2.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 编码器  byte-->int
 */
public class ByteToIntegerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //int 占4个字节
        if(byteBuf.readableBytes()>=4){
            list.add(byteBuf.readInt());
        }
    }
}
