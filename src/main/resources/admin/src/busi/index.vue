<template>
  <div>
    <el-card class="box-card">
      <el-row>
        <el-col>
          <h2>昨日概况</h2>
        </el-col>
      </el-row>
      <el-row :gutter="12">
        <el-col :span="6">
          <el-card shadow="always">
            <div style="text-align: center;color:#999">
              <h5>访问人数</h5>
              <h3 style="color:#000">{{uv}}</h3>
              <p style="font-size: 10px;">日&nbsp;&nbsp;+35.35</p>
              <p style="font-size: 10px;">周&nbsp;&nbsp;+35.35</p>
              <p style="font-size: 10px;">月&nbsp;&nbsp;+35.35</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="always">
            <div style="text-align: center;color:#999">
              <h5>访问次数</h5>
              <h3 style="color:#000">{{pv}}</h3>
              <p style="font-size: 10px;">日&nbsp;&nbsp;+35.35</p>
              <p style="font-size: 10px;">周&nbsp;&nbsp;+35.35</p>
              <p style="font-size: 10px;">月&nbsp;&nbsp;+35.35</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="always">
            <div style="text-align: center;color:#999">
              <h5>商品新增数</h5>
              <h3 style="color:#000">{{productAdd}}</h3>
              <p style="font-size: 10px;">日&nbsp;&nbsp;+35.35</p>
              <p style="font-size: 10px;">周&nbsp;&nbsp;+35.35</p>
              <p style="font-size: 10px;">月&nbsp;&nbsp;+35.35</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="always">
            <div style="text-align: center;color:#999">
              <h5>卖家新增数</h5>
              <h3 style="color:#000">{{sellAdd}}</h3>
              <p style="font-size: 10px;">日&nbsp;&nbsp;+35.35</p>
              <p style="font-size: 10px;">周&nbsp;&nbsp;+35.35</p>
              <p style="font-size: 10px;">月&nbsp;&nbsp;+35.35</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
    <el-card class="box-card">
      <el-row>
        <div>
          <div style="float:left;margin-left:1vw;"><h2>TOP 20</h2></div>
          <div class="block" style="padding:20px 0;float:left;margin-left:1vw;">
            <el-date-picker style="width:100%"
                            v-model="value7"
                            type="daterange"
                            align="left"
                            unlink-panels
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            :picker-options="pickerOptions2">
            </el-date-picker>
          </div>
          <!--<div style="padding:25px 0;color:#999;font-size:12px;float:left;margin-left:1vw;">-->
          <!--<el-button size="mini" round>昨日</el-button>-->
          <!--<el-button size="mini" round>近七天</el-button>-->
          <!--<el-button size="mini" round>近30天</el-button>-->
          <!--</div>-->
        </div>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-card shadow="always">
            <div style="text-align: center;color:#999">
              <p style="font-size: 1rem;">搜索标签</p>
              <div style="padding:5px 5vw">
                <el-row style="padding-top:5px" v-for="(item,index) in searchTag">
                  <el-col :span="8">
                    {{index+1}}
                  </el-col>
                  <el-col :span="8">
                    {{item.tag}}
                  </el-col>
                  <el-col :span="8">
                    {{item.num}}
                  </el-col>
                </el-row>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="always">
            <div style="text-align: center;color:#999">
              <p style="font-size: 1rem;">搜索0结果标签</p>
              <div style="padding:5px 5vw">
                <el-row style="padding-top:5px" v-for="(item,index) in searchTagNone">
                  <el-col :span="8">
                    {{index+1}}
                  </el-col>
                  <el-col :span="8">
                    {{item.tag}}
                  </el-col>
                  <el-col :span="8">
                    {{item.num}}
                  </el-col>
                </el-row>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="always">
            <div style="text-align: center;color:#999">
              <p style="font-size: 1rem;">出售商品标签</p>
              <div style="padding:5px 5vw">
                <el-row style="padding-top:5px" v-for="(item,index) in sellTag">
                  <el-col :span="8">
                    {{index+1}}
                  </el-col>
                  <el-col :span="8">
                    {{item.tag}}
                  </el-col>
                  <el-col :span="8">
                    {{item.num}}
                  </el-col>
                </el-row>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
    <el-card class="box-card">
      <el-row>
        <el-col>
          <div style="float:left;margin-left:1vw;">
            <!--<el-dropdown :hide-on-click="false">-->
            <!--<span class="el-dropdown-link">-->
            <!--<div style="padding:20px 0;float:left;margin-left:1vw;">-->
            <!--<span style="font-size: 1rem;color:#000;font-weight: bold">新增商品数</span><i-->
            <!--class="el-icon-arrow-down el-icon&#45;&#45;right"></i>-->
            <!--</div>-->
            <!--</span>-->
            <!--<el-dropdown-menu slot="dropdown">-->
            <!--<el-dropdown-item>商品累计数</el-dropdown-item>-->
            <!--<el-dropdown-item>卖家累计数</el-dropdown-item>-->
            <!--<el-dropdown-item>新增商品数</el-dropdown-item>-->
            <!--</el-dropdown-menu>-->
            <!--</el-dropdown>-->
            <h2>统计</h2>
          </div>
          <div class="block" style="padding:20px 0;float:left;margin-left:1vw;">
            <el-date-picker style="width:100%"
                            v-model="pickDate"
                            type="daterange"
                            align="left"
                            unlink-panels
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            :picker-options="pickerOptions2"
                            value-format="yyyy-MM-dd"
                            @change="changeHisDate">
            </el-date-picker>
          </div>
          <!--<div style="padding:25px 0;color:#999;font-size:12px;float:left;margin-left:1vw;">-->
          <!--<el-button size="mini" round>昨日</el-button>-->
          <!--<el-button size="mini" round>近七天</el-button>-->
          <!--<el-button size="mini" round>近30天</el-button>-->
          <!--</div>-->
        </el-col>
      </el-row>
      <el-row>
        <line-chart :chart-data="lineChartData"/>
      </el-row>
    </el-card>
  </div>
