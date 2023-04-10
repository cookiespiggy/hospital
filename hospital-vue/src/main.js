import {
    createApp
} from 'vue'

import App from './App.vue'

//导入Svg图片插件，可以在页面上显示Svg图片
import 'virtual:svg-icons-register';

//导入JQuery库，因为Ajax用起来非常方便，支持同步和异步的Ajax请求
import $ from 'jquery';


const app = createApp(App) //创建VUE对象

//导入路由配置
import router from './router'
app.use(router) //挂载路由插件

//使用WebSocket，后端项目给前端页面推送通知
import VueNativeSock from "vue-native-websocket-vue3";
app.use(VueNativeSock, "ws://localhost:8092/hospital-api/socket", {
	"format": "json"
});

//导入Cookie库，可以读写Cookie数据
import VueCookies from 'vue3-cookies'
app.use(VueCookies); //挂载Cookie插件

//导入ElementUI
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
//import 'element-plus/dist/index.css';
import locale from 'element-plus/lib/locale/lang/zh-CN'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

//导入ElementUI的消息通知组件，下面封装全局Ajax的时候处理异常的时候需要弹出通知
import {
    ElMessage
} from 'element-plus'

//挂载ElementUl-Plus插件
app.use(ElementPlus, {
    locale
})

//配置JS生成PDF的公共函数
import html2Canvas from 'html2canvas'
import JsPDF from 'jspdf'
app.config.globalProperties.getPdf = function() {
    var title = this.htmlTitle //PDF标题
    html2Canvas(document.querySelector('#pdfDom'), {
        allowTaint: true,
        taintTest: false,
        useCORS: true,
        //width:960,
        //height:5072,
        dpi: window.devicePixelRatio * 4, //将分辨率提高到特定的DPI 提高四倍
        scale: 4 //按比例增加分辨率
    }).then(function(canvas) {
        let contentWidth = canvas.width
        let contentHeight = canvas.height
        let pageHeight = contentWidth / 592.28 * 841.89
        let leftHeight = contentHeight
        let position = 0
        let imgWidth = 595.28
        let imgHeight = 592.28 / contentWidth * contentHeight
        let pageData = canvas.toDataURL('image/jpeg', 1.0)
        let PDF = new JsPDF('', 'pt', 'a4')
        if (leftHeight < pageHeight) {
            PDF.addImage(pageData, 'JPEG', 0, 0, imgWidth, imgHeight)
        } else {
            while (leftHeight > 0) {
                PDF.addImage(pageData, 'JPEG', 0, position, imgWidth, imgHeight)
                leftHeight -= pageHeight
                position -= 841.89
                if (leftHeight > 0) {
                    PDF.addPage()
                }
            }
        }
        PDF.save(title + '.pdf')
    })
}

//导入echarts库
import * as echarts from 'echarts'
app.config.globalProperties.$echarts = echarts //设置全局变量$echarts


//后端项目的URL根路径
let baseUrl = "http://localhost:8092/hospital-api"
app.config.globalProperties.$baseUrl = baseUrl //设置全局变量$baseUrl

//Minio服务器地址
let minioUrl = "http://124.222.119.196:9000/hospital"
app.config.globalProperties.$minioUrl = minioUrl



//封装全局Ajax公共函数
app.config.globalProperties.$http = function(url, method, data, async, fun) {
    $.ajax({
        url: baseUrl + url,
        type: method,
        dataType: 'json',
        contentType: "application/json",
        traditional: true,
        xhrFields: {
            withCredentials: true
        },
        headers: {
            "token": localStorage.getItem("token")
        },
        async: async,
        data: JSON.stringify(data),
        success: function(resp) {
            if (resp.code == 200) {
                fun(resp)
            } else {
                ElMessage.error({
                    message: resp.msg,
                    duration: 1200
                });
            }
        },
        error: function(e) {
            if (e.status == undefined) {
                ElMessage.error({
                    message: "前端页面错误",
                    duration: 1200
                });
            } else {
                let status = e.status
                if (status == 401) {
                    router.push({
                        name: 'Login'
                    })
                } else {
                    ElMessage.error({
                        message: e.responseText,
                        duration: 1200
                    });
                }
            }

        }
    })
}

//封装用于判断用户是否具有某些权限的公共函数
app.config.globalProperties.isAuth = function(permission) {
    let permissions = localStorage.getItem("permissions");
    let flag = false
    for (let one of permission) {
        if (permissions.includes(one)) {
            flag = true
            break;
        }
    }
    return flag;
}

app.mount('#app')
