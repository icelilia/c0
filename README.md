# c0编译器
## 前言
+ 北航软件学院2017级编译原理大作业
+ 时间：2019-12-26
+ 语言：Java
+ 环境：
    - java version "1.8.0_201"
    - Java(TM) SE Runtime Environment (build 1.8.0_201-b09)
    - Java HotSpot(TM) 64-Bit Server VM (build 25.201-b09, mixed mode)

+ IDE：Eclipse 2019-06
## c0语法
+ 详见/src/readme.txt
## 记录
+ 2019-12-23 凌晨，完成-s输出，Alpha版
+ 2019-12-25 下午，完成-o输出，Beta版
+ 2019-12-25 晚上，添加注释处理
+ 2019-12-26 晚上，通过最终检测
## 编译
+ 命令行（当前目录为项目根目录，编译产物在 /out 中）：
    ```
    find src -name \*.java > javaFiles.txt
    javac -encoding GBK -d out -cp . @javaFiles.txt
    ```
## 运行
+ 命令行（当前目录为项目根目录，以 -h 参数示例）：
    ```
    cd out
    java main.Main -h
    ```