</template>
<style>

</style>
<script>
  import LineChart from './components/LineChart'
  import {getPv, getPersonUv, getProductAdd, getTagTop, getHistogram} from '@/api/index'
  import {parseTime} from '@/utils'

  export default {
    name: 'index',
    components: {LineChart},
    data() {
      return {
        pv: 0,
        uv: 0,
        productAdd: 0,
        sellAdd: 0,
        searchTag: [],
        searchTagNone: [],
        sellTag: [],
        lineChartData: {
          XAxisData: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          newAddProduct: [100, 120, 161, 134, 105, 160, 165],
          sumProduct: [120, 82, 91, 154, 162, 140, 145],
          sumSeller: [231, 12, 2, 123, 23, 43, 65],
        },
        pickerOptions2: {
          shortcuts: [{
            text: '今天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * start.getHours());
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '昨天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 1);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }]
        },
        value7: "",
        pickDate: ''
      }
    },
    created() {
      this.getIndexPv()
      this.getIndexUv()
      this.getIndexProdectAdd()
      this.getTagSearch()
      this.getIndexHis()
    },
    methods: {
      getIndexPv() {
        getPv().then(res => {
          this.pv = res.data.data.count
        })
      },
      getIndexUv() {
        getPersonUv({}).then(res => {
          this.uv = res.data.data.count
        })
      },
      getIndexProdectAdd() {
        getProductAdd().then(res => {
          this.productAdd = res.data.data.productCount
          this.sellAdd = res.data.data.sellCount
        })
      },
      getTagSearch() {
        getTagTop({key: 'tag'}).then(res => {
          this.searchTag = res.data.data.list
        })
      },
      getTagSearchNone() {
        getTagTop({key: 'tag0'}).then(res => {
          this.searchTagNone = res.data.data.list
        })
      },
      getTagSell() {
        getTagTop({key: 'sell'}).then(res => {
          this.sellTag = res.data.data.list
        })
      },
      getIndexHis() {
        let d = parseTime(new Date(), '{y}-{m}-{d}')
        getHistogram({from: d, to: d}).then(res => {
          this.lineChartData = res.data.data.data
        })
      },
      changeHisDate(date) {
        getHistogram({from: date[0], to: date[1]}).then(res => {
          this.$set(this.lineChartData, 'XAxisData', res.data.data.data.XAxisData)
        })
      }
    }
  }
</script>
