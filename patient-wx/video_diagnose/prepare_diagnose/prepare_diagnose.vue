<template>
	<view>
		<view class="main-container">
			<view>
				<u-count-down :time="time" format="mm:ss" autoStart @change="onChange">
					<view class="time">
						<text class="desc">距离问诊{{ startOrEnd }}</text>
						<text :class="startOrEnd == '结束' ? 'minute red' : 'minute'">{{ timeData.minutes }}</text>
						<text class="unit">分</text>
						<text :class="startOrEnd == '结束' ? 'second red' : 'second'">{{ timeData.seconds }}</text>
						<text class="unit">秒</text>
					</view>
				</u-count-down>
			</view>

			<view class="title">请上传诊断材料</view>
			<view class="files">
				<u-upload
					:fileList="fileList"
					@afterRead="afterRead"
					@delete="deleteImage"
					multiple
					:maxSize="2 * 1024 * 1024"
					:maxCount="20"
					:previewFullImage="true"
				></u-upload>
			</view>
			<view class="operate">
				<view class="btn red" @tap="deleteAllImageHandle">清空诊断材料</view>
				<view :class="enableBtn ? 'btn green' : 'btn gray'" @click="enterHandle">进入问诊室</view>
			</view>
		</view>
		<view class="desc-container">
			<view class="title">视频问诊说明</view>
			<view class="desc">
				<text class="num">1.</text>
				<text>
					在视频问诊之前以先上传相关的诊断材料照片，但是最多不能超过20张照片，而且每张照片不能超过2M体积。
				</text>
			</view>
			<view class="desc">
				<text class="num">2.</text>
				<text>
					视频问诊开始的时候，您可以点击【进入问诊室】按钮开始视频问诊。在视频问诊开始之前，请耐心等待。
				</text>
			</view>
			<view class="desc">
				<text class="num">3.</text>
				<text>
					建议您的手机连接WIFI网络，以保证最佳问诊质量。在问诊期间不要随便走动，避免网络信号波动，影响问诊音视频质量。
				</text>
			</view>
			<view class="desc">
				<text class="num">4.</text>
				<text>
					视频问诊仅作为初步诊断，存在误诊几率，不能以此当做准确诊断。建议您结合线下门诊的诊断结果及时就医。
				</text>
			</view>
			<view class="desc">
				<text class="num">5.</text>
				<text>
					如果遇到医疗系统技术故障或者其他不可抗力因素，无法进行视频问诊。请拨打【800-12388】客户热线，联系客服人员予以解决或者退款。
				</text>
			</view>
		</view>
	</view>
</template>

