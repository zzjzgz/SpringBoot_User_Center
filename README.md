# 初始化项目

## 1.1、前端初始化

- 初始化项目

  - 安装git

  - 安装node.js，版本选择node.js 16.20.0

  - 安装Ant Desgin

    - ```bash
      #安装Ant Desgin
      npm i @ant-design/pro-cli -g
      pro create myapp
      
      #版本选择umi3
      #simple
      ```

- 安装依赖

  > cd myapp && npm install

- 启动项目 在 package.josn 文件里面找start命令

## 1.2、后端初始化

- 准备环境（MySQL之类的）
  - 安装MySQL5.7
  - SpringBoot2.x
- 整合框架
  - idea中使用SpringBoot官方的模版生成器
    - 将Spring-web，MySQL的驱动，lombok，mybatis等等的都引入进来
  - 加入mybatis-plus，跑通一个小demo