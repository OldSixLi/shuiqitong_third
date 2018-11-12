<!-- 页面描述 角色权限管理页面 -->
<template>
  <div>
    <h1 class="text-center">角色管理页面</h1>
    <el-popover placement="top" width="160" v-model="isShowPop" ref="delPop">
      <p class="text-center">确认要删除此角色吗?</p>
      <div style="text-align: right; margin: 0">
        <el-button size="mini" type="text" @click="cancelPopClick">取消</el-button>
        <el-button type="primary" size="mini" @click="confirmPopClick" :loading="confirmLoading">确定</el-button>
      </div>
      <span slot="reference"></span>
    </el-popover>
    <el-row :gutter="10">
      <el-col :span="9" :offset="2">
        <div class="text-right table-title">
          <span class="table-word">角色列表</span>
          <el-button type="primary" size="mini" icon="el-icon-plus" @click="addRoleClick">
            添加角色
          </el-button>
        </div>
        <el-table class="role-table" border ref="tableSet" highlight-current-row row-class-name="tableRowClassName"
          :maxHeight="availHeight()" :data="tableData" @current-change="handleRowChange">
          <el-table-column type="index" width="50"> </el-table-column>

          <el-table-column label="角色名称" prop="description"></el-table-column>

          <el-table-column width="160" label="修改时间" align="center">
            <template slot-scope="scope">
              {{scope.row.createTime|toTime}}
            </template>
</el-table-column>

<el-table-column label="操作" align="center">
  <template slot-scope="scope">
              <icon-btn circle word="修改角色" :disable="'sys'== scope.row.name ||'normal' == scope.row.name" :src="require('@/assets/img/edit.png')"
                @clicks="editRoleClick($event,scope.row)"></icon-btn>
              <icon-btn circle word="删除角色" :disable="'sys'== scope.row.name ||'normal' == scope.row.name" :src="require('@/assets/img/delete.png')"
                @clicks="deleteRole($event,scope.row)"></icon-btn>
            </template>
</el-table-column>
</el-table>
</el-col>
<el-col :span="9" :offset="2">
  <div class="table-title text-right" style="margin-top: 0">
    <span class="table-word">权限分配</span>

    <el-button size="mini" type="primary" icon="el-icon-check" :disabled="!currentRoleId" :loading="saveLoading" @click="getTreeData">
      保存
    </el-button>
  </div>
  <div>
    <div style="border:1px solid #ddd;padding: 10px"> 当前选中的角色为：<b>{{currentRoleName}}</b>

      <el-tree ref="dataTree" class="tree-block" :check-on-click-node="false" :expand-on-click-node="false" :data="data2" show-checkbox :default-expand-all="true" node-key="id" :default-checked-keys="checkedData" :props="defaultProps"></el-tree>
    </div>
  </div>
