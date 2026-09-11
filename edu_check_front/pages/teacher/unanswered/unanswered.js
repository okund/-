/**
 * 教师端 - 未命中问题日志
 */
import api from '../../../api/index';

const app = getApp();

Page({
  data: {
    list: [],
    loading: true
  },

  async onLoad() {
    await this.loadData();
  },

  async loadData() {
    this.setData({ loading: true });
    try {
      const result = await api.getUnansweredQuestions({ page: 1, size: 50 });
      this.setData({ list: result?.records || [] });
    } catch (err) {
      app.showToast('加载失败');
    } finally {
      this.setData({ loading: false });
    }
  }
});
