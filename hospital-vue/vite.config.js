const path = require('path')
import vue from '@vitejs/plugin-vue'
import viteSvgIcons, {
    createSvgIconsPlugin
} from 'vite-plugin-svg-icons';

import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import {
    ElementPlusResolver
} from 'unplugin-vue-components/resolvers'

module.exports = {
    base: '/hospital-vue',
    server: {
        port: 3000,
        //是否弹出浏览器
        open: false,
        //允许跨域
        cors: true
    },
    plugins: [
        vue(),
        AutoImport({
            resolvers: [ElementPlusResolver()],
        }),
        Components({
            resolvers: [ElementPlusResolver()],
        }),
        //引入SVG图标素材文件
        createSvgIconsPlugin({
            iconDirs: [path.resolve(process.cwd(), 'src/icons/svg')],
            symbolId: '[name]',
        })
    ]
}
