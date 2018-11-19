package com.f4w.weapp;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.open.api.impl.WxOpenInMemoryConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenInRedisConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenMessageRouter;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

@Component
@Slf4j
@EnableConfigurationProperties({WeiXinOpenConfig.class})
public class WxOpenService extends WxOpenServiceImpl {
    @Resource
    private WeiXinOpenConfig weiXinOpenConfig;
    @Resource
    private RedisProperties redisProperies;
    private static JedisPool pool;

    @PostConstruct
    public void init() {
        WxOpenInRedisConfigStorage wxOpenInMemoryConfigStorage = new WxOpenInRedisConfigStorage(getJedisPool());
        wxOpenInMemoryConfigStorage.setComponentAppId(weiXinOpenConfig.getComponentAppId());
        wxOpenInMemoryConfigStorage.setComponentAppSecret(weiXinOpenConfig.getComponentSecret());
        wxOpenInMemoryConfigStorage.setComponentToken(weiXinOpenConfig.getComponentToken());
        wxOpenInMemoryConfigStorage.setComponentAesKey(weiXinOpenConfig.getComponentAesKey());
        setWxOpenConfigStorage(wxOpenInMemoryConfigStorage);
    }

    @Bean
    public WxOpenMessageRouter wxOpenMessageRouter() {
        WxOpenMessageRouter wxOpenMessageRouter = new WxOpenMessageRouter(this);
        wxOpenMessageRouter.rule().handler((wxMpXmlMessage, map, wxMpService, wxSessionManager) -> {
            log.info("接收到 {} 公众号请求消息，内容：{}", wxMpService.getWxMpConfigStorage().getAppId(), wxMpXmlMessage);
            return null;
        }).next();
        return wxOpenMessageRouter;
    }

    private JedisPool getJedisPool() {
        if (pool == null) {
            pool = new JedisPool(new GenericObjectPoolConfig(), redisProperies.getHost(), redisProperies.getPort(), 2000, redisProperies.getPassword());
        }
        return pool;
    }

