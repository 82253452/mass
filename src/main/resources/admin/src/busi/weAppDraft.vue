<template>
  <div class="app-container">
    <el-table
      v-loading="listLoading"
      :key="tableKey"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%;min-height:500px;">
      <el-table-column align="center" label="draftId" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.draftId }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="templateId" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.templateId }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="版本" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.userVersion }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="描述" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.userDesc }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" fixed="right" width="150" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="updateData(scope.row)">添加到模板库</el-button>
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
  import {gettemplatedraftlist, addtotemplate, deleteTemplate} from '@/api/weApp'
  import {parseTime} from '@/utils'

  export default {
    name: 'ComplexTable',
    filters: {},
    data() {
      return {
        tableKey: 0,
        list: null,

        listQuery: {},
        temp: {}
      }
    },
    created() {
      this.getList()
    },
    methods: {
      getList() {
        this.listLoading = true
        gettemplatedraftlist().then(data => {
          this.list = data.templateDraftList
          this.listLoading = false
        })
      },
      handleDelete(row, status) {
        deleteTemplate({templateId: row.templateId}).then(data => {
          this.getList()
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 2000
          })
        })
      },

      resetTemp() {
        this.temp = {}
      },
      updateData(row) {
        addtotemplate({draftId: row.draftId}).then(() => {
          this.getList()
          this.$notify({
            title: '成功',
            message: '添加成功',
            type: 'success',
            duration: 2000
          })
        })
      }
    }
  }
</script>
