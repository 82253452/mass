<template>
  <div>
    <el-form ref="form" :model="data.data" label-width="120px">
      <div style="text-align: center;font-size:30px;font-weight: bold;padding:20px;">默认条件</div>
      <el-form-item v-for="(k,v) in cat" :label="k.title">
        <el-radio-group v-model="data.data.param[k.k]">
          <div style="margin-top:20px;">
            <el-radio-button size="small" v-for="k2 in k.list" :label="k2.v">{{k2.text}}
            </el-radio-button>
          </div>
        </el-radio-group>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
  export default {
    props: {
      data: {
        type: Object,
        default: {}
      }
    },
    data() {
      return {
        msg: 'hello vue',
        radio4: 'New York',
        cat: [{
          title: '排序', k: 's', list: [
            {text: '综合', k: 's', v: '6'},
            {text: '油耗', k: 's', v: '24'},
            {text: '操控', k: 's', v: '12'},
            {text: '性价比', k: 's', v: '18'},
            {text: '动力', k: 's', v: '10'},
          ]
        }, {
          title: '价格', k: 'p', list: [
            {text: '不限', k: 'p', v: ''},
            {text: '五万以下', k: 'p', v: '0-5'},
            {text: '5-8万', k: 'p', v: '5-8'},
            {text: '8-12万', k: 'p', v: '8-12'},
            {text: '12-18万', k: 'p', v: '12-18'},
            {text: '18-25万', k: 'p', v: '18-25'},
            {text: '25-40万', k: 'p', v: '25-40'},
            {text: '40-80万', k: 'p', v: '40-80'},
            {text: '80万以上', k: 'p', v: '80-9999'},
          ]
        },
          {
            title: '车身', k: 'b', list: [
              {text: '不限', k: 'b', v: ''},
              {text: '两厢', k: 'b', v: '1'},
              {text: '三厢', k: 'b', v: '2'},
              {text: '旅行版', k: 'b', v: '8'},
            ]
          },
          {
            title: '国别', k: 'gc', list: [
              {text: '不限', k: 'gc', v: ''},
              {text: '自主', k: 'gc', v: 'g_1'},
              {text: '合资', k: 'gc', v: 'g_2'},
              {text: '进口', k: 'gc', v: 'g_4'},
              {text: '德系', k: 'gc', v: 'c_4'},
              {text: '日系', k: 'gc', v: 'c_2'},
              {text: '韩系', k: 'gc', v: 'c_16'},
              {text: '美系', k: 'gc', v: 'c_8'},
              {text: '欧系', k: 'gc', v: 'c_484'},
            ]
          },
          {
            title: '变速箱', k: 't', list: [
              {text: '不限', k: 't', v: ''},
              {text: '手动', k: 't', v: '1'},
              {text: '自动-AT', k: 't', v: '2'},
              {text: '半自动-AMT', k: 't', v: '3'},
              {text: '手自一体', k: 't', v: '4'},
              {text: '无极变速CVT', k: 't', v: '6'},
              {text: '双离合DSG', k: 't', v: '16'},
            ]
          }, {
            title: '排量', k: 'd', list: [
              {text: '不限', k: 'd', v: ''},
              {text: '1.3L以下', k: 'd', v: '0-1.3'},
              {text: '1.3-1.6L', k: 'd', v: '1.3-1.6'},
              {text: '1.7-2.0L', k: 'd', v: '1.7-2.0'},
              {text: '2.1-3.0L', k: 'd', v: '2.1-3.0'},
              {text: '3.1-5.0L', k: 'd', v: '3.1-5.0'},
              {text: '5.0以上', k: 'd', v: '5.0-9'},
            ]
          }, {
            title: '驱动', k: 'dt', list: [
              {text: '不限', k: 'dt', v: ''},
              {text: '前驱', k: 'dt', v: '1'},
              {text: '后驱', k: 'dt', v: '2'},
              {text: '四驱', k: 'dt', v: '252'},
              {text: '全时四驱', k: 'dt', v: '4'},
              {text: '分时四驱', k: 'dt', v: '8'},
              {text: '适时四驱', k: 'dt', v: '16'},
            ]
          }, {
            title: '燃料', k: 'f', list: [
              {text: '不限', k: 'f', v: ''},
              {text: '汽油', k: 'f', v: '7'},
              {text: '柴油', k: 'f', v: '8'},
              {text: '纯电动', k: 'f', v: '16'},
              {text: '油电混合', k: 'f', v: '2'},
              {text: '油汽混合', k: 'f', v: '4'},
            ]
          }, {
            title: '车门数', k: 'm1', list: [
              {text: '不限', k: 'm1', v: ''},
              {text: '2-3门', k: 'm1', v: '268'},
              {text: '4-5门', k: 'm1', v: '270'},
            ]
          }, {
            title: '座位数', k: 'm2', list: [
              {text: '不限', k: 'm2', v: ''},
              {text: '2座', k: 'm2', v: '262'},
              {text: '4-5座', k: 'm2', v: '263'},
              {text: '6座', k: 'm2', v: '265'},
              {text: '7座', k: 'm2', v: '266'},
              {text: '7座以上', k: 'm2', v: '267'},
            ]
          },
        ],
      }
    },
    methods: {
      test() {
        console.log(333)
      }
    }
  }
</script>