</el-col>
</el-row>
</div>
</template>
<script>
  import {
    mapGetters
  } from 'vuex';
  import {
    toNormalTime
  } from '@/util/helper.js';
  import {
    routeTree
  } from '@/router';
  import {
    onlySysPoints,
    baseRights
  } from "@/_config";
  import dom from '@/mixins/dom.js';

  export default {
    name: "Role",
    mixins: [dom],
    data() {
      return {
        tableData: [],
        checkedIds: [],
        currentPage1: 1,
        totalPage: 0,
        totalNum: 0,
        currentRoleId: "",
        isShowPop: !1,
        confirmLoading: !1,
        showEditLoading: !1,
        data2: [routeTree],
        defaultProps: {
          children: "children",
          label: "name"
        },
        checkedData: [...baseRights],
        currentRoleName: "",
        saveLoading: !1
      }
    },
    methods: {
      handleRowChange: function(e) {
        if (!e) return this.currentRoleName = "", void(this.currentRoleId = "");
        this.currentRoleName = e.description || "";
        var t = this.currentRoleId = e.id || "";
        if (!t) return !1;
        "sys" == e.name && 1 == t && this.setDisable(this.data2[0].children, !0), this.getRoleRights(t)
      },
      getRoleRights: function(e) {
        var t = this;
        this.$post("/tax/roleManage/getRoleRights", {
          roleId: e
        }).then(function(e) {
          e.success ? t.$refs.dataTree.setCheckedKeys(e.bean.concat(baseRights)) : t.$message.error(e.bean.message ||
            "操作失败,请重试！")
        }).catch(function(err) {
          console.log(err);
          t.$message.error("请求服务失败,请重试！")
        })
      },
      addRoleClick: function() {
        var e = this;
        this.$prompt("请输入要新增的角色名称", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          inputPlaceholder: "请输入新的角色名",
          inputPattern: /^[\s\S]*.*[^\s][\s\S]*$/,
          inputErrorMessage: "角色名称不能为空",
          beforeClose: function(t, n, a) {
            var o = n.inputValue;
            "confirm" === t ? o ? (n.confirmButtonLoading = !0, e.$post("/tax/roleManage/saveOrUpdateRole", {
              roleName: o
            }).then(function(t) {
              t.success ? (e.$message({
                message: "添加成功，请及时给新角色添加权限！",
                type: "success"
              }), e.getRoles(), a()) : (e.$message.error(t.message || "请求服务失败,请重试！"), a())
            }).catch(function() {
              e.$message.error("请求服务失败,请重试！"), a()
            }).finally(function() {
              n.confirmButtonLoading = !1, a()
            })) : e.$message({
              message: "角色名称不能为空",
              type: "error"
            }) : a()
          }
        }).catch(function() {})
      },
      setDisable: function(e, t) {
        var n = this;
        let tree = e;
        tree.forEach(child => {
          if (child.children) {
            n.setDisable(child.children, t);
          } else {
            if ((baseRights.indexOf(child.id) > -1 || onlySysPoints.indexOf(child.id) > -1)) {
              child.disabled = true;
            }
          }
        });
      },
      getTreeData: function() {
        var e = this;
        var t = this.$refs.dataTree.getCheckedKeys().join(","),
          n = this.currentRoleId;
        n || this.$message({
          message: "请选择角色后再进行操作",
          type: "info"
        }), this.saveLoading = !0, this.$post("/tax/roleManage/saveOrUpdateRoleRight", {
          roleId: n,
          rightNames: t
        }).then(function(t) {
          t.success ? e.$message({
            message: "操作成功!",
            type: "success"
          }) : e.$message.error(t.bean.message || "操作失败,请重试！")
        }).catch(function() {
          e.$message.error("请求服务失败,请重试！")
        }).finally(function() {
          e.saveLoading = !1
        })
      },
      editRoleClick: function(e, t) {
        var n = this,
          a = t.id;
        if (this.currentRoleId = a, "sys" == t.name || "normal" == t.name) return !1;
        this.$prompt("请输入新的角色名称", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          inputPlaceholder: "请输入新的角色名",
          inputValue: t.description,
          inputPattern: /^[\s\S]*.*[^\s][\s\S]*$/,
          inputErrorMessage: "角色名称不能为空",
          beforeClose: function(e, t, o) {
            var s = t.inputValue;
            "confirm" === e ? s ? (t.confirmButtonLoading = !0, n.$post("/tax/roleManage/saveOrUpdateRole", {
              roleId: a,
              roleName: s
            }).then(function(e) {
              e.success ? (n.$message({
                message: "角色名称修改成功！",
                type: "success"
              }), n.getRoles(), o()) : (n.$message.error(e.bean.message || "请求服务失败,请重试！"), o())
            }).catch(function() {
              n.$message.error("请求服务失败,请重试！"), o()
            }).finally(function() {
              t.confirmButtonLoading = !1, o()
            })) : n.$message({
              message: "角色名称不能为空",
              type: "error"
            }) : o()
          }
        }).catch(function() {})
      },
      deleteRole: function(e, t) {
        var n = this,
          a = e.target;
        this.currentRoleId = t.id, this.confirmLoading = !1, this.isShowPop = !0, this.$nextTick(function() {
          var e = n.$refs.delPop;
          e.popperJS ? (e.popperJS._reference = a, e.popperJS.state.position = e.popperJS._getPosition(e.popperJS
            ._popper, e.popperJS._reference), e.popperJS.update()) : console.error("popover实例未找到!")
        })
      },
      cancelPopClick: function() {
        this.isShowPop = !1
      },
      confirmPopClick: function() {
        var e = this,
          t = this.currentRoleId;
        this.confirmLoading = !0, this.$post("/tax/roleManage/deleteRole", {
          roleId: t
        }).then(function(t) {
          t.success ? (e.$message({
            message: "操作成功!",
            type: "success"
          }), e.currentRoleName = "", e.currentRoleId = "", e.getRoles()) : e.$message({
            message: t.message || "删除失败,请重试！",
            type: "error"
          })
        }).catch(function() {
          e.$message({
            message: "请求服务失败,请重试！",
            type: "error"
          })
        }).finally(function() {
          e.currentRoleId = "", e.isShowPop = !1, e.confirmLoading = !1
        })
      },
      getRoles: function() {
        var e = this;
        this.$get("/tax/roleManage/getRoleList").then(function(t) {
          t.success ? e.tableData = t.bean : e.$message.error(t.message || "操作失败,请重试！")
        }).catch(function() {
          e.$message.error("请求服务失败,请重试！")
        })
      },
      getAllAuth: function() {}
    },
    beforeRouteEnter: function(e, t, n) {
      n(function(e) {
        e.getRoles()
      })
    },
    created: function() {
      this.setDisable(this.data2[0].children, !0)
    },
    watch: {
      currentRoleId(e, t) {
        //设置基础权限始终被选中
        this.$refs.dataTree.setCheckedKeys(new Set([...e, ...baseRights]));
      }
    },
    //实时计算
    computed: {
      ...mapGetters([
        'userAuths'
      ])
    }
  }
</script>
<style scoped>
  .tree-block {
    margin: 10px 50px
  }
  
  .table-word {
    float: left;
    line-height: 30px;
    font-weight: 700
  }
</style>