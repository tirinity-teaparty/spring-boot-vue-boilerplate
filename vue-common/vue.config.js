const path = require('path');

module.exports = {
  outputDir: path.resolve(__dirname, 'dist'),
  filenameHashing: false,
  publicPath: '/',
  assetsDir: '.',           
  lintOnSave: false,
  css: {
    extract: process.env.NODE_ENV === 'production'
  },
  devServer: {
    port: 3000,
    headers: {
      'Access-Control-Allow-Origin': 'http://localhost:8080',
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
    },
  },
};
