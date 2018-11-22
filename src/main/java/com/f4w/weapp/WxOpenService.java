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
        String str = "https://images.fast4ward.cn/tmp_6bf76e74141468e119e8d1f50e3dd0e0.jpg," +
                "https://images.fast4ward.cn/tmp_1dbaa88e5c4bdc0e7a5b7628d14f56a0.jpg," +
                "https://images.fast4ward.cn/tmp_834bd800899d301e1df732fd528e33f4.jpg," +
                "https://images.fast4ward.cn/tmp_4a57997f498cd7fb1093cebec01429ec.jpg," +
                "https://images.fast4ward.cn/tmp_81f16e21775763c6fbf07dc54973d17302bba38c18fe6435.jpg," +
                "https://images.fast4ward.cn/tmp_0b47e8afccbb41cdcf59eb6ab9a354b8.jpg," +
                "https://images.fast4ward.cn/tmp_151a859d792a317ef69d3678b162dd5e.jpg," +
                "https://images.fast4ward.cn/tmp_0f96cdf91bd70d88ba0342f4b8cf3aec.jpg," +
                "https://images.fast4ward.cn/tmp_ad1e6cd479edb8ae7f8a07051b7782ab.jpg," +
                "https://images.fast4ward.cn/tmp_68e40f6e5046e057c5333d3f8bf12982.jpg," +
                "https://images.fast4ward.cn/tmp_69a27e891fa0720086dec3fc69b03d6d.jpg," +
                "https://images.fast4ward.cn/tmp_b53091eac247a5e326ed7ad388bafd112d6f77b8f9e758b6.jpg," +
                "https://images.fast4ward.cn/tmp_3600204cf58cc869dbc143bb88f2dc8fa1b82ade11334756.jpg," +
                "https://images.fast4ward.cn/tmp_07d22f105b9e50a2a6142ae4a383607d.jpg," +
                "https://images.fast4ward.cn/tmp_424ec42cf78a8ab023a9709825f18ae4.jpg," +
                "https://images.fast4ward.cn/tmp_d9ef028edc9d57b154e9328e9b10f09b.jpg," +
                "https://images.fast4ward.cn/tmp_c5d5e14f45f34566d2d219d94ae3f779.jpg," +
                "https://images.fast4ward.cn/tmp_9358dd8d8054a6214bf1f04158a01314.jpg," +
                "https://images.fast4ward.cn/tmp_2f434df35acbbd877093adfa2e19830b.jpg," +
                "https://images.fast4ward.cn/tmp_ca6c9bd012f08636def8db71da93ea53.jpg," +
                "https://images.fast4ward.cn/tmp_c5621036e495dddfff033bccb8fa2c26b16d16452b573bc6.jpg," +
                "https://images.fast4ward.cn/tmp_c8873b13d18957320643e4ebbf1b4c1b.jpg," +
                "https://images.fast4ward.cn/tmp_8f20817d23f214d0f4543fd047747705.jpg," +
                "https://images.fast4ward.cn/tmp_b041ac9e47dd2894e491124a1ad436b75f6234042397f5a6.jpg," +
                "https://images.fast4ward.cn/tmp_820ea06ae6c3f30c9be612ee82839263.jpg," +
                "https://images.fast4ward.cn/tmp_41c78b85a83dc5c12a85eb84c0ab2772.jpg," +
                "https://images.fast4ward.cn/tmp_144dca42748764825b7f76c560ab4459.jpg," +
                "https://images.fast4ward.cn/tmp_a5e825b5804d4f59d03f4bd571705953.jpg," +
                "https://images.fast4ward.cn/tmp_04b02b9ba12f150dbc03756c86fdc8b6.jpg," +
                "https://images.fast4ward.cn/tmp_c55b4c9585e26ffee1268087ea56ee5fa7b603b2d6ad4c78.jpg," +
                "https://images.fast4ward.cn/tmp_564269b7c7527497fc651872c6398794.jpg," +
                "https://images.fast4ward.cn/tmp_767b91f7c240246b0d404f7148c67539.jpg," +
                "https://images.fast4ward.cn/tmp_434a09a6453d7098b00a1ed29cce63ea.jpg," +
                "https://images.fast4ward.cn/tmp_6ce02a7a0e46d17ccc39464f162c68b86524238b83987d9c.jpg," +
                "https://images.fast4ward.cn/tmp_f93d57156feb6433d6f980a13593356d.jpg," +
                "https://images.fast4ward.cn/tmp_9095918c764a576a1060bfc5383ad5c5.jpg," +
                "https://images.fast4ward.cn/tmp_30f2d4fc88826bcaa1480f9a74fa3db7.jpg," +
                "https://images.fast4ward.cn/tmp_9e53428db51550f8dc56ca8b83f64047.jpg," +
                "https://images.fast4ward.cn/tmp_95d629b1b7301398d583fc5ea9e972ea.jpg," +
                "https://images.fast4ward.cn/tmp_ab08582f65f5f5f06e7e6c97fd92d71a.jpg," +
                "https://images.fast4ward.cn/tmp_6a0b8483600d2eb23aed5b59d422fd26.jpg," +
                "https://images.fast4ward.cn/tmp_55fe6a62b78885d12393d7716b78d553.jpg," +
                "https://images.fast4ward.cn/tmp_4939113c09f9d850350d1a32527962bb5f5e5c938291d8f7.jpg," +
                "https://images.fast4ward.cn/tmp_faca716751f3ee0fe40e1a435a5cf80d.jpg," +
                "https://images.fast4ward.cn/tmp_478c137e692c3855fe7d660f8f921ea4.jpg," +
                "https://images.fast4ward.cn/tmp_81b261fd6db7a16e7e55735ea94b4e1a.jpg," +
                "https://images.fast4ward.cn/tmp_a2135b95b306b4c539e41931236d362d.jpg," +
                "https://images.fast4ward.cn/tmp_4e0ab36e9da3c3e7980b7f2a86932bbf.jpg," +
                "https://images.fast4ward.cn/tmp_3c8ef2fb12fcfc8a27a1a7e6317c97806018bf0b8c23945c.jpg," +
                "https://images.fast4ward.cn/tmp_6a67cabcef2ae3a064fef90ff39d00c3.jpg," +
                "https://images.fast4ward.cn/tmp_5305c859ca666f58c9b3b2dbdbf0fc94.jpg," +
                "https://images.fast4ward.cn/tmp_d71ea9e501db25adcea087e7d13aee54.png," +
                "https://images.fast4ward.cn/tmp_140e75a350794c6a3ada2ae6c704b50a.jpg," +
                "https://images.fast4ward.cn/tmp_d512d715e6ec28b8e28e831f248eac77.jpg," +
                "https://images.fast4ward.cn/tmp_ff54269520ed3bafe88ce5b5831baa0c.jpg," +
                "https://images.fast4ward.cn/tmp_0eb5d3e494702f6b3451193eed54bd9e.jpg," +
                "https://images.fast4ward.cn/tmp_afca60d3766316fff0443f3697d11505.jpg," +
                "https://images.fast4ward.cn/tmp_4d807af7e66cf94882af6895643574c6.jpg," +
                "https://images.fast4ward.cn/tmp_ec38f063492d6fd18a14806b990247da.jpg," +
                "https://images.fast4ward.cn/tmp_bc4444eb05a9b56ae4f031e18e335201.jpg," +
                "https://images.fast4ward.cn/tmp_49ad8a6cfd03cbe2bfa84da4405f9576.jpg," +
                "https://images.fast4ward.cn/tmp_02421bbc38974ed132f2d58ae9ceb2d5.jpg," +
                "https://images.fast4ward.cn/tmp_8270f5627f8979db8887cbb27bb97eb63ae8e728c9b2c6da.jpg," +
                "https://images.fast4ward.cn/tmp_ca4dca5dd6e836f1f6de65e0874fa6a7.jpg," +
                "https://images.fast4ward.cn/tmp_49ccb44fbd29011d958c60fa096c1f4b.jpg," +
                "https://images.fast4ward.cn/tmp_50ec878a30583086b0a9462b0cc9a754.jpg," +
                "https://images.fast4ward.cn/tmp_342dd0aca8e3264521745d172fae5931.jpg," +
                "https://images.fast4ward.cn/tmp_eaafce540deadd9b41b717313266c4ff.jpg," +
                "https://images.fast4ward.cn/tmp_43366e06e534ba2aef27132e54dbaf8b.jpg," +
                "https://images.fast4ward.cn/tmp_be709f0d7fdc4477a263a4f91d8553fb.jpg," +
                "https://images.fast4ward.cn/tmp_ce91e96b3938f238c59003e3b37b1e38.jpg," +
                "https://images.fast4ward.cn/tmp_0041f137e24c6be4d51f9356d2ba83e0.jpg," +
                "https://images.fast4ward.cn/tmp_af19693d5072f9c5835f87a461f5bfb6a5e7b9625b2893b2.jpg," +
                "https://images.fast4ward.cn/tmp_580846ea2edce20362278bc7635f58ad.jpg," +
                "https://images.fast4ward.cn/tmp_b86c275b8dfa20044de6d561eb1f84b01e668ae384207233.jpg," +
                "https://images.fast4ward.cn/tmp_552d423fc3d887ed844e8a386c791ed6.jpg," +
                "https://images.fast4ward.cn/tmp_442fcaa56f25cb0ebcf9d58f077dfc3f.jpg," +
                "https://images.fast4ward.cn/tmp_6e03db0d64f127f9fb75cc0fa9fd60883d6631a93f79833b.jpg," +
                "https://images.fast4ward.cn/tmp_d2078d9f0f2a34530f62bbeb87096fa4.jpg," +
                "https://images.fast4ward.cn/tmp_a10ae7553e2eccd9c51f12c18fac0473.jpg," +
                "https://images.fast4ward.cn/tmp_3b4cfacef29b43a564ba371d84eb247e.jpg," +
                "https://images.fast4ward.cn/tmp_793487efe96b4e163a94fbe280b0537c.jpg," +
                "https://images.fast4ward.cn/tmp_8f7c9dc644c18b3af8dea7d660ce447e.jpg," +
                "https://images.fast4ward.cn/tmp_c1911db850ca102dcac5c6d0ed522dfd.jpg," +
                "https://images.fast4ward.cn/tmp_cdc267dc948bb5974269fe05bdd3ea1581e9deab44e9b1da.jpg," +
                "https://images.fast4ward.cn/tmp_fbf0c8e94b0fd73b7d178814b36aa247.jpg," +
                "https://images.fast4ward.cn/tmp_5bb70ce6cbc5b86e739132410ea7e672.jpg," +
                "https://images.fast4ward.cn/tmp_1b6ffd13515ed7925fcaf61763e4333933fabb04f33cf8cd.jpg," +
                "https://images.fast4ward.cn/tmp_d7bc60d8b2b6fd69dabb3e57c83a5cd7ef71904f9d686bb6.jpg," +
                "https://images.fast4ward.cn/tmp_07596d73c178cc859a1bf8ce8f5e31980018d41ffcd09099.jpg," +
                "https://images.fast4ward.cn/tmp_be502318c4d672e5a39a81848878bdf2.jpg," +
                "https://images.fast4ward.cn/tmp_8afbe4ff93dd2d27e1b74b990820b1c6.jpg," +
                "https://images.fast4ward.cn/tmp_c1547096bd13ad95aebe2a6239da345c.jpg," +
                "https://images.fast4ward.cn/tmp_c38ae7c4cc1efdf002d015359fb6d4caad58b1e8d4f39e1b.jpg," +
                "https://images.fast4ward.cn/tmp_994bdc3e5b426cc1de935b3f297b7c695ec077b947a3d906.jpg," +
                "https://images.fast4ward.cn/tmp_ac5ef4c656a3d88bd2de02d64469aeff.jpg," +
                "https://images.fast4ward.cn/tmp_95eb14ad6e22f99c4c0be708af8104ab.jpg," +
                "https://images.fast4ward.cn/tmp_d90af05125082f70cc3c1bbf82882b2a.jpg," +
                "https://images.fast4ward.cn/tmp_cd8551ad7913921b081ebdb70c9cd2ee.jpg," +
                "https://images.fast4ward.cn/tmp_93cb8185c44c4bccd6977a424b1fc2b7.jpg," +
                "https://images.fast4ward.cn/tmp_ce25be3134e14cc4befa016e814095fccc3801c4f909e961.jpg," +
                "https://images.fast4ward.cn/tmp_fa290815069a34e1a595bab965b16ade014ad5f8dd3528a7.jpg," +
                "https://images.fast4ward.cn/tmp_1b852e16f0502aa77f1bfde4e6efc7a3.jpg," +
                "https://images.fast4ward.cn/tmp_40241e11cc07cf330ffe7cedc101012e.jpg," +
                "https://images.fast4ward.cn/tmp_2b3224a7f7ec30050721a629d10a27f5.jpg," +
                "https://images.fast4ward.cn/tmp_3fa687ce2eeea275e10e035877a70de7.jpg," +
                "https://images.fast4ward.cn/tmp_a756d1d64ad73b1bc41350a3550966d3a2a21154848f3127.jpg," +
                "https://images.fast4ward.cn/tmp_f23c67ffcfaaf22cf935d79b997501f8.jpg," +
                "https://images.fast4ward.cn/tmp_d6c6116b7769a5dd69847d7fe5ca3080.jpg," +
                "https://images.fast4ward.cn/tmp_ee3508bfc5803306644e8993764b17a2.jpg," +
                "https://images.fast4ward.cn/tmp_eda9f316c7b5247956fe40ec40a640f8.jpg," +
                "https://images.fast4ward.cn/tmp_70ccf44ccb5833806c7ca882c1a35c3b.jpg," +
                "https://images.fast4ward.cn/tmp_7f01dfb270d2b9b9a9838bd98672a6f4.jpg," +
                "https://images.fast4ward.cn/tmp_a6b0c6784ef0dd6f7c15eb38dc0ddfb8.jpg," +
                "https://images.fast4ward.cn/tmp_4d567afa66338d403cdabf0d23f1c86c.jpg," +
                "https://images.fast4ward.cn/tmp_c926bf8a06f9b397a2e66f6481e4d79a948a7e79766e190b.jpg," +
                "https://images.fast4ward.cn/tmp_841187c0a8e2e877fecc595776bc9794.jpg," +
                "https://images.fast4ward.cn/tmp_64fa66e42130bb8373161a7acf96b02f.jpg," +
                "https://images.fast4ward.cn/tmp_503f09d8a62c2f2d3f0f3e9da34af24e.jpg," +
                "https://images.fast4ward.cn/tmp_257070acc238c0820ebc74681776d7da.jpg," +
                "https://images.fast4ward.cn/tmp_ed723461706fb3547f6ee390a8567ae6.jpg," +
                "https://images.fast4ward.cn/tmp_b189449b4eb80a9c900dbc40025ed740.jpg," +
                "https://images.fast4ward.cn/tmp_9416a884ce9274ed19eb9dbe06dace90.jpg," +
                "https://images.fast4ward.cn/tmp_76df428ce3adfde1eede34d63e13168c.jpg," +
                "https://images.fast4ward.cn/tmp_54b5247ca5e35d013da2e6abfb963670.jpg," +
                "https://images.fast4ward.cn/tmp_32bd3964ddb84d245e827998e9a7388e.jpg," +
                "https://images.fast4ward.cn/tmp_2638ceafc35b489c8b85ac5aecd43523.jpg," +
                "https://images.fast4ward.cn/tmp_cd1d66ae8b69a1f504e620e7b62e16a9.jpg," +
                "https://images.fast4ward.cn/tmp_eb5d1eebad92aba0604f085ea15359092df50179edb52cbe.jpg," +
                "https://images.fast4ward.cn/tmp_2072e2795c1d4e9bda35829e96016ec1.jpg," +
                "https://images.fast4ward.cn/tmp_b2c64233b57b0ff8d2428d112cf544b0.jpg," +
                "https://images.fast4ward.cn/tmp_b3bceed4f91e354d27356cb9ec3beb88.jpg," +
                "https://images.fast4ward.cn/tmp_7c4eb1fee8489f635a5f6f6fb7096975.jpg," +
                "https://images.fast4ward.cn/tmp_4fd33ed82a47bc26e2316640c86cc58e.jpg," +
                "https://images.fast4ward.cn/tmp_14eca4126e607cef11e147c773e024a3.jpg," +
                "https://images.fast4ward.cn/tmp_76d717d20fb4724189bf371949a0fc89.jpg," +
                "https://images.fast4ward.cn/tmp_a22d35ee52941dffda772365b138e7d9.jpg," +
                "https://images.fast4ward.cn/tmp_0a1167b3b2e826c99202ff4b2737f39d.jpg," +
                "https://images.fast4ward.cn/tmp_b65a9c7903b522832b4c6a79ef77de2ffb65c0e961312dc9.jpg," +
                "https://images.fast4ward.cn/tmp_446c9db2a5965e0195daf982425540f1.jpg," +
                "https://images.fast4ward.cn/tmp_864e0ab72acb517cecacb8c3de974ebb.jpg," +
                "https://images.fast4ward.cn/tmp_393fbb78e1fc3b0dc91f6e95b3f0a03c.jpg,";
        String names = "刘荣彬," +
                "沈张浩," +
                "周孟磊," +
                "李国涛," +
                "李博然," +
                "杨永杰," +
                "陶裕敏," +
                "杨舒晓," +
                "王海旭," +
                "董伯智," +
                "李金翔," +
                "秦亮亮," +
                "黄春晴," +
                "叶文中," +
                "李洋," +
                "李云帆," +
                "李帅兵," +
                "吕建明," +
                "魏铭," +
                "周永杰," +
                "许云鹤," +
                "高坤," +
                "闫超," +
                "王思平," +
                "程昊," +
                "赵梦辰," +
                "王晖," +
                "张云," +
                "王志魁," +
                "宗宇," +
                "李海滨," +
                "钱大源," +
                "王晓昕," +
                "吴航," +
                "张晨," +
                "朱飞," +
                "竺纪明," +
                "张跃," +
                "朱源江," +
                "刘辰," +
                "张哲," +
                "叶方晨," +
                "房纬," +
                "刘宇涛," +
                "任佳源," +
                "马瑞笑," +
                "梁国华," +
                "田学毅," +
                "胡玮," +
                "翁马力," +
                "韩佳旺," +
                "史楷霆," +
                "刘鹏亮," +
                "窦彬语," +
                "柳一歌," +
                "黄志行," +
                "景尧," +
                "金明山," +
                "吴丹杰," +
                "谢宇鹏," +
                "翟佳麟," +
                "王子恒," +
                "戴威," +
                "沈一鼎," +
                "严侃," +
                "王远索," +
                "蔡晨," +
                "蔡严庆," +
                "朱义," +
                "陈杰," +
                "刘焕松," +
                "张豪," +
                "冯锋," +
                "陈海炳," +
                "王仲宁," +
                "吴磊," +
                "司云明," +
                "杨龙健," +
                "王萧蓄," +
                "沈思明," +
                "徐海," +
                "王鑫," +
                "陈晨," +
                "陈福," +
                "林杰," +
                "王欣泽," +
                "苏臆," +
                "程峰," +
                "陈广辉," +
                "孙盛龙," +
                "张小笛," +
                "杨丽鹏," +
                "诸葛思彤," +
                "杨旭," +
                "徐超," +
                "张雷," +
                "纪小鹏," +
                "林新淋," +
                "刘傲仁," +
                "蔡若兰," +
                "陈斌," +
                "梁峻荣," +
                "温泉," +
                "黄冠," +
                "赵静," +
                "张家铭," +
                "蔡旅思," +
                "韦乐," +
                "李英哲," +
                "李强," +
                "徐文君," +
                "周子强," +
                "王辉," +
                "吴博," +
                "位伟," +
                "金宏宇," +
                "姚羭稼," +
                "郝庆伟," +
                "万连林," +
                "刘天亮," +
                "刘冠林," +
                "王中平," +
                "王松," +
                "于树人," +
                "姜涛," +
                "徐欣," +
                "马甲泉," +
                "徐海平," +
                "徐思宇," +
                "杨晨," +
                "孙俣," +
                "刘旭," +
                "成晨," +
                "李佳原," +
                "苏琛," +
                "孔令轶," +
                "陈俊," +
                "殷圣博," +
                "赵泽宇," +
                "吴凡," +
                "FRANCISCOJAVIER," +
                "刘聪,";
        String[] strarry = str.split(",");
        String[] anmesArray = names.split(",");
//        for (int i = 0; i < strarry.length; i++) {
//            download(strarry[i], anmesArray[i] + ".jpg", "D:\\dimage2");
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
