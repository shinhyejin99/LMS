import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // API만 백엔드(80)로 프록시. 페이지는 React가 담당해야 함.
      '/classroom/api': {
        target: 'http://localhost',
        changeOrigin: true,
      },
    },
  },
})
