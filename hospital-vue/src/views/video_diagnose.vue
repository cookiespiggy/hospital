<template>
	<div v-if="isAuth(['ROOT', 'VIDEO_DIAGNOSE:DIAGNOSE'])">
		<div class="main">
			<div id="remoteVideo">
				<div v-show="!showRemoteVideo">
					<img src="../assets/trtc/camera.png" class="camera" />
					<p>患者摄像头画面</p>
				</div>
			</div>
			<div class="sidebar">
				<div id="localVideo">
					<div v-show="!showLocalVideo">
						<img src="../assets/trtc/camera.png" class="camera" />
						<p>本地摄像头画面</p>
					</div>
				</div>
				<div class="operate">
					<input
						type="button"
						:class="status == 'offline' ? 'btn primary' : 'btn error'"
						:value="status == 'offline' ? '我要上线' : '立即下线'"
						@click="onlineOrOfflineHandle"
					/>
					<input
						type="button"
						:class="open ? 'btn info' : 'btn success'"
						:value="open ? '关闭挂号' : '开放挂号'"
						@click="openOrCloseHandle"
					/>
				</div>
				<div class="current-order">
					<h3>
						<SvgIcon name="camera_fill" class="icon-svg camera" />
						当前视频问诊
					</h3>
					<div v-show="currentInfo.diagnoseId != null">
						<div class="info">
							<el-avatar shape="square" :size="45" :src="currentInfo.photo" />
							<ul>
								<li class="name">{{ currentInfo.name }}</li>
								<li class="tel">{{ currentInfo.tel }}</li>
							</ul>
						</div>
						<div class="timer-container">
							<span class="desc">
								距离
								<br />
								{{ { '1': '开始', '2': '结束' }[currentStatus + ''] }}
							</span>
							<div class="timer">{{ countDownCard }}</div>
							<div class="clock">
								<el-icon :size="20" color="#777"><AlarmClock /></el-icon>
							</div>
						</div>
					</div>
					<div class="empty" v-show="currentInfo.diagnoseId == null">
						<el-empty description="无人问诊" image-size="55" />
					</div>
				</div>
				<div class="next-order">
					<h3>
						<SvgIcon name="camera_fill" class="icon-svg camera" />
						排队视频问诊
					</h3>
					<div v-show="nextInfo.diagnoseId != null">
						<div class="info">
							<el-avatar shape="square" :size="45" :src="nextInfo.photo" />
							<ul>
								<li class="name">{{ nextInfo.name }}</li>
								<li class="tel">{{ nextInfo.tel }}</li>
							</ul>
						</div>
						<div class="time-range">
							<el-icon><Clock /></el-icon>
							<span>起始时间： {{ nextInfo.expectStart }} ~ {{ nextInfo.expectEnd }}</span>
						</div>
					</div>
					<div class="empty" v-show="nextInfo.diagnoseId == null">
						<el-empty description="无人问诊" image-size="55" />
					</div>
				</div>
			</div>
			<div class="data-container">
				<el-table
					:data="tableData"
					stripe
					border
					style="width: 100%"
					:header-cell-style="{ background: '#409eff', color: '#fff' }"
				>
					<el-table-column type="index" header-align="center" align="center" width="100" label="序号">
						<template #default="scope">
							<span>{{ scope.$index + 1 }}</span>
						</template>
					</el-table-column>
					<el-table-column prop="date" label="日期" header-align="center" align="center" />
					<el-table-column prop="count" label="问诊人数" header-align="center" align="center" />
				</el-table>
				<div id="chart"></div>
			</div>
		</div>
		<div class="images">
			<el-scrollbar>
				<div style="display: flex; height: 120px;">
					<el-image
						style="width: 100px; height: 100px;margin-right: 10px;flex-shrink: 0;flex-grow: 0;"
						v-for="one in imgList"
						:src="one"
						:preview-src-list="imgList"
						:initial-index="0"
						fit="cover"
					/>
				</div>
			</el-scrollbar>
		</div>
	</div>
</template>

<script>
import $ from 'jquery';
import TRTC from 'trtc-js-sdk';
import * as echarts from 'echarts';
import * as dayjs from 'dayjs';

