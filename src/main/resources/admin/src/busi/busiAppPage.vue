<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input @keyup.enter.native="handleFilter" style="width: 200px;" class="filter-item"
                :placeholder="$t('table.title')" v-model="listQuery.title">
      </el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">
        {{$t('table.search')}}
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px;" @click="handleCreate" type="primary"
                 icon="el-icon-edit">{{$t('table.add')}}
      </el-button>
    </div>

    <el-table :key='tableKey' :data="list" v-loading="listLoading" border fit highlight-current-row
              style="width: 100%;min-height:500px;">
      <el-table-column align="center" label="id" width="150">
        <template slot-scope="scope">
          <span>{{scope.row.id}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="页面名称" width="150">
        <template slot-scope="scope">
          <el-popover trigger="hover" placement="top">
            <el-input placeholder="页面名称" @blur="modifyPageName(scope.row.id,scope.row.pageName)"
                      v-model="scope.row.pageName"></el-input>
            <div slot="reference" class="name-wrapper">
              <el-tag size="medium">{{scope.row.pageName}}</el-tag>
            </div>
          </el-popover>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" fixed="right" width="150" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{$t('table.edit')}}</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row,'deleted')">{{$t('table.delete')}}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container" style="text-align: center">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                     :current-page="listQuery.page" :page-sizes="[10,20,30, 50]" :page-size="listQuery.limit"
                     layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="80vw" top="10vh"
               custom-class="diaClass">
      <el-row :gutter="20">
        <el-col :span="7">
          <el-card>
            <h3>页面</h3>
            <draggable v-model="pageList" :options="{group:{ name:'page',  pull:'clone', put:false}}" :clone="pageClone"
                       @start="onPageStart" @remove="onPageRemove" @end="onPageEnd"
                       style="width:100%;min-height: 150px;">
              <div v-for="element in pageList" :key="element.id" style="float: left;margin:3%;">
                <el-card style="width:80px;height: 80px" :body-style="{ padding: '0px' }">
                  <div style="text-align: center">
                    <i class="el-icon-edit" style="width: 40px;height: 40px;padding-top:20px;"></i>
                    <div style="padding-top: 10px;">
                      <span>{{element.title}}</span>
                    </div>
                  </div>
                </el-card>
              </div>
            </draggable>
            <h3>组件库</h3>
            <draggable v-model="comList" :options="{group:{ name:'compont',  pull:'clone', put:false}}"
                       :clone="compClone"
                       @start="drag=true" @end="onEnd" style="width:100%;min-height: 150px;">
              <div v-for="element in comList" :key="element.id" style="float: left;margin:3%;">
                <el-card style="width:80px;height: 80px" :body-style="{ padding: '0px' }">
                  <div style="text-align: center">
                    <i class="el-icon-edit" style="width: 40px;height: 40px;padding-top:20px;"></i>
                    <div style="padding-top: 10px;">
                      <span>{{element.name}}</span>
                    </div>
                  </div>
                </el-card>
              </div>
            </draggable>
            <div style="text-align: center">
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
              <h4 style="padding-top:40px;margin:1px;">{{temp.pageTitle}}</h4>
            </div>
            <draggable v-if="temp.comResult" v-model="temp.comResult" :options="{group:'compont'}" @start="drag=true"
                       @end="drag=false"
                       class="phoneView">
              <component v-for="(element,index) in temp.comResult" :key="index" :data="element.data"
                         v-bind:is="element.name" @click.native="comClick(index)" @dblclick.native="comDbClick(index)"
                         :class="index===borderTemp?'dottedLine comResult':'comResult'"
                         @mouseenter.native="enter(index)"
                         @mouseleave.native="leave()"
              ></component>
            </draggable>
            <div style="background: #fff;text-align: center;height:80px;">
              <draggable v-model="pageListResult" :options="{group:'page'}" @start="drag=true" @end="drag=false"
                         style="height:80px;">
                <div v-for="(element,index) in pageListResult"
                     :key="index" :style="{width:parseInt(100/tempPageSize)-1+'%',float:'left'}"
                     @click="pageConfig(index)">
                  <div style="text-align: center">
                    <img src="element.img" style="width: 40px;height: 40px;padding-top:10px;"/>
                    <div style="padding-top:5px;font-size: 4px;">
                      {{element.pageName||element.title}}
                    </div>
                  </div>
                </div>
              </draggable>
            </div>
          </div>
        </el-col>
        <el-col :span="7" :offset="1">
          <component v-if="dateTemp.name" :data="dateTemp" v-bind:is="dateTemp.name+'Edit'"
                     @update="updateComData"></component>
        </el-col>
      </el-row>
    </el-dialog>


  </div>
</template>

<script>
  import {selectByPage, insert, selectById, updateById, deleteById} from '@/api/busiAppPage'
  import waves from '@/directive/waves' // 水波纹指令
  import draggable from 'vuedraggable'
  import ScrollImg from './components/ScrollImg'
  import List from './components/List'
  import ListEdit from './components/ListEdit'
  import PageEdit from './components/PageEdit'
  import ScrollImgEdit from './components/ScrollImgEdit'
  import Navigation from './components/Navigation'
  import NavigationEdit from './components/NavigationEdit'
  import {parseTime} from '@/utils'

  export default {
    name: 'complexTable',
    components: {
      draggable,
      ScrollImg,
      ScrollImgEdit,
      List,
      ListEdit,
      PageEdit,
      Navigation,
      NavigationEdit
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
          limit: 20,
          importance: undefined,
          title: undefined,
          type: undefined,
          sort: '+id'
        },
        pageInfo: {
          id: '',
          pageName: '自定义页面',
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
          {id: 1, name: 'ScrollImg', data: {imgs: []}}, {
            id: 2, name: 'List', data: {}
          },
          {id: 3, name: 'Navigation', data: {}},
        ],
        pageList: [
          {id: 1, name: 'Page', title: '空页面', comResult: []},
        ],
        pageListResult: [],
      }
    },
    created() {
      this.getList()
    },
    methods: {
      modifyPageName(id, pageName) {
        updateById({id: id, pageName: pageName}).then(() => {
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
        selectByPage(this.listQuery).then(response => {
          this.list = response.data.list
          this.total = response.data.total
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
        deleteById({id: row.id}).then(response => {
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
        this.temp = {}
        this.dateTemp = {}
        this.pageListResult = []
        // this.comResult = []
      },
      createData() {
        this.listLoading = true
        insert({
          ...this.pageInfo,
          content: JSON.stringify(this.pageListResult)
        }).then((id) => {
          this.getList();
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
        updateById({...this.pageInfo, content: JSON.stringify(this.pageListResult)}).then(() => {
          this.getList();
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
        console.log(this.pageListResult)
      },
      comClick(n) {
        console.log(this.temp)
        console.log(this.dateTemp)
        this.dateTemp = this.temp.comResult[n]
      },
      pageConfig(n) {
        this.temp = this.pageListResult[n]
        this.dateTemp = this.temp
      },
      comDbClick(n) {
        this.pageListResult.comResult.splice(n, 1)
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
        console.log('pageClone')

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
    height: 80vh;
  }

  .appMain {
    border-radius: 3px;
    box-shadow: 0 3px 10px #dcdcdc;
    border: 1px solid #ddd;
    background: #b3b3b3;
  }
</style>
