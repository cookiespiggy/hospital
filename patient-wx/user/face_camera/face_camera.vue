<template>
	<view>
		<view class="face-container">
			<camera device-position="front" flash="off" class="camera" @error="error" v-if="showCamera">
				<cover-image :src="img.bg" class="bg"></cover-image>
			</camera>
			<view class="image-container" v-if="showImage">
				<image mode="widthFix" class="photo" :src="photoPath"></image>
				<view class="cover"></view>
			</view>
		</view>
		<view class="desc">
			<block v-if="mode == 'verificate'">
				<image :src="img.tips" mode="widthFix" class="tips"></image>
				<text>请把面部放在圆圈内</text>
				<text>拍摄脸部来确认身份</text>
			</block>
			<block v-if="mode == 'create'">
				<image :src="img.face" mode="widthFix" class="face"></image>
				<text>请把完整面部放在圆圈内</text>
				<text>拍摄脸部来保存身份识别数据</text>
			</block>
		</view>
		<button class="btn" @tap="confirmHandle">{{ mode == 'create' ? '录入面部信息' : '身份核实' }}</button>
	</view>
</template>

<script>
let dayjs = require('dayjs');
export default {
	data() {
		return {
			img: {
				bg: `${this.patientUrl}/page/user/face_camera/bg.png`,
				tips: `${this.patientUrl}/page/user/face_camera/tips.png`,
				face: `${this.patientUrl}/page/user/face_camera/face.png`
			},
			voice: {
				voice_1: `${this.patientUrl}/voice/voice_1.mp3`
			},
			mode: 'verificate',
			photoPath: '',
			showCamera: true,
			showImage: false,
			audio: null
		};
	},
	methods: {
    confirmHandle: function() {
        let that = this;
        //停止播放音频提示信息
        that.audio.stop();
        //获取摄像头对象
        let ctx = uni.createCameraContext();
        //拍摄照片
        ctx.takePhoto({
            quality: 'high',
            success: function(resp) {
                //照片在小程序上临时路径（小程序关闭后，照片会被销毁）
                that.photoPath = resp.tempImagePath;
                //照片拍摄完成就可以隐藏摄像头取景框组件了
                that.showCamera = false;
                //在页面上显示拍摄的照片
                that.showImage = true;
                //把照片读取成base64字符串
                uni.getFileSystemManager().readFile({
                    filePath: that.photoPath,
                    encoding: 'base64',
                    success: function(resp) {
                        let base64 = 'data:image:/png;base64,' + resp.data;
                        let url = null;
                        if (that.mode == 'create') {
                            //创建司机面部模型档案
                            url = that.api.createFaceModel;
                        } else {
                            //验证司机面部模型
                            url = that.api.verifyFaceModel;
                        }
                        //提交Ajax请求，上传照片Base64字符串
                        that.ajax(url, 'POST', { photo: base64 }, function(resp) {
                            if (that.mode == 'create') {
                                uni.showToast({
                                    icon: 'success',
                                    title: '面部录入成功'
                                });
                                setTimeout(function() {
                                    //跳回到上一页
                                    uni.navigateBack({
                                        delta: 1
                                    });
                                }, 2000);
                            } else {
                                //TODO 判断人脸识别结果
                               let result = resp.data.result;
                               if (result) {
                                    uni.showToast({
                                    icon: 'success',
                                    title: '面部验证成功'
                              });
                              setTimeout(function() {
                                    uni.navigateBack({
                                    delta: 1
                              });
                              }, 2000);
                              } else {
                                    uni.showToast({
                                    icon: 'error',
                                     title: '面部验证失败'
                              });
                                setTimeout(function() {
                                that.showImage = false;
                                that.showCamera = true;
                                that.audio.play();
                                }, 2000);
                              }
                                                      }
                                                  });
                                              }
                                          });
                                      }
                                  });
                              }
		
	},
	onLoad: function(options) {
	    let that = this;
	    that.mode = options.mode;
	
	    //创建Audio对象
	    let audio = uni.createInnerAudioContext();
	    that.audio = audio;
	    audio.src = that.voice.voice_1;
	    audio.play();
	},

	onHide: function() {
	    if (this.audio != null) {
	        this.audio.stop();
	    }
	}

};
</script>

<style lang="less">
@import url('face_camera.less');
</style>
