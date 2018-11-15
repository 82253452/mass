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
      <el-table-column align="center" label="小程序" width="150">
        <template slot-scope="scope">
          <span>{{getAppName(scope.row.appId)}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="标题" width="150">
        <template slot-scope="scope">
          <span>{{scope.row.title}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="分类" width="150">
        <template slot-scope="scope">
          <span>{{scope.row.tag}}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="是否推荐" width="150">
        <template slot-scope="scope">
          <span>{{scope.row.recommend?'是':'否'}}</span>
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

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :model="temp" :rules="rules" class="form-container">
        <div class="createPost-main-container">
          <el-row>
            <el-col :span="24">
              <el-form-item style="margin-bottom: 40px;" prop="title"
                            :rules="[{ required: true, message: '请输入标题', trigger: 'blur' }]"
              >
                <MDinput v-model="temp.title" :maxlength="100" name="name" maxlength="20" required>
                  标题
                </MDinput>
              </el-form-item>

              <el-form-item style="margin-bottom: 40px;" label-width="60px" label="分类:" prop="tag"
                            :rules="[{ required: true, message: '请输入分类', trigger: 'blur' }]"
              >
                <el-input :rows="1" v-model="temp.tag" type="textarea" class="article-textarea" autosize
                          placeholder="请输入内容"/>
                <span v-show="contentShortLength" class="word-counter">{{ contentShortLength }}字以内</span>
              </el-form-item>

              <el-form-item style="margin-bottom: 40px;" label-width="60px" label="缩略图:" prop="img"
                            :rules="[{ required: true, message: '请上传缩略图', trigger: 'blur' }]"
              >
                <qiniuImage v-model="temp.img"></qiniuImage>
              </el-form-item>

              <div class="postInfo-container">
                <el-row>
                  <el-col :span="8">
                    <el-form-item label-width="80px" label="小程序:" class="postInfo-container-item" prop="appId"
                                  :rules="[{ required: true, message: '请选择小程序', trigger: 'blur' }]"
                    >
                      <el-select v-model="temp.appId" placeholder="Select">
                        <el-option
                          v-for="(item,v) in apps"
                          :key="item"
                          :label="item"
                          :value="v">
                        </el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label-width="80px" label="是否推荐:" class="postInfo-container-item">
                      <el-switch
                        v-model="temp.recommend"
                        active-color="#13ce66"
                        :active-value='1'
                        :inactive-value='0'>
                      </el-switch>
                    </el-form-item>
                  </el-col>

                  <el-col :span="8">
                    <el-form-item label-width="60px" label="权重:" class="postInfo-container-item">
                      <el-rate
                        v-model="temp.weight"
                        :max="10"
                        :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                        :low-threshold="1"
                        :high-threshold="10"
                        style="margin-top:8px;"/>
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>
            </el-col>
          </el-row>


          <div class="editor-container">
            <Tinymce ref="editor" :height="400" v-model="temp.content"/>
          </div>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{$t('table.cancel')}}</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">{{$t('table.confirm')}}</el-button>
        <el-button v-else type="primary" @click="updateData">{{$t('table.confirm')}}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {selectByPage, insert, selectById, updateById, deleteById, getApps} from '@/api/busiArticle'
  import waves from '@/directive/waves' // 水波纹指令
  import {parseTime} from '@/utils'
  import Tinymce from '@/components/Tinymce'
  import MDinput from '@/components/MDinput'
  import {validateURL} from '@/utils/validate'
  import {fetchArticle} from '@/api/article'
  import {userSearch} from '@/api/remoteSearch'
  import qiniuImage from './components/qiniuImage'


  export default {
    name: 'complexTable',
    components: {Tinymce, MDinput, qiniuImage},
    directives: {
      waves
    },
    data() {
      return {
        tableKey: 0,
        list: null,
        total: null,
        listLoading: true,
        contentShortLength: 6,
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
        rules: {},
        apps: {},
      }
    },
    created() {
      this.getList()
      this.getApps()
    },
    filters: {},
    methods: {
      getApps() {
        getApps().then(resp => {
          this.apps = resp.data
        })
      },
      getAppName: function (appId) {
        if (appId) {
          if (this.apps[appId]) {
            return this.apps[appId]
          }
        }
        return '无';
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
              this.getList();
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
            updateById(tempData).then(() => {
              this.getList();
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
      },
    }
  }
</script>
