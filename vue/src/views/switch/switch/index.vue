<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item label="设备名称" prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          placeholder="请输入设备名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择状态" clearable size="small">
          <el-option
            v-for="dict in switchStateOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="传感器" prop="sensor">
        <el-select v-model="queryParams.sensor" placeholder="请选择传感器" clearable size="small">
          <el-option
            v-for="dict in sensorOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['switch:switch:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['switch:switch:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['switch:switch:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['switch:switch:export']"
        >导出</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="switchList" @selection-change="handleSelectionChange" :row-class-name="switchDisable" border class="switch">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="设备ID" width="70" align="center" prop="deviceId" />
      <el-table-column label="APIKEY" align="center" prop="apiKey" />
      <el-table-column label="设备名称" align="center" prop="deviceName" />
      <el-table-column label="传感器" align="center" prop="sensor" :formatter="sensorFormat" />
      <el-table-column label="设备分类" align="center" prop="category" :formatter="categoryFormat" />
      <el-table-column label="更新时间" align="center" prop="updateTime" />
      <el-table-column label="状态" align="center" >
          <template slot-scope="scope">
            <el-tag v-if="scope.row.state=='0'" effect="plain" type="danger">离线</el-tag>
            <el-tag v-else-if="scope.row.state=='1'" effect="plain" type="success">在线</el-tag>
          </template>
      </el-table-column>
      <el-table-column label="控制" align="center">
        <template slot-scope="scope">
              <el-switch
                v-model="scope.row.switchA"
                active-color="#ff4949"
                active-value="1"
                inactive-value="0"
                :disabled="scope.row.state=='0'"
                @change="handleSetingSwitchState(scope.row)">
              </el-switch>
              <div v-if="scope.row.category=='double_switch'"> <!--双路继电器显示-->
              <el-switch
                  v-model="scope.row.switchB"
                  active-color="#ff4949"
                  active-value="1"
                  inactive-value="0"
                  :disabled="scope.row.state=='0'"
                  @change="handleSetingSwitchState(scope.row)"
                ></el-switch>
              </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['switch:switch:edit']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleMonitor(scope.row.deviceId)"
            v-hasPermi="['switch:switch:list']"
          >监测日志</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['switch:switch:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 查看开关监测数据 -->
    <el-dialog title="监测数据" :visible.sync="dialogMonitor" width="600px"  append-to-body>
      <el-table :data="switchLogList" border size="mini"  height="500">
        <el-table-column property="createTime" label="时间"  :formatter="getTime" align="center"></el-table-column>
        <el-table-column property="temperature" label="温度(℃)" align="center" width="100px"></el-table-column>
        <el-table-column property="humidity" label="湿度(%)" align="center" width="100px"></el-table-column>
        <el-table-column property="switchA" label="继电器A状态" align="center" width="100px">
          <template slot-scope="scope">
              <el-switch
                v-model="scope.row.switchA"
                active-color="#ff4949"
                disabled>
              </el-switch>
          </template>
        </el-table-column>
        <el-table-column property="switchB" label="继电器B状态" align="center" width="100px">
          <template slot-scope="scope">
              <el-switch
                v-model="scope.row.switchB"
                active-color="#ff4949"
                disabled>
              </el-switch>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogMonitor=false">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改智能开关对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">

        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>

        <el-form-item label="APIKEY" prop="apiKey">
          <el-input v-model="form.apiKey" placeholder="APIKEY" :disabled="true" />
        </el-form-item>

        <el-form-item label="传感器" prop="sensor">
          <el-select v-model="form.sensor" placeholder="请选择传感器">
            <el-option
              v-for="dict in sensorOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="设备分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择设备分类">
            <el-option
              v-for="dict in categoryOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="设备简介" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入设备简介" type="textarea" :rows="3"/>
        </el-form-item>

        <el-form-item label="创建时间" prop="createTime">
          <el-input v-model="form.userName" placeholder="创建时间" :disabled="true"/>
        </el-form-item>

        <el-form-item label="用户昵称" prop="userName">
          <el-input v-model="form.userName" placeholder="用户昵称" :disabled="true"/>
        </el-form-item>


      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSwitch, setSwitch,getSwitch,getMonitor, delSwitch, addSwitch, updateSwitch, exportSwitch } from "@/api/switch/switch";
