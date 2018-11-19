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
      <el-table-column align="center" label="题目" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="答题" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.questions }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="正确答案" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.right }}</span>
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
          :rules="[{ required: true, message: '请输入题目', trigger: 'blur' }]"
          label="题目"
          prop="title">
          <el-input v-model="temp.title"/>
        </el-form-item>
        <el-form-item label="选项" prop="questions">
          <el-input v-for="(v,i) in options" v-model="options[i]" placeholder="请输入内容" class="input-with-select">
            <el-button slot="append" icon="el-icon-minus" @click="minusInput(i)"/>
            <el-button slot="append" icon="el-icon-plus" @click="addInput"/>
            <el-button
              v-if="temp.right===i"
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
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">{{ $t('table.confirm') }}</el-button>
        <el-button v-else type="primary" @click="updateData">{{ $t('table.confirm') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { selectByPage, insert, selectById, updateById, deleteById } from '@/api/busiQuestion'
import { getApps } from '@/api/busiArticle'
import waves from '@/directive/waves' // 水波纹指令
import { parseTime } from '@/utils'

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
        limit: 20,
        importance: undefined,
        title: undefined,
        type: undefined,
        sort: '+id'
      },
      temp: {},
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      rules: {
        questions: [
          { validator: checkOptions, message: '选项不能为空', trigger: 'blur' }
        ]
      },
      options: [
        '',
        '',
        '',
        ''
      ],
      apps: []
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
    getApps() {
      getApps().then(resp => {
        this.apps = resp.data
      })
    },
    rightKey(i) {
      this.temp.right = i
    },
    minusInput(i) {
      this.options.splice(i, 1)
    },
    addInput() {
      this.options.push('')
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
      this.temp = {
        questions: '',
        right: ''
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
