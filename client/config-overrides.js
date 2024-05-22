/* eslint-disable @typescript-eslint/no-var-requires */
const { override, addWebpackAlias } = require('customize-cra');
const path = require('path');

module.exports = override(
  addWebpackAlias({
    '@/atoms': path.resolve(__dirname, 'src/components/atoms'),
    '@/molecules': path.resolve(__dirname, 'src/components/molecules'),
    '@/organisms': path.resolve(__dirname, 'src/components/organisms'),
    '@': path.resolve(__dirname, 'src'),
  })
);