    public static void main(String[] args) throws Exception {
//        String str = "https://images.fast4ward.cn/tmp_b896e155860be1a41d32fbb5317a4030.jpg," +
//                "https://images.fast4ward.cn/tmp_5d2fa1643e75a80333cebb0cff71e08b.jpg," +
//                "https://images.fast4ward.cn/tmp_dd7061c8a40adbed33b0c35f760177ad.jpg," +
//                "https://images.fast4ward.cn/tmp_aedb57c2708e821b336d21230b305522.jpg," +
//                "https://images.fast4ward.cn/tmp_9c27e8b29f56082d67d93024a0f4b681f292a58d02c66cb3.jpg," +
//                "https://images.fast4ward.cn/tmp_f6ea60b23ec204ad5c4abbe4f8638962.jpg," +
//                "https://images.fast4ward.cn/tmp_6c53fb5e0fabb0793acbf4375af00e78.jpg," +
//                "https://images.fast4ward.cn/tmp_7365d103c7ebdbf2fb1b52cc8b1bb79b.jpg," +
//                "https://images.fast4ward.cn/tmp_a74bb25c68ba2dae93a20bcc3265741c.jpg," +
//                "https://images.fast4ward.cn/tmp_9ad6b8f2e6a2023e72a482a69ff27cee.jpg," +
//                "https://images.fast4ward.cn/tmp_12b4d73f2e3efad7bdfca219c05f9dec.jpg," +
//                "https://images.fast4ward.cn/tmp_8f2f1b7eaa51fdc6b2250ff0f496b9f9b6fb263a38a5525c.jpg," +
//                "https://images.fast4ward.cn/tmp_928ebde93e3f31cf71b3ed21b8dbe1993b4301475f2497fb.jpg," +
//                "https://images.fast4ward.cn/tmp_7aa70a22bfbdb94b89ad6c2741c87c15.jpg," +
//                "https://images.fast4ward.cn/tmp_c64e7fa80e545cfd08c9918ef734abd9.jpg," +
//                "https://images.fast4ward.cn/tmp_4e3f1a2f609281ab9d5155b1f30113c7.jpg," +
//                "https://images.fast4ward.cn/tmp_db03475a9fb23e31458e62d49d96fcad.jpg," +
//                "https://images.fast4ward.cn/tmp_dfb58c5fee07329ffc256d45afa8ef22.jpg," +
//                "https://images.fast4ward.cn/tmp_96bec7229f7d41c620647e6b4667e3b2.jpg," +
//                "https://images.fast4ward.cn/tmp_f1e4b22d9d74e0ae0f4bdbc98ef03ebc.jpg," +
//                "https://images.fast4ward.cn/tmp_2220f4a349bfc1bfd4337f726e006184.jpg," +
//                "https://images.fast4ward.cn/tmp_f81c893116ca154aebc25ecc012d3199.jpg," +
//                "https://images.fast4ward.cn/tmp_45568ff294ee2079fd20460f2b7823f3d19bcc3c06443404.jpg," +
//                "https://images.fast4ward.cn/tmp_0f486276bb2218a5947c2e0e3338a5f3.jpg," +
//                "https://images.fast4ward.cn/tmp_83bcdc6a63929d45437bfcf494339eb8.jpg," +
//                "https://images.fast4ward.cn/tmp_7d50404e02af6c69279506777bc5da96.jpg," +
//                "https://images.fast4ward.cn/tmp_0761158fd9a5be7bd7cf200b99b9345c.jpg," +
//                "https://images.fast4ward.cn/tmp_1050060b180932dbfce5d3de6350cfa2.jpg," +
//                "https://images.fast4ward.cn/tmp_bf6a11a96cd51940d90e50741aea2d5c0205d449501e38ad.jpg," +
//                "https://images.fast4ward.cn/tmp_f518da75c28ccdff6d8379df1bf76c54.jpg," +
//                "https://images.fast4ward.cn/tmp_8c598e3d38ed0c7b62507e2246530e30.jpg," +
//                "https://images.fast4ward.cn/tmp_de93e15e0ff7afa5700eb5e600ae7264.jpg," +
//                "https://images.fast4ward.cn/tmp_0b94dee7422d5230c44e7e669301b83afcaeaab1c090734b.jpg," +
//                "https://images.fast4ward.cn/tmp_725dd02e348d326cac566144f1af8b93.jpg," +
//                "https://images.fast4ward.cn/tmp_69cfddce2687b6df0ab36e725de4e074.jpg," +
//                "https://images.fast4ward.cn/tmp_2678b451f1ea2222465ec5ea027a4f84.jpg," +
//                "https://images.fast4ward.cn/tmp_adaf92410b8406c7f2a5f6365d18975a.jpg," +
//                "https://images.fast4ward.cn/tmp_14444ddca3db45e477d3b66ead93045c.jpg," +
//                "https://images.fast4ward.cn/tmp_7d06c8ba1031690eb73cf7e62f3410d9.jpg," +
//                "https://images.fast4ward.cn/tmp_e15d5fc229671cc274f219f62e8f274b.jpg," +
//                "https://images.fast4ward.cn/tmp_ed59a4236beb60aaacc6721dc6104114.jpg," +
//                "https://images.fast4ward.cn/tmp_af91ae07b8b0c11d3d12ae033bb5605d25cb525b78a73295.jpg," +
//                "https://images.fast4ward.cn/tmp_1bb4693efad75a8fc131b23e69083fad.jpg," +
//                "https://images.fast4ward.cn/tmp_0744496f99c2064f775e55487797b340.jpg," +
//                "https://images.fast4ward.cn/tmp_4f1983dd4e11749b20eed2020a36cb4d.jpg," +
//                "https://images.fast4ward.cn/tmp_6781bad93076af27144663f25b54f786.jpg," +
//                "https://images.fast4ward.cn/tmp_b676ee814fce87b6fd53ee653189feb4.jpg," +
//                "https://images.fast4ward.cn/tmp_51f6a9b0b7f6e2fd30df99ea4abaee8053badced69193499.jpg," +
//                "https://images.fast4ward.cn/tmp_612eaae37dea8533511332fefc266817.jpg," +
//                "https://images.fast4ward.cn/tmp_5bc478e5a651fc5e88a771a7a7ee1c5b.jpg," +
//                "https://images.fast4ward.cn/tmp_277f952e9e853c05b400c7ae9ebd5164.jpg," +
//                "https://images.fast4ward.cn/tmp_0feef908dfe8113d15e763c0ed77d9db.jpg," +
//                "https://images.fast4ward.cn/tmp_ce11334f33e41fc68f09f9809bf2bfab.jpg," +
//                "https://images.fast4ward.cn/tmp_584721a450f8702a028392fe2dee1f37.jpg," +
//                "https://images.fast4ward.cn/tmp_0618e9e5359c22ce991ae5f3b7c58907.jpg," +
//                "https://images.fast4ward.cn/tmp_4175b54ac6bbff03abc199ce138afaef.jpg," +
//                "https://images.fast4ward.cn/tmp_5ea8c8bd14837c8a470461ccccbcc7bc.jpg," +
//                "https://images.fast4ward.cn/tmp_5ce98b900be9f7ab25bb3c3838c6ac30.jpg," +
//                "https://images.fast4ward.cn/tmp_fdf01cf7fddfac2eb1aa93ae3d46320a.jpg," +
//                "https://images.fast4ward.cn/tmp_9779767c7f1bcf88571232c960248b37.jpg," +
//                "https://images.fast4ward.cn/tmp_c8c1bbf65017361d430f409f99828d88.jpg," +
//                "https://images.fast4ward.cn/tmp_d024b2c236fed9399904e7ead16281d3a6a618854d38cfcd.jpg," +
//                "https://images.fast4ward.cn/tmp_c9b225d1da65a01fbf5a8f3c5c718230.jpg," +
//                "https://images.fast4ward.cn/tmp_560f5c7eb758367b6a317fc29c5f1f1e.jpg," +
//                "https://images.fast4ward.cn/tmp_bcaf064a257a329166a70275d5d5eedb.jpg," +
//                "https://images.fast4ward.cn/tmp_59c9f1920bb54ed4c5902b3a235bb81a.jpg," +
//                "https://images.fast4ward.cn/tmp_2c64a36843b09fa50f226a27bddfb856.jpg," +
//                "https://images.fast4ward.cn/tmp_5ab42e5f2962674306eb73c24f861514.jpg," +
//                "https://images.fast4ward.cn/tmp_5a3205bc09036c29091339f53e5453c4.jpg," +
//                "https://images.fast4ward.cn/tmp_13e3331848eed5f61b5af3ac02dfe210.jpg," +
//                "https://images.fast4ward.cn/tmp_113add53c352a50c614db67e5e31e75a.jpg," +
//                "https://images.fast4ward.cn/tmp_f79408c4b0a8188bbd4758390e087f252a82fc0d02114bf7.jpg," +
//                "https://images.fast4ward.cn/tmp_b6be18f1d86a4eff6278b35a190d2fb5.jpg," +
//                "https://images.fast4ward.cn/tmp_a67557b4be101c4315bf5cdebd751da8c566070275425239.jpg," +
//                "https://images.fast4ward.cn/tmp_fe6e5015576c7c25212bee6c7db503fa.jpg," +
//                "https://images.fast4ward.cn/tmp_90b4ba7c2bfa4ac07a562cb0e4d8edd0.jpg," +
//                "https://images.fast4ward.cn/tmp_fd3d56fc82fac04d91729588f445ad01c1798ccf32f6324a.jpg," +
//                "https://images.fast4ward.cn/tmp_f04e17d216cc2f5adcad68744858e212.jpg," +
//                "https://images.fast4ward.cn/tmp_392eb62e290c80a7c12c09a8f8a620d9.jpg," +
//                "https://images.fast4ward.cn/tmp_3b00fa31441fbf863059c3e2098e7919.jpg," +
//                "https://images.fast4ward.cn/tmp_e22819ed5a66bd0ae1aa77e65433ed05.jpg," +
//                "https://images.fast4ward.cn/tmp_9a5a97fb6f63f24f953534510a792557.jpg," +
//                "https://images.fast4ward.cn/tmp_4e27fba28df8540f5cb24713d365ec04.jpg," +
//                "https://images.fast4ward.cn/tmp_212fb8a40bf070c4d8ea802f1b638f4c877f0e1ad1e932e0.jpg," +
//                "https://images.fast4ward.cn/tmp_d4932d05cff83c446fdeecbef1fe8e35.jpg," +
//                "https://images.fast4ward.cn/tmp_ece0a8819fc1001c1a32f237f656cb76.jpg," +
//                "https://images.fast4ward.cn/tmp_ac7c06afadd52d76046d2ca670a05e5377c2709e73102486.jpg," +
//                "https://images.fast4ward.cn/tmp_a6da696d729eba63596bde0e57322a94c53196e9c437a909.jpg," +
//                "https://images.fast4ward.cn/tmp_0f8773e19b2b921a483d0bf4c45ae09290ba70acf0ec8a54.jpg," +
//                "https://images.fast4ward.cn/tmp_e5f8d7e11d7eafacfa8e6b6dc9378040.jpg," +
//                "https://images.fast4ward.cn/tmp_6db6bd05b681e078aa613ce24f8d1797.jpg," +
//                "https://images.fast4ward.cn/tmp_3a525d67b5a0e02c1c8456de7d68751c.jpg," +
//                "https://images.fast4ward.cn/tmp_9846603142137ac4888584b3ebbdef0b0f6644d64f0e452f.jpg," +
//                "https://images.fast4ward.cn/tmp_b3192763e0c4dc4a76a1452766ade30ffa0f87b69fc040a0.jpg," +
//                "https://images.fast4ward.cn/tmp_80ee05efa757160994f4fbbeda477c45.jpg," +
//                "https://images.fast4ward.cn/tmp_e842861251a258981a71d0056299e488.jpg," +
//                "https://images.fast4ward.cn/tmp_676b24a1bece01fdf8d47fdd6227aa90.jpg," +
//                "https://images.fast4ward.cn/tmp_4e14f5da19b2368733aae2a05915fc9f.jpg," +
//                "https://images.fast4ward.cn/tmp_f33f4114de22427974cb27864ce0c29a.jpg," +
//                "https://images.fast4ward.cn/tmp_e3955e2ef81c86645510fa1fc553aa5fbae21b10a99658ba.jpg," +
//                "https://images.fast4ward.cn/tmp_e6dabd0dc1346ff675808de3cc7ffa9ea68fc863f26113e5.jpg," +
//                "https://images.fast4ward.cn/tmp_09053519a7887b3caaff75f0bcb387e7.jpg," +
//                "https://images.fast4ward.cn/tmp_a1051c047f38136071ff2104ccf70186.jpg," +
//                "https://images.fast4ward.cn/tmp_64a260e4c0151a80bd7b36a4d78535d0.jpg," +
//                "https://images.fast4ward.cn/tmp_0cc142beceebc400eeecf8ae8371f2a4.jpg," +
//                "https://images.fast4ward.cn/tmp_6e961a4542c7b993d2e49773151488c007b178afde32186b.jpg," +
//                "https://images.fast4ward.cn/tmp_10b843aa0c08377ea0f29f86e49bb2fe.jpg," +
//                "https://images.fast4ward.cn/tmp_c55901300628e9b37f6a498b3bba487e.jpg," +
//                "https://images.fast4ward.cn/tmp_8f00916838ee866308858f7da2e0a226.jpg," +
//                "https://images.fast4ward.cn/tmp_1628c99e82a772fb4730fbbe2e2db18a.jpg," +
//                "https://images.fast4ward.cn/tmp_eabc227a76396ee43df196d8a3ef1e54.jpg," +
//                "https://images.fast4ward.cn/tmp_be99eaa8604dfbee281eb1952b3eb1f5.jpg," +
//                "https://images.fast4ward.cn/tmp_093410d6915560fb523fef960c562452.jpg," +
//                "https://images.fast4ward.cn/tmp_ad7d78b440a1fc45a1c610b68894a54e.jpg," +
//                "https://images.fast4ward.cn/tmp_4280efb718069cb6b08a2f260c65e2cf.jpg," +
//                "https://images.fast4ward.cn/tmp_5dde9832cfec2f8d14fcf7627de076dd.jpg," +
//                "https://images.fast4ward.cn/tmp_62bb7db1bd72e39f52a0a49f02cb5690.jpg," +
//                "https://images.fast4ward.cn/tmp_d69327c0b5a2a2b1978157c94b2a3c30.jpg," +
//                "https://images.fast4ward.cn/tmp_8150ff97fa40b9072f07c5829af8e71b.jpg," +
//                "https://images.fast4ward.cn/tmp_2bec866851f643b729e13337b80141bd.jpg," +
//                "https://images.fast4ward.cn/tmp_bfe2b9caa535f11b9655adb0771bce08.jpg," +
//                "https://images.fast4ward.cn/tmp_a68fdf0152f5c8fefe80b2752df36957.jpg," +
//                "https://images.fast4ward.cn/tmp_42d88b621416d780c0746912af93770d.jpg," +
//                "https://images.fast4ward.cn/tmp_18f2482c70eba62445c255ec4a53d926.jpg," +
//                "https://images.fast4ward.cn/tmp_6e4f765cccb9781035506144d8f2904c.jpg," +
//                "https://images.fast4ward.cn/tmp_500d3bdd11a95d63617be0a374b083dc.jpg," +
//                "https://images.fast4ward.cn/tmp_35f37586a54ff4a5c13ee9c4fd3327517fcb6ce7d42ee8a9.jpg," +
//                "https://images.fast4ward.cn/tmp_2d6149a7be883c6a08bf91a3671ae595.jpg," +
//                "https://images.fast4ward.cn/tmp_9e61b2353b3cb065a89c4356b439f088.jpg," +
//                "https://images.fast4ward.cn/tmp_feecf98439dd4aca82599db076fa26bf.jpg," +
//                "https://images.fast4ward.cn/tmp_230c0756f55342941701e660d5ce6ac3.jpg," +
//                "https://images.fast4ward.cn/tmp_54300368e202ff60534afb6615e086ee.jpg," +
//                "https://images.fast4ward.cn/tmp_305ae467582a2b59bd1806b6c04964be.jpg," +
//                "https://images.fast4ward.cn/tmp_fa6cf4f5ac87255d25f1fe3e81c6467d.jpg," +
//                "https://images.fast4ward.cn/tmp_0f43c026b9f7e2eb83666c938f1f573d.jpg," +
//                "https://images.fast4ward.cn/tmp_66598f839cd1363bdd67866748e7ddf0.jpg," +
//                "https://images.fast4ward.cn/tmp_75cce060f9f720b1c3f2174992d91a4f0cc7a646662fc351.jpg," +
//                "https://images.fast4ward.cn/tmp_9e16c5bcc7561acd478e83f1e1700fbf.jpg";
//        String names = "刘荣彬," +
//                "沈张浩," +
//                "周孟磊," +
//                "李国涛," +
//                "李博然," +
//                "杨永杰," +
//                "陶裕敏," +
//                "杨舒晓," +
//                "王海旭," +
//                "董伯智," +
//                "李金翔," +
//                "秦亮亮," +
//                "黄春晴," +
//                "叶文中," +
//                "李洋," +
//                "李云帆," +
//                "李帅兵," +
//                "吕建明," +
//                "魏铭," +
//                "周永杰," +
//                "高坤," +
//                "闫超," +
//                "王思平," +
//                "程昊," +
//                "赵梦辰," +
//                "王晖," +
//                "张云," +
//                "王志魁," +
//                "宗宇," +
//                "李海滨," +
//                "钱大源," +
//                "王晓昕," +
//                "吴航," +
//                "张晨," +
//                "朱飞," +
//                "竺纪明," +
//                "张跃," +
//                "朱源江," +
//                "刘辰," +
//                "张哲," +
//                "叶方晨," +
//                "房纬," +
//                "刘宇涛," +
//                "任佳源," +
//                "马瑞笑," +
//                "梁国华," +
//                "田学毅," +
//                "胡玮," +
//                "翁马力," +
//                "韩佳旺," +
//                "史楷霆," +
//                "刘鹏亮," +
//                "窦彬语," +
//                "柳一歌," +
//                "黄志行," +
//                "景尧," +
//                "金明山," +
//                "吴丹杰," +
//                "谢宇鹏," +
//                "翟佳麟," +
//                "王子恒," +
//                "戴威," +
//                "沈一鼎," +
//                "严侃," +
//                "王远索," +
//                "蔡晨," +
//                "蔡严庆," +
//                "朱义," +
//                "陈杰," +
//                "刘焕松," +
//                "张豪," +
//                "冯锋," +
//                "陈海炳," +
//                "王仲宁," +
//                "吴磊," +
//                "司云明," +
//                "杨龙健," +
//                "王萧蓄," +
//                "沈思明," +
//                "徐海," +
//                "王鑫," +
//                "陈晨," +
//                "陈福," +
//                "林杰," +
//                "王欣泽," +
//                "苏臆," +
//                "程峰," +
//                "陈广辉," +
//                "孙盛龙," +
//                "张小笛," +
//                "杨丽鹏," +
//                "诸葛思彤," +
//                "杨旭," +
//                "徐超," +
//                "张雷," +
//                "纪小鹏," +
//                "林新淋," +
//                "刘傲仁," +
//                "蔡若兰," +
//                "陈斌," +
//                "梁峻荣," +
//                "温泉," +
//                "黄冠," +
//                "赵静," +
//                "张家铭," +
//                "蔡旅思," +
//                "韦乐," +
//                "李英哲," +
//                "李强," +
//                "徐文君," +
//                "周子强," +
//                "王辉," +
//                "吴博," +
//                "位伟," +
//                "姚羭稼," +
//                "郝庆伟," +
//                "万连林," +
//                "刘天亮," +
//                "刘冠林," +
//                "王中平," +
//                "王松," +
//                "于树人," +
//                "姜涛," +
//                "徐欣," +
//                "马甲泉," +
//                "徐海平," +
//                "徐思宇," +
//                "杨晨," +
//                "孙俣," +
//                "刘旭," +
//                "成晨," +
//                "李佳原," +
//                "苏琛," +
//                "孔令轶," +
//                "陈俊," +
//                "殷圣博," +
//                "赵泽宇," +
//                "吴凡";
//        String[] strarry = str.split(",");
//        String[] anmesArray = names.split(",");
//        for (int i = 0; i < strarry.length; i++) {
//            download(strarry[i], anmesArray[i] + ".jpg", "D:\\dimages");
//        }

    }

    public static void download(String urlString, String filename, String savePath) throws Exception {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5 * 1000);
        // 输入流
        InputStream is = con.getInputStream();

        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        File sf = new File(savePath);
        if (!sf.exists()) {
            sf.mkdirs();
        }
        OutputStream os = new FileOutputStream(sf.getPath() + "\\" + filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }


}
