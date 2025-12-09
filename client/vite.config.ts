import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    // 启用代码分割
    rollupOptions: {
      output: {
        manualChunks: {
          // 将第三方库单独打包
          'vendor': ['vue', 'vue-router', 'pinia', 'element-plus'],
          'axios': ['axios']
        }
      }
    },
    // 启用压缩，使用默认的esbuild压缩器
    minify: 'esbuild',
    // 生成sourcemap，便于调试
    sourcemap: false
  }
})
