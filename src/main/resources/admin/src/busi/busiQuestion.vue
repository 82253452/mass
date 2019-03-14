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
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="handleDeleteAll">全部删除
      </el-button>
      <div
        class="filter-item"
        style="margin-left: 10px;">
        <el-upload
          ref="upload"
          :headers="getToken()"
          :data="{appId:temp.appId}"
          :show-file-list="false"
          :before-upload="valiteData"
          :on-success="handleDataSuccess"
          action="busiQuestion/importQue">
          <el-select v-model="temp.appId" placeholder="Select">
            <el-option
              v-for="(item,v) in apps"
              :key="v"
              :label="item"
              :value="v"/>
          </el-select>
          <el-button type="primary">上传</el-button>
        </el-upload>
      </div>
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
      <el-table-column align="center" label="题目" width="200">
        <template slot-scope="scope">
          <el-tooltip
            v-if="scope.row.title"
            :content="scope.row.title"
            class="item"
            effect="dark"
            placement="top">
            <el-button>{{ scope.row.title.length<=8?scope.row.title:scope.row.title.substring(0,8)+'...' }}
            </el-button>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column align="center" label="类型" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.type===1?'单选题':scope.row.type===2?'多选题':'判断题' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="正确答案" width="200">
        <template slot-scope="scope">
          <el-tooltip
            :content="getContent(scope)"
            class="item"
            effect="dark"
            placement="top">
            <el-button>{{ getContent(scope,1) }}</el-button>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.status }}</span>
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

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left:50px;">
        <el-form-item
          :rules="[{ required: true, message: '选择类型', trigger: 'blur' }]"
          label="类型"
          prop="type">
          <el-radio-group v-model="temp.type">
            <el-radio-button label="1">单选题</el-radio-button>
            <el-radio-button label="2">多选题</el-radio-button>
            <el-radio-button label="3">判断题</el-radio-button>
          </el-radio-group>
          <!--<el-radio v-model="temp.type" label="1">单选题</el-radio>-->
          <!--<el-radio v-model="temp.type" label="2">多选题</el-radio>-->
          <!--<el-radio v-model="temp.type" label="3">判断题</el-radio>-->
        </el-form-item>
        <el-form-item
          :rules="[{ required: true, message: '请输入题目', trigger: 'blur' }]"
          label="题目"
          prop="title">
          <el-input v-model="temp.title">
            <el-button
              v-if="temp.type==3 && temp.answer==0"
              slot="append"
              icon="el-icon-close"
              style="color:green"
              @click="rightKey(1)"/>
            <el-button
              v-if="temp.type==3 && temp.answer==1"
              slot="append"
              style="color:green"
              icon="el-icon-check"
              @click="rightKey(0)"/>
          </el-input>
        </el-form-item>
        <el-form-item
          v-if="temp.type!=3"
          label="选项"
          prop="questions">
          <el-input v-for="(v,i) in options" v-model="options[i]" placeholder="请输入内容" class="input-with-select">
            <el-button slot="append" icon="el-icon-minus" @click="minusInput(i)"/>
            <el-button slot="append" icon="el-icon-plus" @click="addInput"/>
            <el-button
              v-if="temp.answer?temp.answer.toString().indexOf(i)!=-1:0"
              slot="append"
              icon="el-icon-circle-check"
              style="color:green"
              @click="rightKey(i)"/>
            <el-button v-else slot="append" icon="el-icon-check" @click="rightKey(i)"/>
          </el-input>
        </el-form-item>
        <el-form-item
          :rules="[{ required: true, message: '请选择公众号', trigger: 'blur' }]"
          label="公众号"
          prop="appId">
          <el-select v-model="temp.appId" placeholder="Select">
            <el-option
              v-for="(item,v) in apps"
              :key="item"
              :label="item"
              :value="v"/>
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">{{ $t('table.confirm') }}
        </el-button>
        <el-button v-else type="primary" @click="updateData">{{ $t('table.confirm') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { selectByPage, insert, selectById, updateById, deleteById ,deleteAll } from '@/api/busiQuestion'
import { getApps } from '@/api/busiArticle'
import waves from '@/directive/waves' // 水波纹指令
import { parseTime } from '@/utils'
import { getToken } from '@/utils/auth'
import { Loading } from 'element-ui'

export default {
  name: 'ComplexTable',
  directives: {
    waves
  },
  data() {
    const checkOptions = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('选项不能为空'))
      }
      const array = value.split('&')
      array.map(m => {
        if (!m) {
          return callback(new Error('选项不能为空'))
        }
      })
      callback()
    }
    return {
      tableKey: 0,
      list: null,
      total: null,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        importance: undefined,
        title: undefined,
        type: undefined,
        sort: '+id'
      },
      temp: {},
      dialogFormVisible: false,
      dialogStatus: '',
      listQue: ['A', 'B', 'C', 'D', 'E', 'F', 'G'],
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      rules: {
        questions: [
          { validator: checkOptions, message: '选项或答案不能为空', trigger: 'blur' }
        ]
      },
      options: [
        '',
        '',
        '',
        ''
      ],
      apps: [],
      loadingInstance: {}
    }
  },
  watch: {
    'options': {
      handler: function(newValue, oldValue) {
        this.temp.questions = newValue.join('&')
      },
      deep: true
    }
  },
  created() {
    this.getList()
    this.getApps()
  },
  methods: {
    getContent(scope, short) {
      const type = scope.row.type
      const answer = scope.row.answer
      if (type == 3) {
        if (answer == 1) {
          return '正确'
        }
        return '错误'
      }
      const ques = scope.row.questions
      let render = ''
      const queArray = ques.split('&')
      const n = answer.length
      for (let i = 0; i < n; i++) {
        render += queArray[answer.charAt(i)] + '-'
      }
      if (short) {
        return render.substring(0, render.length - 1).substring(0, 8) + '...'
      }
      return render.substring(0, render.length - 1)
    },
    valiteData() {
      if (!this.temp.appId) {
        this.$message({
          message: '请先选择小程序',
          type: 'success'
        })
        return false
      }
      this.loadingInstance = Loading.service({ fullscreen: true })
    },
    handleDataSuccess(resp) {
      if (resp && resp.code === 1000) {
        this.getList()
        this.$message({
          message: '导入成功',
          type: 'success'
        })
      } else {
        this.$message({
          message: '导入失败',
          type: 'success'
        })
      }
      this.loadingInstance.close()
    },
    getToken() {
      return { 'X-Token': getToken() }
    },
    getQueList(text) {
      let r = ''
      const array = text.split('&')
      if (!array) {
        return r
      }
      array.map(v => {
        r += v + '<br/>'
      })
      return r
    },
    getApps() {
      getApps({ type: 1 }).then(data => {
        this.apps = data
      })
    },
    rightKey(i) {
      if (this.temp.type == 2) {
        if (this.temp.answer.indexOf('' + i) != -1) {
          this.temp.answer = this.temp.answer.replace('' + i, '')
          return
        }
        this.temp.answer += '' + i
        return
      }
      this.temp.answer = '' + i
    },
    minusInput(i) {
      this.options.splice(i, 1)
    },
    addInput() {
      this.options.push('')
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
      deleteById({ id: row.id }).then(data => {
        this.list.splice(this.list.indexOf(row), 1)
        this.$message({
          message: '操作成功',
          type: 'success'
        })
      })
    },
    handleDeleteAll() {
      deleteAll().then(data => {
        this.list=[]
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
      this.temp = {
        questions: '',
        answer: '',
        type: 1
      }
    },
    createData() {
      console.log('add')
      this.$refs['dataForm'].validate((valid) => {
        console.log(222)
        console.log(valid)
        if (valid) {
          console.log('yes')
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
      this.options = this.temp.questions.split('&')
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