<script>
import dayjs from 'dayjs';
export default {
	data() {
		return {
			videoDiagnoseId: null,
			expectStart: null,
			expectEnd: null,
			doctorId: null,
			startOrEnd: '开始',
			enableBtn: false,
			time: null,
			timeData: {},
			fileList: []
		};
	},
	methods: {
    afterRead: function(event) {
        let that = this;
        let token = uni.getStorageSync('token');
      
        //保存选中照片的数组
        let lists = event.file;
        //模型层fileList数组元素数量
        let fileListLen = that.fileList.length;
      
        //如果倒计时结束或者问诊已经开始了就不能再上传照片了
        // if (that.time <= 0 || that.startOrEnd == '结束') {
        //     return;
        // }
      
        //把新选中的照片添加到模型层的fileList数组中
        lists.map(item => {
            that.fileList.push({
                ...item,
                status: 'uploading',
                message: '上传中'
            });
        });
      
        //遍历数组，把这些照片上传到服务端
        for (let i = 0; i < lists.length; i++) {
            uni.uploadFile({
                url: `${that.api.uploadVideoDiagnoseImage}?token=${token}`,
                filePath: lists[i].url,
                name: 'file',
                formData: {
                    videoDiagnoseId: that.videoDiagnoseId
                },
                success: resp => {
                    //返回的是字符串，需要转换成JSON
                    let filename = JSON.parse(resp.data).filename;
                    //取出刚上传的成功文件的JSON对象
                    let item = that.fileList[fileListLen];
                    item["status"]="success"
                    //因为删除文件的时候需要使用filename，所以把filename添加到JSON对象中
                    item["filename"]=filename
                    fileListLen++;
                }
            });
        }
    },
    onChange: function(e) {
        //当前倒计时的分钟数字
        let minutes = e.minutes;
        //当前倒计时的秒钟数字
        let seconds = e.seconds;
      
        if (minutes == 0 && seconds == 0) {
            //如果到了问诊开始的时间
            if (this.startOrEnd == '开始') {
                this.startOrEnd = '结束';
                this.enableBtn = true;
                //更新时间差
                this.time = dayjs(this.expectEnd).valueOf() - new Date().getTime();
                //手机发出震动，并且弹出提示信息
                uni.vibrateLong({
                    complete: function() {
                        uni.showToast({
                            icon: 'none',
                            title: '请进入问诊室',
                            duration: 2000
                        });
                    }
                });
            } 
            /*
             * 可能在问诊中，患者手机突然没电关机了。等到他充上电开机之后，
             * 重新进入到候诊页面，可能视频问诊已经过了结束时间，所以这种
             * 情况就不允许进入视频问诊页面
             */
            else {
                //不可以点击进按钮入问诊页面
                this.enableBtn = false;
                uni.showToast({
                    icon: 'none',
                    title: '问诊已结束'
                });
            }
        }
    
      
        if (minutes < 10) {
            e.minutes = '0' + minutes;
        }
    
        if (seconds < 10) {
            e.seconds = '0' + seconds;
        }
    
        this.timeData = e;
    },

    deleteImage: function(event) {
        let that = this;
        //倒计时结束或者问诊开始，则无法删除照片
        // if (that.time <= 0 || that.startOrEnd == '结束') {
        //     return;
        // }
        let filename = that.fileList[event.index].filename;
        let data = {
            videoDiagnoseId: that.videoDiagnoseId,
            filename: filename
        };
        that.ajax(
            that.api.deleteVideoDiagnoseImage,
            'POST',
            data,
            function(resp) {
                uni.showToast({
                    icon: 'success',
                    title: '文件删除成功'
                });
                that.fileList.splice(event.index, 1);
            },
            false
        );
    },
    deleteAllImageHandle: function() {
        let that = this;
        // if (that.time <= 0 || that.startOrEnd == '结束') {
        //     return;
        // }
        uni.showModal({
            title: '提示信息',
            content: '您确定删除所有上传文件？',
            success: function(resp) {
                if (resp.confirm) {
                    let data = {
                        videoDiagnoseId: that.videoDiagnoseId
                    };
                    that.ajax(
                        that.api.deleteVideoDiagnoseImage,
                        'POST',
                        data,
                        function(resp) {
                            uni.showToast({
                                icon: 'success',
                                title: '文件删除成功'
                            });
                            that.fileList.length=[];
                        },
                        false
                    );
                }
            }
        });
    },
    enterHandle: function() {
        let that = this;
        if (!that.enableBtn) {
            return;
        }
        
        let data = {
            doctorId: that.doctorId
        };
        //查询roomId
        that.ajax(
            that.api.searchRoomId,
            'POST',
            data,
            function(resp) {
                let roomId = resp.data.result;
                if (roomId != null) {
                    that.roomId = roomId;
                    //查询TRTC的相关数据
                    that.ajax(that.api.searchUserSig, 'GET', null, function(resp) {
                        let userId = resp.data.userId;
                        let userSig = resp.data.userSig;
                        uni.navigateTo({
                            url: `../video_diagnose/video_diagnose?roomId=${that.roomId}&userId=${userId}&userSig=${userSig}&expectEnd=${that.expectEnd}`
                        });
                    });
                }
            },
            false
        );
    }




		
	},
	onLoad: function(options) {
	    let that = this;
	    that.videoDiagnoseId = Number(options.videoDiagnoseId);
	    that.expectStart = options.expectStart;
	    that.expectEnd = options.expectEnd;
	    that.doctorId = options.doctorId;
	
	    //计算开始时间与当前时间的时间差（时间戳毫秒时间）
	    let time = dayjs(that.expectStart).valueOf() - new Date().getTime();
	    //开始时间晚于当前时间
	    if (time > 0) {
	        that.startOrEnd = '开始';
	        that.enableBtn = false;
	    } 
	    else {
	        time = dayjs(that.expectEnd).valueOf() - new Date().getTime();
	        that.startOrEnd = '结束';
	        that.enableBtn = true;
	    }
	    //把时间差保存到模型层
	    that.time = time;
	  
	    let data = {
	        videoDiagnoseId: that.videoDiagnoseId
	    };
	    that.ajax(that.api.searchImageByVideoDiagnoseId, 'POST', data, function(resp) {
	        let result = resp.data.result;
	        for (let one of result) {
	            that.fileList.push({
	                filename: one.filename,
	                thumb: `${that.minioUrl}/${one.path}`,
	                url: `${that.minioUrl}/${one.path}`,
	                type: 'image'
	            });
	        }
	    });
	}

};
</script>

<style lang="less">
@import url('prepare_diagnose.less');
</style>
