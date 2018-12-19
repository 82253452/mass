<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        :placeholder="$t('table.title')"
        v-model="listQuery.title"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        {{ $t('table.search') }}
      </el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="handleCreate">{{ $t('table.add') }}
      </el-button>
    </div>

    <el-table
      v-loading="listLoading"
      :key="tableKey"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%;min-height:500px;">
      <el-table-column align="center" label="id" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="模板名称" width="150">
        <template slot-scope="scope">
          <el-popover trigger="hover" placement="top">
            <el-input
              v-model="scope.row.pageName"
              placeholder="页面名称"
              @blur="modifyPageName(scope.row.id,scope.row.pageName)"/>
            <div slot="reference" class="name-wrapper">
              <el-tag size="medium">{{ scope.row.pageName }}</el-tag>
            </div>
          </el-popover>
        </template>
      </el-table-column>

      <el-table-column align="center" label="隐藏导航" width="150">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.custom==1"
            @change="modifyCustom(scope.row.id,scope.row.custom)"/>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" fixed="right" width="150" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('table.edit') }}</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row,'deleted')">{{ $t('table.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container" style="text-align: center">
      <el-pagination
        :current-page="listQuery.page"
        :page-sizes="[10,20,30, 50]"
        :page-size="listQuery.limit"
        :total="total"
        background
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"/>
    </div>

    <el-dialog
      :title="textMap[dialogStatus]"
      :visible.sync="dialogFormVisible"
      width="80vw"
      top="10vh"
      custom-class="diaClass">
      <el-row :gutter="20">
        <el-col :span="7">
          <el-card>
            <h3>页面</h3>
            <draggable
              v-model="pageList"
              :options="{group:{ name:'page', pull:'clone', put:false}}"
              :clone="pageClone"
              style="width:100%;min-height: 150px;"
              @start="onPageStart"
              @remove="onPageRemove"
              @end="onPageEnd">
              <div v-for="element in pageList" :key="element.id" style="float: left;margin:3%;">
                <el-card :body-style="{ padding: '0px' }" style="width:80px;height: 80px">
                  <div style="text-align: center">
                    <i class="el-icon-edit" style="width: 40px;height: 40px;padding-top:20px;"/>
                    <div style="padding-top: 10px;">
                      <span>{{ element.title }}</span>
                    </div>
                  </div>
                </el-card>
              </div>
            </draggable>
            <h3>组件库</h3>
            <draggable
              v-model="comList"
              :options="{group:{ name:'compont', pull:'clone', put:false}}"
              :clone="compClone"
              style="width:100%;min-height: 350px;"
              @start="drag=true"
              @end="onEnd">
              <div v-for="element in comList" :key="element.id" style="float: left;margin:3%;">
                <el-card :body-style="{ padding: '0px' }" style="width:80px;min-height: 80px">
                  <div style="text-align: center">
                    <i class="el-icon-edit" style="width: 40px;height: 40px;padding-top:20px;"/>
                    <div style="padding-top: 10px;">
                      <span>{{ element.label }}</span>
                    </div>
                  </div>
                </el-card>
              </div>
            </draggable>
            <div style="text-align: center;clear:both;">
              <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">保存页面</el-button>
              <el-button v-else type="primary" @click="updateData">保存页面</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="7" :offset="1" style="">
          <div class="appMain">
            <div
              style="background: url('https://demo.yiovo.com/assets/store/img/diy/phone-top-black.png')  no-repeat center/contain;
                 height: 66px;text-align: center;background-color: #fff;padding-top:0;">
              <h4 style="padding-top:40px;margin:1px;">{{ temp.pageTitle }}</h4>
            </div>
            <draggable
              v-if="temp.comResult"
              v-model="temp.comResult"
              :options="{group:'compont'}"
              class="phoneView"
              @start="drag=true"
              @end="drag=false">
              <component
                v-for="(element,index) in temp.comResult"
                :key="index"
                :data="element.data"
                :is="element.name"
                :class="index===borderTemp?'dottedLine comResult':'comResult'"
                @click.native="comClick(index)"
                @dblclick.native="comDbClick(index)"
                @mouseenter.native="enter(index)"
                @mouseleave.native="leave()"
              />
            </draggable>
            <div style="background: #fff;text-align: center;height:80px;border-top:1px solid #999999">
              <draggable
                v-model="pageListResult"
                :options="{group:'page'}"
                style="height:80px;"
                @start="drag=true"
                @end="drag=false">
                <div
                  v-for="(element,index) in pageListResult"
                  :key="index"
                  :style="{width:parseInt(100/tempPageSize)-1+'%',float:'left'}"
                  @click="pageConfig(index)">
                  <div style="text-align: center;min-height:50px;">
                    <!--<img src="element.img" style="width: 40px;height: 40px;padding-top:10px;">-->
                    <i :class="'iconfont icon-'+element.iconName" style="font-size:30px;padding-top:10px;"/>
                    <div style="padding-top:5px;font-size: 4px;">
                      {{ element.pageName||element.title }}
                    </div>
                  </div>
                </div>
              </draggable>
            </div>
          </div>
        </el-col>
        <el-col :span="7" :offset="1">
          <component
            v-if="dateTemp.name"
            :data="dateTemp"
            :is="dateTemp.name+'Edit'"
            @update="updateComData"/>
        </el-col>
      </el-row>
    </el-dialog>

  </div>
</template>

<script>
import { selectByPage, insert, selectById, updateById, deleteById } from '@/api/busiAppPage'
import waves from '@/directive/waves' // 水波纹指令
import draggable from 'vuedraggable'
import ScrollImg from './components/ScrollImg'
import List from './components/List'
import ListEdit from './components/ListEdit'
import PageEdit from './components/PageEdit'
import ScrollImgEdit from './components/ScrollImgEdit'
import Navigation from './components/Navigation'
import NavigationEdit from './components/NavigationEdit'
import { parseTime } from '@/utils'
import Dictionary from './components/Dictionary'
import DictionaryEdit from './components/DictionaryEdit'
import HuangLi from './components/HuangLi'
import HuangLiEdit from './components/HuangLiEdit'
import NameMatch from './components/NameMatch'
import NameMatchEdit from './components/NameMatchEdit'
import NameRate from './components/NameRate'
import NameRateEdit from './components/NameRateEdit'
import CookBook from './components/CookBook'
import CookBookEdit from './components/CookBookEdit'
import CarKouBei from './components/CarKouBei'
import CarKouBeiEdit from './components/CarKouBeiEdit'
import HomeImg from './components/HomeImg'
import HomeImgEdit from './components/HomeImgEdit'
import GoodsHello from './components/GoodsHello'
import GoodsHelloEdit from './components/GoodsHelloEdit'
import Anquanqi from './components/Anquanqi'
import AnquanqiEdit from './components/AnquanqiEdit'
import KuaiDi from './components/KuaiDi'
import KuaiDiEdit from './components/KuaiDiEdit'

export default {
  name: 'ComplexTable',
  components: {
    draggable,
    ScrollImg,
    ScrollImgEdit,
    List,
    ListEdit,
    PageEdit,
    Navigation,
    NavigationEdit,
    Dictionary,
    DictionaryEdit,
    HuangLi,
    HuangLiEdit,
    NameMatch,
    NameMatchEdit,
    NameRate,
    NameRateEdit,
    CookBook,
    CookBookEdit,
    CarKouBei,
    CarKouBeiEdit,
    HomeImg,
    HomeImgEdit,
    GoodsHello,
    GoodsHelloEdit,
    Anquanqi,
    AnquanqiEdit,
    KuaiDi,
    KuaiDiEdit,
  },
  directives: {
    waves
  },
  data() {
    return {
      components: this.components,
      tableKey: 0,
      list: null,
      total: null,
      listLoading: true,
      loop: undefined,
      listQuery: {
        page: 1,
        limit: 10,
        importance: undefined,
        title: undefined,
        type: undefined,
        sort: '+id'
      },
      pageInfo: {
        id: '',
        pageName: '自定义页面'
      },
      temp: {},
      tempPageSize: 0,
      dateTemp: {},
      borderTemp: undefined,
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      rules: {},
      comList: [
        { id: 1, name: 'ScrollImg', label: '轮播', data: { imgs: [] }}, {
          id: 2, name: 'List', label: '文章列表', data: {}
        },
        { id: 3, name: 'Navigation', label: '企业导航页', data: {}},
        { id: 4, name: 'Dictionary', label: '字典页', data: {}},
        { id: 5, name: 'HuangLi', label: '黄历', data: {}},
        { id: 6, name: 'NameMatch', label: '姓名配对', data: {}},
        { id: 7, name: 'NameRate', label: '姓名评分', data: {}},
        { id: 8, name: 'CookBook', label: '家常菜', data: {}},
        {
          id: 9, name: 'CarKouBei', label: '汽车口碑', data: {
            param: {
              page: 1,
              p: '',
              b: '',
              gc: '',
              t: '',
              dt: '',
              d: '',
              f: '',
              m1: '',
              m2: '',
              s: '6'
            }
          }
        },
        { id: 10, name: 'HomeImg', label: '装修效果图', data: {}},
        { id: 11, name: 'GoodsHello', label: '祝福语大全', data: {}},
        { id: 12, name: 'Anquanqi', label: '安全期', data: {}},
        { id: 13, name: 'KuaiDi', label: '快递速查', data: {}}
      ],
      pageList: [
        {
          id: 1,
          name: 'Page',
          title: '空页面',
          iconName: 'homepage',
          pageName: '首页',
          shareTitle: '分享',
          pageTitle: '标题',
          comResult: []
        }
      ],
      pageListResult: [{
        id: 1,
        name: 'Page',
        title: '空页面',
        iconName: 'homepage',
        pageName: '首页',
        shareTitle: '分享',
        pageTitle: '标题',
        comResult: []
      }]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    modifyPageName(id, pageName) {
      updateById({ id: id, pageName: pageName }).then(() => {
        this.$notify({
          title: '成功',
          message: '更新成功',
          type: 'success',
          duration: 2000
        })
      })
    },
    modifyCustom(id, custom) {
      if (custom == 0) {
        custom = 1
      } else {
        custom = 0
      }
      updateById({ id: id, custom: custom }).then(() => {
        this.getList()
        this.$notify({
          title: '成功',
          message: '更新成功',
          type: 'success',
          duration: 2000
        })
      })
    },
    getList() {
      this.listLoading = true
      selectByPage(this.listQuery).then(data => {
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.limit = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },
    handleDelete(row, status) {
      deleteById({ id: row.id }).then(response => {
        this.list.splice(this.list.indexOf(row), 1)
        this.$message({
          message: '操作成功',
          type: 'success'
        })
      })
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
    },
    resetTemp() {
      this.dateTemp = {}
      this.pageListResult = [{
        id: 1,
        name: 'Page',
        title: '空页面',
        iconName: 'homepage',
        pageName: '首页',
        shareTitle: '分享',
        pageTitle: '标题',
        comResult: []
      }]
      this.temp = this.pageListResult[0]
      this.tempPageSize = 1
      // this.comResult = []
    },
    createData() {
      this.listLoading = true
      insert({
        ...this.pageInfo,
        content: JSON.stringify(this.pageListResult)
      }).then((id) => {
        this.getList()
        this.dialogFormVisible = false
        this.$notify({
          title: '成功',
          message: '创建成功',
          type: 'success',
          duration: 2000
        })
      })
    },
    handleUpdate(row) {
      this.pageListResult = JSON.parse(row.content)
      this.temp = this.pageListResult[0]
      this.dateTemp = this.temp
      this.tempPageSize = this.pageListResult.length
      this.pageInfo.id = row.id
      this.pageInfo.pageName = row.pageName
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
    },
    updateData() {
      this.listLoading = true
      updateById({ ...this.pageInfo, content: JSON.stringify(this.pageListResult) }).then(() => {
        this.getList()
        this.dialogFormVisible = false
        this.$notify({
          title: '成功',
          message: '更新成功',
          type: 'success',
          duration: 2000
        })
      })
    },
    updateComData(val) {
      this.dateTemp = val
    },
    comClick(n) {
      this.dateTemp = this.temp.comResult[n]
    },
    pageConfig(n) {
      this.temp = this.pageListResult[n]
      this.dateTemp = this.temp
    },
    comDbClick(n) {
      this.temp.comResult.splice(n, 1)
      this.dateTemp = this.temp
    },
    enter(n) {
      this.borderTemp = n
    },
    leave() {
      this.borderTemp = undefined
    },
    onEnd(evt) {
      // this.comResult[evt.newIndex] = JSON.parse(JSON.stringify(this.comResult[evt.newIndex]))
    },
    onPageStart(evt) {
      this.tempPageSize++
    },
    onPageRemove() {
      this.tempPageSize++
    },
    onPageEnd(evt) {
      this.tempPageSize--
      if (this.pageListResult.length === 1) {
        this.temp = this.pageListResult[0]
      }
    },
    pageClone(obj) {
      return JSON.parse(JSON.stringify(obj))
    },
    compClone(obj) {
      return JSON.parse(JSON.stringify(obj))
    }
  }
}
</script>
<style>
  .dottedLine {
    border: 1px dashed red;
  }

  .comResult {
    float: left;
    background: #fff;
  }

  .phoneView {
    height: 50vh;
    overflow-y: scroll;
  }

  .phoneView::-webkit-scrollbar {
    display: none;
  }

  .diaClass {
    min-height: 80vh;
  }

  .appMain {
    border-radius: 3px;
    box-shadow: 0 3px 10px #dcdcdc;
    border: 1px solid #ddd;
    background: #b3b3b3;
  }
</style>
