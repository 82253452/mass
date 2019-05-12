<template>
  <div class="app-container">
    <div class="filter-container">
      <!--<el-input-->
      <!--:placeholder="$t('table.title')"-->
      <!--v-model="listQuery.title"-->
      <!--style="width: 200px;"-->
      <!--class="filter-item"-->
      <!--@keyup.enter.native="handleFilter"/>-->
      <!--<el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">-->
      <!--{{ $t('table.search') }}-->
      <!--</el-button>-->
      <!--<el-button-->
      <!--class="filter-item"-->
      <!--style="margin-left: 10px;"-->
      <!--type="primary"-->
      <!--icon="el-icon-edit"-->
      <!--@click="handleCreate">{{ $t('table.add') }}-->
      <!--</el-button>-->
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="getAuthUrlInit">授权应用
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
      <el-table-column align="center" label="名称" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.nickName }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="简介" width="150">
        <template slot-scope="scope">
          <el-tooltip
            v-if="scope.row.signature"
            :content="scope.row.signature"
            class="item"
            effect="dark"
            placement="top">
            <el-button>{{ scope.row.signature.length<=8?scope.row.signature:scope.row.signature.substring(0,8)+'...'
            }}
            </el-button>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column align="center" label="头像" width="150">
        <template slot-scope="scope">
          <img :src="scope.row.headImg" width="50px" height="50px">
        </template>
      </el-table-column>
      <el-table-column v-if="checkPer(['weArticle','admin'])" align="center" label="推送文章状态" width="130">
        <template slot-scope="scope">
          <span>{{ scope.row.messageParam|getArticleStatus }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="类型" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.miniProgramInfo===1?'公众号':'小程序' }}</span>
        </template>
      </el-table-column>
      <!--<el-table-column align="center" label="模板" width="150">-->
      <!--<template slot-scope="scope">-->
      <!--<el-popover trigger="hover" placement="top">-->
      <!--<el-radio-group v-model="scope.row.pageId" @change="radioChange(scope.row.id,scope.row.pageId)">-->
      <!--<el-radio-button v-for="(p,v) in pages" :key="v" :label="v">{{ p }}</el-radio-button>-->
      <!--</el-radio-group>-->
      <!--<div slot="reference" class="name-wrapper">-->
      <!--<el-tag size="medium">{{ pages[scope.row.pageId]?pages[scope.row.pageId]:'无' }}</el-tag>-->
      <!--</div>-->
      <!--</el-popover>-->
      <!--</template>-->
      <!--</el-table-column>-->
      <el-table-column align="center" label="状态" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.status|getStatus }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" fixed="right" width="350" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-popover
            v-if="scope.row.miniProgramInfo===2 && (scope.row.status===2
              ||scope.row.status===3||scope.row.status===4
              ||scope.row.status===5||scope.row.status===6
            ||scope.row.status===7)"
            trigger="click"
            @show="getTestQrcode(scope.row.appId)"
          >
            <img :src="testCodeUrl" width="200px" height="200px">
            <el-button slot="reference" type="primary">预览</el-button>
          </el-popover>
          <el-button
            v-if="scope.row.miniProgramInfo===2 && (scope.row.status===1
              ||scope.row.status===4||scope.row.status===3||scope.row.status===5
              ||scope.row.status===6
            ||scope.row.status===7)"
            type="primary"
            @click="pushShow(scope.row.appId)">发版
          </el-button>
          <el-button
            v-if="scope.row.miniProgramInfo===2 && (scope.row.status===3||scope.row.status===6)"
            type="primary"
            @click="releaseWeapp(scope.row.appId)">发布
          </el-button>
          <el-button
            v-if="checkPer(['weArticle','admin'])&&scope.row.miniProgramInfo===1"
            type="primary"
            @click="autoMessageClick(scope.row)">
            {{ scope.row.autoMessage===0?'开启自动推送文章':'关闭自动推送文章' }}
          </el-button>
          <el-button
            v-if="checkPer(['question'])&&(scope.row.miniProgramInfo===1&&!scope.row.replay)"
            type="primary"
            @click="startReplay(scope.row.replay,scope.row.id)">开启答题回复
          </el-button>
          <el-button
            v-if="checkPer(['question'])&&(scope.row.replay===1||scope.row.replay===2)"
            type="primary"
            @click="startReplay(scope.row.replay,scope.row.id)">{{ scope.row.replay===1?'开启答题共享':'关闭答题' }}
          </el-button>
          <!--<el-button v-if="!status||status===0" type="primary" size="mini" @click="getAuthUrlInit">授权</el-button>-->
          <!--<el-button v-if="scope.row.status!=2" type="primary" size="mini" @click="generator(scope.row)">生成</el-button>-->
          <!--<a-->
          <!--v-if="scope.row.status==1"-->
          <!--:href="'https://dev.innter.fast4ward.cn/testApi/busiApp/downloadFile/?id='+scope.row.id"-->
          <!--download="weapp.zip">-->
          <!--<el-button type="primary" size="mini">-->
          <!--下载-->
          <!--</el-button>-->
          <!--</a>-->
          <!--<el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('table.edit') }}</el-button>-->
          <el-button size="mini" type="danger" @click="handleDelete(scope.row,'deleted')">{{
          $t('table.delete') }}
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
    <el-dialog :visible.sync="autoMessageShow" title="自动推送文章">
      <el-form
        ref="messageForm"
        :model="messageTemp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left:50px;">
        <!--<el-form-item label="类型"-->
        <!--prop="type"-->
        <!--:rules="[{ required: true, message: '类型不能为空', trigger: 'blur' }]">-->
        <!--<el-select-->
        <!--v-model="messageTemp.type"-->
        <!--placeholder="请选择类型"-->
        <!--@change="pushItemChange">-->
        <!--<el-option-->
        <!--v-for="(item,index) in messageTypes"-->
        <!--:key="index"-->
        <!--:value="item.type"-->
        <!--:label="item.name"-->
        <!--/>-->
        <!--</el-select>-->
        <!--</el-form-item>-->
        <el-form-item
          :rules="[{ required: true, message: '栏目不能为空', trigger: 'blur' }]"
          label="栏目"
          prop="column">
          <el-select
            v-model="messageTemp.column"
            placeholder="请选择栏目"
            @change="pushItemChange">
            <el-option
              v-for="(item,index) in columns"
              :key="index"
              :value="item"
              :label="getColumnsLabel(item)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="是否开启留言" label-width="100px">
          <el-switch
            v-model="messageTemp.comment"
            active-color="#13ce66"
            inactive-color="#ff4949"/>
        </el-form-item>
        <el-form-item
          :rules="[{ required: true, message: '时间不能为空', trigger: 'blur' }]"
          label="时间"
          prop="time">
          <el-time-picker
            v-model="messageTemp.time"
            :picker-options="{
              selectableRange: '00:00:00 - 23:59:59'
            }"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择时间"/>
        </el-form-item>
        <!--<el-form-item label="数量"-->
        <!--prop="num"-->
        <!--:rules="[{ required: true, message: '数量不能为空', trigger: 'blur' }]">-->
        <!--<el-input-number v-model="messageTemp.num" :min="1" :max="8" label="描述文字"/>-->
        <!--</el-form-item>-->
        <el-form-item
          :rules="[{ required: true, message: '图文列表不能为空', trigger: 'blur' }]"
          label="图文列表"
          prop="types">
          <el-tag
            v-for="(tag,i) in (messageTemp.types?messageTemp.types.split('-'):[])"
            :key="i"
            :type="tag==0?'info':tag==1?'warning':''"
            closable
            @close="removeTypes(i)">
            {{ tag==0?'视频':tag==1?'文章':'' }}
          </el-tag>
        </el-form-item>
        <el-button-group>
          <el-button v-show="(messageTemp.types?messageTemp.types.split('-'):[]).length<8" type="primary" icon="el-icon-plus" @click="addTypes(0)">视频</el-button>
          <el-button v-show="(messageTemp.types?messageTemp.types.split('-'):[]).length<8" type="primary" icon="el-icon-plus" @click="addTypes(1)">文章</el-button>
        </el-button-group>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="autoMessageButton">确认</el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="pushWeappShow" title="一键发版小程序">
      <el-form
        ref="pushForm"
        :model="pushTemp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left:50px;">
        <el-form-item
          :rules="[{ required: true, message: '标题', trigger: 'blur' }]"
          label="标题"
          prop="title">
          <el-input v-model="pushTemp.title"/>
        </el-form-item>
        <el-form-item
          :rules="[{ required: true, message: '标签', trigger: 'blur' }]"
          label="标签"
          prop="tag">
          <el-input v-model="pushTemp.tag"/>
        </el-form-item>
        <el-form-item
          :rules="[{ required: true, message: '模板', trigger: 'blur' }]"
          label="模板"
          prop="pageId">
          <el-radio-group v-model="pushTemp.pageId">
            <el-radio-button v-for="(p,v) in pages" :key="v" :label="v">{{ p }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="类目">
          <el-select
            v-model="itemIndex"
            placeholder="请选择类目"
            @change="pushItemChange">
            <el-option
              v-for="(item,index) in itemList"
              :key="index"
              :value="index"
              :label="item.firstClass+'-'+item.secondClass+'-'+item.thirdClass"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="pushWeapp">确认发版</el-button>
        <el-button v-if="isAdmin" type="primary" @click="onlyPushWeapp">只上传</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  selectByPage,
  insert,
  selectById,
  updateById,
  deleteById,
  Generator,
  Download,
  getAppPages,
  getAuthUrl,
  pushWeappByAppId,
  onlyPushWeapp,
  getItemList,
  releaseApp,
  autoMessageApi,
  closeMessageApi,
  getColumns
} from '@/api/busiApp'
import waves from '@/directive/waves' // 水波纹指令
import { parseTime } from '@/utils'
import VueQrcode from '@chenfengyuan/vue-qrcode'
import checkPermission from '@/utils/permission'

