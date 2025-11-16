import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src') // __dirname 是vite.config.js所在目录，即videoFront
    }
  },
  // 新增这部分 server 配置！！！  新增部分：使得手机等局域网设备可以访问
  server: {
    host: '0.0.0.0', // 关键：开放局域网访问
    port: 5173 // 可选，明确指定端口（默认就是5173，写不写都可以）
  }
})