import moment from 'moment';
moment.locale('zh-cn'); //设置语言 或 moment.lang('zh-cn'); 


export default {
  name: "KwSwitch",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 智能开关表格数据
      switchList: [],
      // 智能开关监测表格数据
      switchLogList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示监测日志弹出层
      dialogMonitor: false,
      // 传感器字典
      sensorOptions: [],
      // 设备分类字典
      categoryOptions: [],
      //开关状态
      switchStateOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceName: undefined,
        state: undefined,
        sensor: undefined,
        category: undefined,
        createTime: undefined,
        userId: undefined,
        userName: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        deviceName: [
          { required: true, message: "设备名称不能为空", trigger: "blur" }
        ],
        
        sensor: [
          { required: true, message: "传感器不能为空", trigger: "change" }
        ],
        category: [
          { required: true, message: "设备分类不能为空", trigger: "change" }
        ],
                remark: [
          { required: true, message: "备注不能为空", trigger: "blur" }
        ]
        
      }
    };
  },
  created() {
    this.getList();
    this.getDicts("sensor").then(response => {
      this.sensorOptions = response.data;
    });
    this.getDicts("switch_state").then(response => {
      this.switchStateOptions = response.data;
    });
    this.getDicts("device_category").then(response => {
      this.categoryOptions = response.data;
    });
  },
  methods: {
    /** 查询智能开关列表 */
    getList() {
      this.loading = true;
      listSwitch(this.queryParams).then(response => {
        this.switchList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 传感器字典翻译
    sensorFormat(row, column) {
      return this.selectDictLabel(this.sensorOptions, row.sensor);
    },
    // 设备分类字典翻译
    categoryFormat(row, column) {
      return this.selectDictLabel(this.categoryOptions, row.category);
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        deviceId: undefined,
        deviceName: undefined,
        monitorValue: undefined,
        apiKey: undefined,
        state: "0",
        sensorDictId: undefined,
        categoryDictId: undefined,
        createTime: undefined,
        userId: undefined,
        userName: undefined,
        remark: undefined,
        delFlg: undefined
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.deviceId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /**新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加智能开关";
    },
    /** 查看按钮操作 */
    handleUpdate(row) {
      const deviceId = row.deviceId || this.ids
      getSwitch(deviceId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改智能开关";
      });
    },
    /** 监测日志按钮操作 */
    handleMonitor(deviceId) {
      console.log(deviceId);
      getMonitor(deviceId).then(response => {
        this.switchLogList = response.rows;
        this.dialogMonitor = true;
      });
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.deviceId != undefined) {
            updateSwitch(this.form).then(response => {
              if (response.code === 200) {
                this.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.msgError(response.msg);
              }
            });
          } else {
            addSwitch(this.form).then(response => {
              if (response.code === 200) {
                this.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.msgError(response.msg);
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const deviceIds = row.deviceId || this.ids;
      this.$confirm('是否确认删除智能开关编号为"' + deviceIds + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delSwitch(deviceIds);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        }).catch(function() {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有智能开关数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportSwitch(queryParams);
        }).then(response => {
          this.download(response.msg);
        }).catch(function() {});
    },
    /**发送开关指令 */
    handleSetingSwitchState(row){
      this.msgSuccess("指令已发送");
      return setSwitch(row.deviceId,row.switchA,row.switchB);      
    },
    /**离线设置样式 */
    switchDisable({row, rowIndex}) {
            if (row.state =="0") {
              return 'switch-row-disable';
            } 
            return '';
    },
    getTime(row,column){
      var time=row[column.property];
      if(time==undefined) {return ''}
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    }
  }
};
</script>
<style>
.switch .el-switch__core{border-radius:2px;}
.switch .el-switch__core:after{border-radius:0;}
.el-table .switch-row-disable{ background:#f9d5d51f;}
</style>