#### 1、介绍
&emsp;&emsp;这是对CompletableFuture和quasar框架虚拟线程进行了一个简单封装的工具类，将异步任务抽象为了Task对象，可通过Task对象的方式进行异步任务的执行。
#### 2、快速使用
##### 2.1、导入quasar依赖（如果不使用quasar的虚拟线程可不导入）
```
<dependency>
    <groupId>co.paralleluniverse</groupId>
    <artifactId>quasar-core</artifactId>
    <version>0.7.10</version>
</dependency>
```
##### 2.2、创建Task对象，使用AsyncService接口选取合适的实现类调用run方法即可。
**AsyncTask**：异步任务,带返回值  
**AsyncTaskChain**：异步任务,按放入run方法的顺序执行,带上一个线程的返回值,有返回结果  
**AsyncVoidTask**：异步任务,无返回值  
**AsyncCoroutineService**：使用quasar框架采用协程完成异步任务的执行  
**AsyncThreadService**：使用CompletableFuture采用线程完成异步任务的执行
##### 2.3、可参考代码中的Test类的用法