export default {
  name: 'ComplexTable',
  components: {
    VueQrcode
  },
  directives: {
    waves
  },
  filters: {
    getStatus: function(status) {
      if (status) {
        if (status === 1) {
          return '授权成功'
        }
        if (status === 2) {
          return '发版审核中'
        }
        if (status === 3) {
          return '审核通过'
        }
        if (status === 4) {
          return '审核不通过'
        }
        if (status === 5) {
          return '发布成功'
        }
        if (status === 6) {
          return '发布失败'
        }
        if (status === 7) {
          return '上传成功'
        }
        return '初始状态'
      }
      return '待授权'
    },
    getArticleStatus: function(param) {
      let text = ''
      if (param) {
        const jsonP = JSON.parse(param)
        if (jsonP.time) {
          text += '时间：' + parseTime(new Date(jsonP.time), ' {h}:{i}:{s}')
        }
        if (jsonP.num) {
          text += '数量：' + jsonP.num
        }
        return text
      }
      return '无'
    }
  },
  data() {
    return {
      tableKey: 0,
      list: null,
      total: null,
      listLoading: true,
      autoMessageShow: false,
      listQuery: {
        page: 1,
        limit: 10,
        importance: undefined,
        title: undefined,
        type: undefined,
        sort: '+id',
        miniType: 1
      },
      temp: {},
      testCodeUrl: '',
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      rules: {},
      pages: {},
      pushWeappShow: false,
      itemList: [],
      pushTemp: {},
      messageTemp: { type: 0, time: '00:00', num: 5, types: '' },
      messageTypes: [{ name: '视频', type: '0' }, { name: '文章', type: '1' }],
      itemIndex: '',
      isAdmin: this.checkPer(['admin']),
      columns: []
    }
  },
  created() {
    this.getList()
    this.getPages()
    this.getColumn()
  },
  methods: {
    removeTypes(index) {
      const typesArray = this.messageTemp.types.split('-')
      typesArray.splice(index, 1)
      this.messageTemp.types = typesArray.join('-')
    },
    addTypes(type) {
      if (this.messageTemp.types === '') {
        this.messageTemp.types = type + ''
      } else {
        this.messageTemp.types += '-' + type
      }
    },
    getColumnsLabel(v) {
      if (v) {
        if (v == 1) {
          return '1电竞游戏中心'
        } else if (v == 2) {
          return '2生活健康小常识'
        } else if (v == 3) {
          return '3石雕奇石'
        } else if (v == 4) {
          return '4周易国学家'
        } else if (v == 5) {
          return '5电动汽车报价大全'
        } else if (v == 6) {
          return '6SUV汽车大全'
        } else if (v == 7) {
          return '7古玩收藏交易古董鉴定'
        } else if (v == 8) {
          return '8佛心慧语精选'
        } else if (v == 9) {
          return '9传奇故事会'
        } else if (v == 10) {
          return '10广场舞教学合集'
        } else if (v == 11) {
          return '11搞笑相声小品大全'
        } else if (v == 12) {
          return '12健身视频集锦'
        } else if (v == 13) {
          return '13古筝名曲欣赏'
        }
        return v
      }
      return ''
    },
    getColumn() {
      getColumns().then(resp => {
        this.columns = resp.list
      })
    },
    autoMessageButton() {
      this.$refs['messageForm'].validate((valid) => {
        if (valid) {
          autoMessageApi(this.messageTemp).then(resp => {
            this.autoMessageShow = false
            this.getList()
          })
        }
      })
    },
    autoMessageClick(row) {
      const param = row.messageParam
      const appId = row.appId
      if (row.autoMessage === 1) {
        closeMessageApi(row).then(resp => {
          this.autoMessageShow = false
          this.getList()
          this.$message({
            message: '关闭成功',
            type: 'success'
          })
        })
        return
      }
      if (param) {
        this.messageTemp = JSON.parse(param)
        if (this.messageTemp.comment && this.messageTemp.comment == 'true') {
          this.messageTemp.comment = true
        }
        if (this.messageTemp.types == null) {
          this.$set(this.messageTemp, 'types', '')
        }
      } else {
        this.messageTemp = {
          appId: appId
        }
      }
      this.autoMessageShow = true
    },
    checkPer(role) {
      return checkPermission(role)
    },
    startReplay(status, id) {
      var param = { id: id }
      if (status === 0) {
        param.replay = 1
      } else if (status === 1) {
        param.replay = 2
      } else if (status === 2) {
        param.replay = 0
      }
      updateById(param).then(() => {
        this.getList()
        this.$notify({
          title: '成功',
          message: '更新成功',
          type: 'success',
          duration: 2000
        })
      })
    },
    pushShow(appId) {
      this.getItemListByAppId(appId)
      this.pushWeappShow = true
      this.pushTemp.appId = appId
    },
    pushItemChange(item) {
      this.pushTemp = { ...this.itemList[item], ...this.pushTemp }
    },
    getItemListByAppId(appId) {
      getItemList({ appId: appId }).then(data => {
        this.itemList = data.list
      })
    },
    getTestQrcode(appId) {
      this.testCodeUrl = process.env.BASE_API + 'common/getTestQrcode?appId=' + appId + '&uuid=' + Math.random()
    },
    pushWeapp() {
      this.$refs['pushForm'].validate((valid) => {
        if (valid) {
          pushWeappByAppId(this.pushTemp).then(resp => {
            this.pushWeappShow = false
            this.getList()
          })
        }
      })
    },
    onlyPushWeapp() {
      this.$refs['pushForm'].validate((valid) => {
        if (valid) {
          onlyPushWeapp(this.pushTemp).then(resp => {
            this.pushWeappShow = false
            this.getList()
          })
        }
      })
    },
    releaseWeapp(appId) {
      releaseApp({ appId: appId }).then(resp => {
        this.getList()
      })
    },
    getAuthUrlInit() {
      getAuthUrl().then(data => {
        window.open(data.url, '微信授权')
      })
    },
    radioChange(id, pageId) {
      updateById({ id: id, pageId: pageId }).then(() => {

      })
    },
    getPages() {
      getAppPages().then(data => {
        this.pages = data
      })
    },
    generator(row) {
      Generator({ id: row.id }).then(resp => {
        this.getList()
      })
    },
    download(row) {
      // Download({id: row.id}).then(resp => {
      //   console.log(resp)
      // })
      // window.open('http://localhost:8082/busiApp/downloadFile')
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
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    resetTemp() {
      this.temp = {}
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          insert(this.temp).then((id) => {
            this.getList()
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '创建成功',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row) // copy obj
      this.temp.timestamp = new Date(this.temp.timestamp)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          tempData.timestamp = +new Date(tempData.timestamp) // change Thu Nov 30 2017 16:41:05 GMT+0800 (CST) to 1512031311464
          updateById(tempData).then(() => {
            this.getList()
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '更新成功',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    }
  }
}
</script>
