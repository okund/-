/**
 * 首页逻辑 - 学生/教师双角色
 */
import api from '../../api/index';

const app = getApp();

Page({
  data: {
    userInfo: {},
    stats: { dormTotal: 0, classTotal: 0, internTotal: 0, streakDays: 0 },
    banners: [
      { src: '/images/banner_1.png' },
      { src: '/images/banner_2.png' },
      { src: '/images/banner_3.png' }
    ],
    loading: true,
    role: 'student',
    connected: true
  },

  async onLoad() {
    this.initBanners();
    this.syncAppState();
  },

  async onShow() {
    this.syncAppState();
    await this.loadDashboardData();
  },

  /** 同步全局状态 */
  syncAppState() {
    const role = app.globalData.role || wx.getStorageSync('userRole') || 'student';
    this.setData({
      role,
      connected: app.globalData.connected
    });
  },

  /** 初始化轮播图 */
  initBanners() {
    // 通过接口向后端发送请求
    api.getBanners().then(paths => {
      // 如果返回的内容不为空 且长度大于0
      if (paths && paths.length > 0) {
        // 将后端返回值  赋值给 banners 变量
        this.setData({
          banners: paths.map(src => ({ src }))
        });
      }
      // 处理异常
    }).catch(() => {});
  },

  /** 从后端API加载首页数据 */
  async loadDashboardData() {
    this.setData({ loading: true });

    try {
      if (app.globalData.userInfo?.name) {
        this.setData({
          userInfo: app.globalData.userInfo,
          stats: app.globalData.checkinStats || this.data.stats
        });
      }

      const overview = await api.getDashboardOverview();
      if (overview) {
        this.setData({
          userInfo: overview.userInfo || this.data.userInfo,
          stats: overview.stats || this.data.stats
        });
        app.globalData.userInfo = overview.userInfo;
        app.globalData.checkinStats = overview.stats;
      }
    } catch (err) {
      console.warn('[首页] 加载数据失败:', err);
      this.setData({
        userInfo: app.globalData.userInfo || this.data.userInfo,
        stats: app.globalData.checkinStats || this.data.stats
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // ==================== 学生端 ====================

  goDormHistory() {
    wx.navigateTo({ url: '/pages/dorm-history/dorm-history' });
  },

  goClassHistory() {
    wx.navigateTo({ url: '/pages/course-history/course-history' });
  },

  goInternHistory() {
    wx.navigateTo({ url: '/pages/intern-history/intern-history' });
  },

  goDormCheckin() {
    wx.navigateTo({ url: '/pages/dorm/dorm' });
  },

  goClassCheckin() {
    wx.navigateTo({ url: '/pages/course/course' });
  },

  goInternCheckin() {
    wx.navigateTo({ url: '/pages/intern/intern' });
  },

  // ==================== 教师端 ====================

  goTeacherPanel() {
    wx.navigateTo({ url: '/pages/teacher/teacher' });
  },

  goStudents() {
    wx.navigateTo({ url: '/pages/teacher/students' });
  },

  goLeaveApprove() {
    wx.navigateTo({ url: '/pages/teacher/leave-approve' });
  },

  goDynamicCode() {
    wx.navigateTo({ url: '/pages/teacher/dynamic-code' });
  }
});
