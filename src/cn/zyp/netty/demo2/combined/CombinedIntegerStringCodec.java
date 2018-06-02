package cn.zyp.netty.demo2.combined;

import cn.zyp.netty.demo2.decoder.IntegerToStringDecoder;
import cn.zyp.netty.demo2.encoder.StringToIntegerEncoder;
import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * 对于编解码器的结合
 */
public class CombinedIntegerStringCodec extends CombinedChannelDuplexHandler<IntegerToStringDecoder, StringToIntegerEncoder> {
    public CombinedIntegerStringCodec(){
        super(new IntegerToStringDecoder(),new StringToIntegerEncoder());
    }
}