export default {
	data() {
		return {
			status: 'offline',
			open: false,
			showLocalVideo: false,
			showRemoteVideo: false,
			appId: null,
			userSign: null,
			userId: null,
			roomId: null,
			client: null,
			localStream: null,
			remoteStream: null,
			// timerStyle: 'timer',
			currentOrder: null,
			currentStatus: null,
			currentStart: null,
			currentEnd: null,
			currentInfo: {
				diagnoseId: null,
				name: null,
				tel: null,
				photo: null,
				expectStart: null,
				expectEnd: null,
				status: null
			},
			countDownCard: null,
			nextInfo: {
				diagnoseId: null,
				name: null,
				tel: null,
				photo: null,
				expectStart: null,
				expectEnd: null,
				status: null
			},
			// imgUrl: null,
			imgList: [],
			tableData: []
		};
	},
	
  methods: {
    onlineOrOfflineHandle: function() {
        let that = this;
        //执行医生上线
        if (that.status == 'offline') {
            that.$http('/video_diagnose/online', 'GET', null, true, function(resp) {
                that.status = 'online';
            });
        }
        //执行医生下线
        else {
            that.$http('/video_diagnose/offline', 'GET', null, true, function(resp) {
                if (!resp.result) {
                    ElMessage({
                        message: '有问诊患者，所以不能下线',
                        type: 'warning',
                        duration: 1200
                    });
                } else {
                    that.status = 'offline';
                    that.open = false;
                }
            });
        }
    },
    openOrCloseHandle: function() {
        let that = this;
        if (that.status == 'offline') {
            ElMessage({
                message: '请先上线才能开放挂号',
                type: 'warning',
                duration: 1200
            });
            return;
        }
    
        that.open = !that.open;
        let data = {
            open: that.open
        };
        that.$http('/video_diagnose/updateOpenFlag', 'POST', data, true, function(resp) {});
    },
    refreshInfo: function(isCatchMessage) {
        let that = this;
        //查询上线缓存中当前问诊信息
        that.$http('/video_diagnose/refreshInfo', 'GET', {}, true, function(resp) {
            let result = resp.result;
            let status = result.status;
            /*
             * 如果医生已经上线，但是浏览器闪退，再次进入视频问诊页面，发起主动查询，
             * 存在医生上线缓存，说明医生已经是上线状态了，页面就会自动设定为上线状态，
             * 不需要医生手动上线
             */
            if (status == 'online') {
                that.status = 'online';
                that.open = result.open;
                let roomId = result.roomId;
                that.roomId = roomId;
                that.currentOrder = result.currentOrder;
                that.currentStatus = result.currentStatus;
                that.currentStart = result.currentStart;
                that.currentEnd = result.currentEnd;
                //查询数据库中的当前问诊和等候问诊的信息
                that.$http('/video_diagnose/searchVideoDiagnoseInfo', 'GET', null, true, function(resp) {
                    let result = resp.result;
                    if (result.hasOwnProperty('currentInfo')) {
                        let currentInfo = result.currentInfo;
                        that.currentInfo = currentInfo;
                    } else {
                        that.currentInfo = {
                            diagnoseId: null,
                            name: null,
                            tel: null,
                            photo: null,
                            expectStart: null,
                            expectEnd: null,
                            status: null
                        };
                    }
                    if (result.hasOwnProperty('nextInfo')) {
                        let nextInfo = result.nextInfo;
                        nextInfo.expectStart = dayjs(nextInfo.expectStart).format('HH:mm');
                        nextInfo.expectEnd = dayjs(nextInfo.expectEnd).format('HH:mm');
                        that.nextInfo = nextInfo;
                    } else {
                        that.nextInfo = {
                            diagnoseId: null,
                            name: null,
                            tel: null,
                            photo: null,
                            expectStart: null,
                            expectEnd: null,
                            status: null
                        };
                    }
    
                    if (isCatchMessage) {
                        //监听推送消息
                        that.messageHandle();
                    }
                    if (that.currentOrder != null && that.currentOrder != 'none') {
                        //如果当前没有开始问诊，就静候后端推送的消息
                        if (that.currentStatus == 1) {
                            //开始倒计时
                            that.countDown();
                        }
                        //如果当前问诊开始了，就静候后端推送的结束消息，并且进入视频Room开始问诊
                        else if (that.currentStatus == 2) {
                            //开始倒计时
                            that.countDown();
                            if (isCatchMessage) {
                                that.startDiagnose();
                                //这里是新添加的语句
                                that.loadImage(that.currentInfo.diagnoseId);
                                ElMessage({
                                    message: '视频问诊开始',
                                    type: 'success',
                                    duration: 5000
                                });
                                let audio = new Audio('../../static/voice_1.mp3');
                                audio.play(); //播放提示音频
                            }
                        }
                    }
                });
            }
        });
    },

    countDown: function() {
        let that = this;
        let target = null;
        //如果问诊未开始，就倒计时还有多久开始
        if (that.currentStatus == 1) {
            target = that.currentStart;
        } 
        //如果问诊已经开始，就倒计时还有多久结束
        else if (that.currentStatus == 2) {
            target = that.currentEnd;
        }
    
        let timer = setInterval(function() {
            let now = dayjs();
            let seconds = Math.abs(dayjs(target).diff(now, 'second'));
            let minute = Math.floor(seconds / 60);
            if (minute < 10) {
                minute = '0' + minute;
            }
    
            let second = seconds % 60;
            if (second < 10) {
                second = '0' + second;
            }
    
            if (minute == '00' && second == '00') {
                clearInterval(timer);
            } else {
                that.countDownCard = minute + ':' + second;
            }
        }, 1000);
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
    startDiagnose: function() {
        let that = this;
        //获取在onLoad()函数中创建的TRTC客户端对象
        let client = that.client;
      
        //监听新增远端推流事件
        client.on('stream-added', event => {
            let remoteStream = event.stream;
            //订阅远端流
            client.subscribe(remoteStream);
            that.remoteStream = remoteStream;
        });
    
        //监听远端音视频流订阅成功事件
        client.on('stream-subscribed', event => {
            let remoteStream = event.stream;
            that.showRemoteVideo = true;
            //在页面DIV标签内显示视频
            remoteStream.play('remoteVideo');
        });
    
        //监听远端停止推流事件
        client.on('stream-removed', event => {
            let remoteStream = event.stream;
            //取消订阅该远端流
            // client.unsubscribe(remoteStream);
            that.showLocalVideo=false
            //停止播放远端流视频，并且关闭远端流
            remoteStream.stop();
            remoteStream.close();
            that.remoteStream = null;
        });
    
        //进入TRTC房间
        client.join({ roomId: that.roomId })
            .then(() => {
                //成功进入会议室，然后创建本地流
                let localStream = TRTC.createStream({
                    userId: that.userId + '',
                    audio: true,
                    video: true
                });
                //把本地音视频流对象保存到模型层变量
                that.localStream = localStream;
                //设置分辨率
                localStream.setVideoProfile('480p'); 
                //初始化本地音视频流
                localStream.initialize()
                    .then(() => {
                        console.log('初始化本地流成功');
                        that.showLocalVideo = true;
                        //在页面DIV中播放本地音视频流
                        localStream.play('localVideo');
                        //向远端用户推送本地流
                        client.publish(localStream)
                            .then(() => {
                                console.log('本地流发布成功');
                            })
                            .catch(error => {
                                console.error('本地流发布失败 ' + error);
                            });
                    })
                    .catch(error => {
                        console.error('初始化本地流失败 ' + error);
                    });
            })
            .catch(error => {
                console.error('进入房间失败: ' + error);
            });
    },
    endDiagnose: function() {
        let that = this;
        let stream = that.localStream;
        that.client.unpublish(stream).then(() => {
            that.client
                .leave()
                .then(() => {
                    console.log('成功退出视频问诊');
                    //关闭本地流
                    stream.stop();
                    stream.close();
                    that.localStream = null;
                    //关闭远端流
                    if (that.remoteStream != null) {
                        that.remoteStream.stop();
                        that.remoteStream.close();
                        that.remoteStream = null;
                    }
                })
                .catch(error => {
                    console.error('退出视频问诊失败' + error);
                });
        });
        that.showLocalVideo = false;
        that.showRemoteVideo = false;
        //这里是清空数组的语句
        that.imgList = [];
    },


    messageHandle: function() {
        let that = this;
        //开始接收WebSocket推送消息
        that.$options.sockets.onmessage = function(resp) {
            //如果医生下线了，就不处理服务器推送的消息
            if (that.status != 'online') {
                return;
            }
            //推送消息的内容
            let data = resp.data;
            //消息本体
            let message = data.split('#')[0];
            //参数数据
            let param = data.split('#')[1];
            //判断是否开始视频问诊
            if (message == 'StartDiagnose') {
                //让倒计时牌提示文字为“距离结束”
                that.currentStatus = 2;
                //记录当前问诊状态（暂时没用上，以后待用）
                that.currentInfo.status = 2;
                //开始倒计时
                that.countDown();
    
                //从推送消息中获取roomId
                that.roomId = param.split('&')[0];
                //从推送消息中获取挂号单ID
                let videoDiagnoseId = param.split('&')[1];
                //TODO 根据挂号单ID获取患者上传的照片
    
                //调用开始问诊函数
                that.startDiagnose(); 
                ElMessage({
                    message: '视频问诊开始',
                    type: 'success',
                    duration: 5000
                });
                let audio = new Audio('../../static/voice_1.mp3');
                //播放提示音频
                audio.play(); 
            }
            //如果收到的是结束问诊的消息
            else if (message == 'EndDiagnose') {
                //调用结束问诊函数
                that.endDiagnose();
                ElMessage({
                    message: '视频问诊结束',
                    type: 'warning',
                    duration: 5000
                });
                //刷新问诊信息，加载新的当前问诊和排队问诊
                that.refreshInfo(false);
            } 
            //如果收到的是刷新问诊消息（候诊挂号单付款成功）
            else if (message == 'RefreshDiagnose') {
                //重新加载问诊区的信息
                that.refreshInfo(false);
            }
        };
    
        setTimeout(function() {
            that.$socket.sendObj({ opt: 'connected', token: localStorage.getItem('token') });
        }, 2000);
    },

    showTableAndChart: function() {
        let that = this;
        that.$http('/video_diagnose/searchMyStatistics','GET',null,true,
            function(resp) {
                let result = resp.result;
                let days = result.days;
                let year = result.year;
                let length = days.length;
                for (let i = 0; i < 7 - length; i++) {
                    days.push({
                        date: '',
                        count: ''
                    });
                }
                that.tableData = days;
    
                let data = [];
                for (let one of year) {
                    data.push(one.count);
                    let option = {
                        title: {
                            text: '全年视频问诊统计图',
                            left: 'center',
                            top: '15px'
                        },
                        grid: {
                            left: '5px',
                            right: '15px',
                            bottom: '0px',
                            containLabel: true
                        },
                        xAxis: {
                            type: 'category',
                            boundaryGap: false,
                            data: [
                                '1月',
                                '2月',
                                '3月',
                                '4月',
                                '5月',
                                '6月',
                                '7月',
                                '8月',
                                '9月',
                                '10月',
                                '11月',
                                '12月'
                            ]
                        },
                        yAxis: {
                            type: 'value'
                        },
                        series: [
                            {
                                data: data,
                                type: 'line',
                                areaStyle: {},
                                smooth: true,
                                areaStyle: {
                                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                        {
                                            offset: 0,
                                            color: 'rgb(255, 158, 68)'
                                        },
                                        {
                                            offset: 1,
                                            color: 'rgb(255, 70, 131)'
                                        }
                                    ])
                                },
                                itemStyle: {
                                    color: 'rgb(255, 70, 131)'
                                }
                            }
                        ],
                        tooltip: {
                            trigger: 'axis',
                            axisPointer: {
                                type: 'cross'
                            }
                        }
                    };
                    let myChart = that.$echarts.init($('#chart')[0]);
                    myChart.setOption(option);
                }
            },
            false
        );
    },

    loadImage: function(videoDiagnoseId) {
        let that = this;
        that.imgList = [];
        let data = {
            videoDiagnoseId: videoDiagnoseId
        };
        that.$http('/video_diagnose/searchImageByVideoDiagnoseId', 'POST', data, true, function(resp) {
            let result = resp.result;
            for (let one of result) {
                //为了避免图片被浏览器缓存，这里添加随机参数
                that.imgList.push(`${that.$minioUrl}/${one}?random=${Math.random()}`);
            }
        });
    },


	},
  
  
	created: function() {
		
	},
  
	mounted: function() {
	    let that = this;
	    //进入该页面，默认是不接诊的离线状态
	    that.status = 'offline';
	  
	    //TODO 发送WebSocket心跳检测
	  
	    //检查浏览器是否支持TRTC技术（电脑必须具有摄像头或者连接和摄像头）
	    TRTC.checkSystemRequirements().then(checkResult => {
	        if (!checkResult.result) {
	            that.$alert('当前浏览器不支持在线视频会议', '提示信息', {
	                confirmButtonText: '确定'
	            });
	        } 
	        else {
	            //TRTC日志输出级别为Error（减少无用的日志输出）
	            TRTC.Logger.setLogLevel(TRTC.Logger.LogLevel.ERROR);
	
	            //生成TRTC签名字符串
	            that.$http('/video_diagnose/searchMyUserSig', 'GET', {}, false, function(resp) {
	                that.appId = resp.appId;
	                that.userSig = resp.userSig;
	                that.userId = resp.userId;
	                console.log(that.userSig)
	            });
	
	            //创建TrtcClient对象
	            let client = TRTC.createClient({
	                mode: 'rtc',
	                sdkAppId: that.appId,
	                userId: that.userId + '',
	                userSig: that.userSig,
	                useStringRoomId: true
	            });
	            that.client = client;
	        }
	    });
	
	    //TODO 加载问诊信息
	    //TODO 显示Chart图表
      
      that.refreshInfo(true);
      that.showTableAndChart();
	},

};
</script>

<style lang="less">
@import url('video_diagnose.less');
</style>
