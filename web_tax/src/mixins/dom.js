
import * as domTool from '@/util/dom.js'; 
export default {
  methods: {
    aaa() {
      return '1';
    },
    /**
     * 获取 滚动条到底部的距离
     * @returns 
     */
    getScrollBottomHeight() {
      return domTool.getScrollBottomHeight();
    },
    /**
     * 浏览器窗体的高度 
     * @returns 
     */
    getWindowHeight() {
      return domTool.getScrollBottomHeight();
    },
    /**
     * table组件的最大高度 
     * @returns 
     */
    availHeight() {
      return domTool.getWindowHeight() - 100;
    },
    tableRowClassName({
      row,
      rowIndex
    }) {
      row.index = rowIndex;
    }
  }
}
