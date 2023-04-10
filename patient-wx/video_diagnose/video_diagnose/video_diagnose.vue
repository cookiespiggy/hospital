<template>
	<view><trtc-room id="trtc-room" :config="trtcConfig"></trtc-room></view>
</template>

<script>
import dayjs from 'dayjs';
export default {
	data() {
		return {
			trtcConfig: {
				sdkAppID: this.tencent.trtc.appid,
				userID: null, // 用户唯一标识符
				userSig: null,
				template: 'grid' // 画面排版模式
			},
			roomId: null,
			expectEnd: null
		};
	},
	methods: {
    

	},
	onLoad: function(options) {
	    let that = this;
	    //获取URL传入的参数
	    that.roomId = options.roomId;
	    that.trtcConfig.userID = options.userId;
	    that.trtcConfig.userSig = options.userSig;
	    that.expectEnd = options.expectEnd;
	
	    //查找视图层的TRTC标签
	    let trtcRoomContext = that.selectComponent('#trtc-room');
	  
	    //创建定时器，用于到问诊结束的时候自动关闭TRTC视频通讯
	    let timer = setInterval(function() {
	        let now = new Date().getTime();
	        let expectEnd = dayjs(that.expectEnd).valueOf();
	        //判断是不是到了结束时间
	        if (now >= expectEnd) {
	            //结束TRTC通讯
	            that.trtcRoomContext.exitRoom()
	                .then(() => {
	                    console.log('退出问诊');
	                })
	                .catch(resp => {
	                    console.error('退出问诊失败:', resp);
	                });
	            uni.showToast({
	                icon: 'error',
	                title: '问诊结束'
	            });
	            //销毁定时器
	            clearInterval(timer);
	        }
	    }, 1000);
	
	    //设置屏幕常亮，不会锁屏休眠
	    uni.setKeepScreenOn({
	        keepScreenOn: true
	    });
	
	    
	    //获取TRTC事件常量合集
	    let EVENT = trtcRoomContext.EVENT;
	    //如果找到了视图层TRTC标签
	    if (trtcRoomContext) {
	        //监听是否进入TRTC的ROOM
	        trtcRoomContext.on(EVENT.LOCAL_JOIN, event => {
	            // 把本地音视频流向远端推送
	            trtcRoomContext.publishLocalVideo();
	            trtcRoomContext.publishLocalAudio();
	        });
	        // 监听远端推送来视频流
	        trtcRoomContext.on(EVENT.REMOTE_VIDEO_ADD, event => {
	            let userID = event.data.userID;
	            let streamType = event.data.streamType;
	            //订阅远端的视频流
	            trtcRoomContext.subscribeRemoteVideo({ userID: userID, streamType: streamType });
	        });
	        // 监听远端推送来音频流
	        trtcRoomContext.on(EVENT.REMOTE_AUDIO_ADD, event => {
	            let userID = event.data.userID;
	            //订阅远端的音频流
	            trtcRoomContext.subscribeRemoteAudio({ userID: userID });
	        });
	    }
	    //把初始好的TRTC对象保存到模型层
	    that.trtcRoomContext = trtcRoomContext;
	},


	onShow: function() {
	    let that = this;
	    uni.setKeepScreenOn({
	        keepScreenOn: true
	    });
	    that.$nextTick(function() {
	        that.trtcRoomContext.enterRoom({ roomID: that.roomId })
	            .then(() => {
	                console.log('成功进入问诊室');
	            })
	            .catch(res => {
	                console.error('进入问诊室失败:', res);
	            });
	    });
	}


};
</script>

<style lang="less">
@import url('video_diagnose.less');
</style>
