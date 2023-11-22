<template>
  <div>
    <!-- 服务列表 -->
    <div>
      <div>
        <span>远端服务列表:</span>
        <span style="float: right">
          <el-button @click="serverAddClick()" type="text">新增</el-button>
        </span>
      </div>
      <div>
        <el-table :data="serversData" border>
          <el-table-column prop="name" label="服务名称" width="240">
          </el-table-column>
          <el-table-column  label="接口URL">
            <template slot-scope="scope">
              <a target="_blank" :href="scope.row.url">{{ scope.row.url }}</a>
            </template>
          </el-table-column>
          <el-table-column label="Metrics指标">
            <template slot-scope="scope">
              <a target="_blank" :href="backendEndpoint + '/exporter/metrics?serverId=' + scope.row.id">{{ backendEndpoint }}/exporter/metrics?serverId={{ scope.row.id }}</a>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="280">
            <template slot-scope="scope">
              <el-button @click="preprocessViewClick(scope.row)" type="text" size="small">查看预处理</el-button>
              <el-button @click="serverViewClick(scope.row)" type="text" size="small">查看指标</el-button>
              <el-button @click="serverEditClick(scope.row)" type="text" size="small">编辑</el-button>
              <el-button @click="serverDelClick(scope.row)" type="text" size="small">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 新增Server表单 -->
    <div>
      <el-dialog title="新增Server" :visible.sync="newServerDialogVisible" center>
        <el-form :model="newServerFormData" label-width="200px">
          <el-form-item label="Server名称">
            <el-input v-model="newServerFormData.name"></el-input>
          </el-form-item>
          <el-form-item label="URL">
            <el-input v-model="newServerFormData.url"></el-input>
          </el-form-item>
          <el-form-item
            v-for="(header, index) in newServerFormData.headers"
            :label="'header' + index"
            :key="header.key"
            :prop="'headers.' + index + '.value'"
            :rules="{
              required: true, message: 'header不能为空', trigger: 'blur'
            }"
          >
            <el-input placeholder="Appect: Application/json" v-model="header.value"></el-input><el-button @click.prevent="removeHeaderAtNewServerForm(header)">删除</el-button>
          </el-form-item>
          <div class="preprocesses" style="margin-left: 50px;">
            <div>数据预处理</div>
            <div v-for="(preprocess, index) in newServerFormData.preprocesses" style="border-style: solid;">
              <el-form-item label="类型">
                <el-select v-model="preprocess.name" placeholder="请选择预处理类型">
                  <el-option label="xml转json" value="xmlconvert"></el-option>
                </el-select>
              </el-form-item>
              <el-button @click.prevent="removePreprocessAtNewServerForm(preprocess)">删除预处理步骤</el-button>
            </div>
          </div>
          <div class="metrics" style="margin-left: 50px;">
            <div>Metrics</div>
            <div v-for="(metric, index) in newServerFormData.metrics" style="border-style: solid;">
              <el-form-item label="指标名称">
                <el-input v-model="metric.name"></el-input>
              </el-form-item>
              <el-form-item>
                <template slot="label">
                  指标JSONPath
                  <el-tooltip content="在线调试" placement="top">
                    <a target="_blank" :href="JSONPathEvaluatorEndpoint"><i class="el-icon-question"></i></a>
                  </el-tooltip>
                </template>

                <el-input v-model="metric.path"></el-input>
              </el-form-item>
              <el-form-item label="指标帮助信息">
                <el-input v-model="metric.help"></el-input>
              </el-form-item>
              <el-form-item label="类型">
                <el-radio-group v-model="metric.type">
                  <el-radio label="value">简单对象</el-radio>
                  <el-radio label="object">复杂对象</el-radio>
                </el-radio-group>
              </el-form-item>
              <div class="labels" style="margin-left: 50px;">
                <div>Labels</div>
                <div v-for="(label, labelIndex) in metric.labels" style="border-style: solid;">
                  <el-form-item label="标签名称">
                    <el-input v-model="label.name"></el-input>
                  </el-form-item>
                  <el-form-item label="标签JSONPath">
                    <el-input v-model="label.path"></el-input>
                  </el-form-item>
                  <el-button @click.prevent="removeMetricLabelAtNewServerForm(metric, label)">删除Label</el-button>
                </div>
              </div>
              <div class="values" v-if="metric.type == 'object'" style="margin-left: 50px;">
                <div>Values</div>
                <div v-for="(value, labelIndex) in metric.values" style="border-style: solid;">
                  <el-form-item label="对象类型指标名称">
                    <el-input v-model="value.name"></el-input>
                  </el-form-item>
                  <el-form-item label="对象类型指标JSONPath">
                    <el-input v-model="value.path"></el-input>
                  </el-form-item>
                  <el-button @click.prevent="removeMetricValueAtNewServerForm(metric, value)">删除Value</el-button>
                </div>
              </div>

              <el-button @click.prevent="addMetricLabelAtNewServerForm(metric)">新增Label</el-button>
              <el-button v-if="metric.type == 'object'" @click.prevent="addMetricValueAtNewServerForm(metric)">
                新增Value
              </el-button>
              <el-button @click.prevent="removeMetricAtNewServerForm(metric)">删除Metric</el-button>
            </div>
          </div>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="addHeaderClick">新增自定义请求头</el-button>
          <el-button @click="addPreprocessClick">新增预处理步骤</el-button>
          <el-button @click="addServerMetricClick">新增Metric</el-button>
          <el-button @click="newServerFormCancel">取 消</el-button>
          <el-button type="primary" @click="newServerFormSubmit">确 定</el-button>
        </div>
      </el-dialog>
    </div>

    <!-- 修改Server表单 -->
    <div>
      <el-dialog title="修改Server" :visible.sync="editServerDialogVisible" center>
        <el-form :model="editServerFormData" label-width="200px">
          <el-form-item label="Server名称">
            <el-input v-model="editServerFormData.name"></el-input>
          </el-form-item>
          <el-form-item label="URL">
            <el-input v-model="editServerFormData.url"></el-input>
          </el-form-item>
          <el-form-item
            v-for="(header, index) in editServerFormData.headers"
            :label="'header' + index"
            :key="header.key"
            :prop="'headers.' + index + '.value'"
            :rules="{
              required: true, message: 'header不能为空', trigger: 'blur'
            }"
          >
            <el-input placeholder="Appect: Application/json" v-model="header.value"></el-input><el-button @click.prevent="removeHeaderAtEditServerForm(header)">删除</el-button>
          </el-form-item>
          <div class="preprocesses" style="margin-left: 50px;">
            <div>数据预处理</div>
            <div v-for="(preprocess, index) in editServerFormData.preprocesses" style="border-style: solid;">
              <el-form-item label="类型">
                <el-select v-model="preprocess.name" placeholder="请选择预处理类型">
                  <el-option label="xml转json" value="xmlconvert"></el-option>
                </el-select>
              </el-form-item>
              <el-button @click.prevent="removePreprocessAtEditServerForm(preprocess)">删除预处理步骤</el-button>
            </div>
          </div>
          <div class="metrics" style="margin-left: 50px;">
            <div>Metrics</div>
            <div v-for="(metric, index) in editServerFormData.metrics" style="border-style: solid;">
              <el-form-item label="指标名称">
                <el-input v-model="metric.name"></el-input>
              </el-form-item>
              <el-form-item label="指标JSONPath">
                <el-input v-model="metric.path"></el-input>
              </el-form-item>
              <el-form-item label="指标帮助信息">
                <el-input v-model="metric.help"></el-input>
              </el-form-item>
              <el-form-item label="类型">
                <el-radio-group v-model="metric.type">
                  <el-radio label="value">简单对象</el-radio>
                  <el-radio label="object">复杂对象</el-radio>
                </el-radio-group>
              </el-form-item>
              <div class="labels" style="margin-left: 50px;">
                <div>Labels</div>
                <div v-for="(label, labelIndex) in metric.labels" style="border-style: solid;">
                  <el-form-item label="标签名称">
                    <el-input v-model="label.name"></el-input>
                  </el-form-item>
                  <el-form-item label="标签JSONPath">
                    <el-input v-model="label.path"></el-input>
                  </el-form-item>
                  <el-button @click.prevent="removeMetricLabelAtEditServerForm(metric, label)">删除Label</el-button>
                </div>
              </div>
              <div class="values" v-if="metric.type == 'object'" style="margin-left: 50px;">
                <div>Values</div>
                <div v-for="(value, labelIndex) in metric.values" style="border-style: solid;">
                  <el-form-item label="对象类型指标名称">
                    <el-input v-model="value.name"></el-input>
                  </el-form-item>
                  <el-form-item label="对象类型指标JSONPath">
                    <el-input v-model="value.path"></el-input>
                  </el-form-item>
                  <el-button @click.prevent="removeMetricValueAtEditServerForm(metric, value)">删除Value</el-button>
                </div>
              </div>

              <el-button @click.prevent="addMetricLabelAtEditServerForm(metric)">新增Label</el-button>
              <el-button v-if="metric.type == 'object'" @click.prevent="addMetricValueAtEditServerForm(metric)">
                新增Value
              </el-button>
              <el-button @click.prevent="removeMetricAtEditServerForm(metric)">删除Metric</el-button>
            </div>
          </div>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="addHeaderAtEditServerFormClick">新增自定义请求头</el-button>
          <el-button @click="addPreprocessAtEditServerFormClick">新增预处理步骤</el-button>
          <el-button @click="addServerMetricAtEditServerFormClick">新增Metric</el-button>
          <el-button @click="editServerFormCancel">取 消</el-button>
          <el-button type="primary" @click="editServerFormSubmit">确 定</el-button>
        </div>
      </el-dialog>
    </div>

    <!-- 查看预处理表单 -->
    <div>
      <el-dialog title="预处理步骤" :visible.sync="preprocessDialogVisible" center>
        <el-collapse v-for="(preprocess, index) in preprocessDialogData">
          <el-collapse-item :title="preprocess.name" :name="index">
            <div>
              <el-descriptions class="margin-top" :column="1" size="medium" border>
                <el-descriptions-item>
                  <template slot="label">
                    <i class="el-icon-location-outline"></i>
                    步骤
                  </template>
                  {{ preprocess.name }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-collapse-item>
        </el-collapse>
        <div slot="footer" class="dialog-footer">
          <el-button @click="preprocessDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="preprocessDialogVisible = false">确 定</el-button>
        </div>
      </el-dialog>
    </div>

    <!-- 查看Server表单 -->
    <div>
      <el-dialog title="Metrics" :visible.sync="metricsDialogVisible" center>
        <el-collapse v-for="(metric, index) in metricsDialogData">
          <el-collapse-item :title="metric.name" :name="index">
            <div>
              <el-descriptions class="margin-top" :column="1" size="medium" border>
                <el-descriptions-item>
                  <template slot="label">
                    <i class="el-icon-location-outline"></i>
                    指标帮助信息
                  </template>
                  {{ metric.help }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    <i class="el-icon-mobile-phone"></i>
                    JSONPath
                  </template>
                  {{ metric.path }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    <i class="el-icon-user"></i>
                    JSONPath目标类型
                  </template>
                  {{ metric.type }}
                </el-descriptions-item>
                <el-descriptions-item v-if="metric.type == 'object'">
                  <template slot="label">
                    <i class="el-icon-office-building"></i>
                    值列表
                  </template>
                  <div v-for="value in metric.values">
                    <span>{{ value.name }}: {{ value.path }}</span>
                  </div>
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    <i class="el-icon-office-building"></i>
                    标签列表
                  </template>
                  <div v-for="label in metric.labels">
                    <span>{{ label.name }}: {{ label.path }}</span>
                  </div>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-collapse-item>
        </el-collapse>
        <div slot="footer" class="dialog-footer">
          <el-button @click="metricsDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="metricsDialogVisible = false">确 定</el-button>
        </div>
      </el-dialog>
    </div>

  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'app',
  mounted() {
    this.getServerList();
  },
  data() {
    return {
      backendEndpoint: process.env.VUE_APP_BASE_URL,
      JSONPathEvaluatorEndpoint: '/jsonpath/index.html',
      newServerDialogVisible: false,
      newServerFormData: {
        headers: [],
        preprocesses: [],
        metrics: [],
      },
      preprocessDialogVisible: false,
      preprocessDialogData: [],
      metricsDialogVisible: false,
      metricsDialogData: [],
      serversData: [],
      editServerDialogVisible: false,
      editServerFormData: {
        headers: [],
        preprocesses: [],
        metrics: [],
      }
    }
  },
  methods: {
    getServerList() {
      let that = this;
      axios.get( this.backendEndpoint + "/exporter/server").then(function(response) {
        console.log(response);
        that.serversData = response.data;
      }).catch(function(error) {
        console.log(error);
      })
    },
    // 新增表单方法 //
    serverAddClick() {
      this.newServerDialogVisible = true;
    },
    addHeaderClick() {
      this.newServerFormData.headers.push({
        value: '',
        key: Date.now()
      });
      this.$forceUpdate();
    },
    removeHeaderAtNewServerForm(item) {
      var index = this.newServerFormData.headers.indexOf(item)
      if (index !== -1) {
        this.newServerFormData.headers.splice(index, 1)
      }
      this.$forceUpdate();
    },
    addPreprocessClick() {
      this.newServerFormData.preprocesses.push({name: 'xmlconvert'});
      this.$forceUpdate();
    },
    removePreprocessAtNewServerForm(item) {
      let index = this.newServerFormData.preprocesses.indexOf(item);
      if (index !== -1){
        this.newServerFormData.preprocesses.splice(index, 1);
      }
      this.$forceUpdate();
    },
    addServerMetricClick() {
      this.newServerFormData.metrics.push({type: "value", labels: [], values: []});
      this.$forceUpdate();
    },
    removeMetricAtNewServerForm(item) {
      let index = this.newServerFormData.metrics.indexOf(item);
      if (index !== -1) {
        this.newServerFormData.metrics.splice(index, 1);
      }
      this.$forceUpdate();
    },
    addMetricLabelAtNewServerForm(item) {
      let index = this.newServerFormData.metrics.indexOf(item);
      if (index !== -1) {
        if (this.newServerFormData.metrics[index].labels != null) {
          this.newServerFormData.metrics[index].labels.push({})
        } else {
          this.newServerFormData.metrics[index].labels = [];
          this.newServerFormData.metrics[index].labels.push({})
        }
      }
      this.$forceUpdate();
    },
    removeMetricLabelAtNewServerForm(metric, label) {
      let metricIndex = this.newServerFormData.metrics.indexOf(metric);
      let labelIndex = -1;
      if (metricIndex !== -1) {
        labelIndex = this.newServerFormData.metrics[metricIndex].labels.indexOf(label);
      }
      if (labelIndex !== -1) {
        this.newServerFormData.metrics[metricIndex].labels.splice(labelIndex, 1);
      }
      this.$forceUpdate();
    },
    addMetricValueAtNewServerForm(item) {
      let index = this.newServerFormData.metrics.indexOf(item);
      if (index !== -1) {
        if (this.newServerFormData.metrics[index].values != null) {
          this.newServerFormData.metrics[index].values.push({})
        } else {
          this.newServerFormData.metrics[index].values = [];
          this.newServerFormData.metrics[index].values.push({})
        }
      }
      this.$forceUpdate();
    },
    removeMetricValueAtNewServerForm(metric, value) {
      let metricIndex = this.newServerFormData.metrics.indexOf(metric);
      let valueIndex = -1;
      if (metricIndex !== -1) {
        valueIndex = this.newServerFormData.metrics[metricIndex].values.indexOf(value);
      }
      if (valueIndex !== -1) {
        this.newServerFormData.metrics[metricIndex].values.splice(valueIndex, 1);
      }
      this.$forceUpdate();
    },
    newServerFormCancel() {
      this.newServerDialogVisible = false;
      this.newServerFormData = {metrics: []};
    },
    newServerFormSubmit() {
      console.log(this.newServerFormData);
      let that = this;
      axios.post(this.backendEndpoint + "/exporter/server", this.newServerFormData).then(function(response) {
        console.log(response);
        that.success("新增成功");
        that.getServerList();
      }).catch(function(error) {
        console.log(error);
        that.failed("新增失败")
        that.getServerList();
      })
      // reset form data
      this.newServerDialogVisible = false;
      this.newServerFormData = {metrics: []};
    },
    // 修改表单方法 //
    serverEditClick(server) {
      this.editServerFormData = server;
      this.editServerDialogVisible = true;
    },
    addHeaderAtEditServerFormClick() {
      console.log("##########")
      console.log(this.editServerFormData)
      this.editServerFormData.headers.push({
        value: '',
        key: Date.now()
      });
      this.$forceUpdate();
    },
    removeHeaderAtEditServerForm(item) {
      var index = this.editServerFormData.headers.indexOf(item)
      if (index !== -1) {
        this.editServerFormData.headers.splice(index, 1)
      }
    },
    addPreprocessAtEditServerFormClick() {
      this.editServerFormData.preprocesses.push({name: "xmlconvert"});
      this.$forceUpdate();
    },
    removePreprocessAtEditServerForm(item) {
      let index = this.editServerFormData.preprocesses.indexOf(item);
      if (index !== -1) {
        this.editServerFormData.preprocesses.splice(index, 1);
      }
    },
    addServerMetricAtEditServerFormClick() {
      this.editServerFormData.metrics.push({type: "value", labels: [], values: []});
      this.$forceUpdate();
    },
    removeMetricAtEditServerForm(item) {
      let index = this.editServerFormData.metrics.indexOf(item);
      if (index !== -1) {
        this.editServerFormData.metrics.splice(index, 1);
      }
      this.$forceUpdate();
    },
    addMetricLabelAtEditServerForm(item) {
      let index = this.editServerFormData.metrics.indexOf(item);
      if (index !== -1) {
        if (this.editServerFormData.metrics[index].labels != null) {
          this.editServerFormData.metrics[index].labels.push({})
        } else {
          this.editServerFormData.metrics[index].labels = [];
          this.editServerFormData.metrics[index].labels.push({})
        }
      }
      this.$forceUpdate();
    },
    removeMetricLabelAtEditServerForm(metric, label) {
      let metricIndex = this.editServerFormData.metrics.indexOf(metric);
      let labelIndex = -1;
      if (metricIndex !== -1) {
        labelIndex = this.editServerFormData.metrics[metricIndex].labels.indexOf(label);
      }
      if (labelIndex !== -1) {
        this.editServerFormData.metrics[metricIndex].labels.splice(labelIndex, 1);
      }
      this.$forceUpdate();
    },
    addMetricValueAtEditServerForm(item) {
      let index = this.editServerFormData.metrics.indexOf(item);
      if (index !== -1) {
        if (this.editServerFormData.metrics[index].values != null) {
          this.editServerFormData.metrics[index].values.push({})
        } else {
          this.editServerFormData.metrics[index].values = [];
          this.editServerFormData.metrics[index].values.push({})
        }
      }
      this.$forceUpdate();
    },
    removeMetricValueAtEditServerForm(metric, value) {
      let metricIndex = this.editServerFormData.metrics.indexOf(metric);
      let valueIndex = -1;
      if (metricIndex !== -1) {
        valueIndex = this.editServerFormData.metrics[metricIndex].values.indexOf(value);
      }
      if (valueIndex !== -1) {
        this.editServerFormData.metrics[metricIndex].values.splice(valueIndex, 1);
      }
      this.$forceUpdate();
    },
    editServerFormCancel() {
      this.editServerDialogVisible = false;
      this.editServerFormData = {metrics: []};
    },
    editServerFormSubmit() {
      console.log(this.editServerFormData);
      let that = this;
      axios.put(this.backendEndpoint + "/exporter/server/" + this.editServerFormData.id , this.editServerFormData).then(function(response) {
        console.log(response);
        that.success("修改成功");
        that.getServerList();
      }).catch(function(error) {
        console.log(error);
        that.failed("修改失败")
        that.getServerList();
      })
      // reset form data
      this.editServerDialogVisible = false;
      this.editServerFormData = {metrics: []};
    },
    // 查看方法 //
    preprocessViewClick(server) {
      this.preprocessDialogData = server.preprocesses;
      this.preprocessDialogVisible = true;
    },
    serverViewClick(server) {
      this.metricsDialogData = server.metrics;
      this.metricsDialogVisible = true;
    },
    // 删除方法 //
    serverDelClick(server) {
      let that = this;
      axios.delete(this.backendEndpoint + "/exporter/server/" + server.id).then(function(response) {
        console.log(response);
        that.success("删除成功");
        that.getServerList();
      }).catch(function(error) {
        console.log(error);
        that.failed("删除失败")
        that.getServerList();
      })
    },
    // 通知 //
    success(message) {
      this.$message({
        message: message,
        type: 'success'
      });
    },
    failed(message) {
      this.$message.error(message);
    }
  }
}
</script>

<style lang="scss">
* {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}
</style>
