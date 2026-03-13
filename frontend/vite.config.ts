import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(),tailwindcss(),],
  server: {
    port: 3000,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://192.168.92.128:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path, // НЕ добавляем /api, так как path уже включает /api
        configure: (proxy) => {
          proxy.on('proxyReq', (proxyReq, req) => {
            console.log('Proxying:', req.method, req.url, '->', proxyReq.path)
          })
        }
      }
    }
  },
  preview: {
    port: 3000,
    host: '0.0.0.0',
  }
})
