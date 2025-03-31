@SpringBootTest
public class AsyncTest {

    @Resource(name = "asyncCoroutineService")
    private AsyncService asyncService;
    @Resource(name = "asyncThreadService")
    private AsyncService asyncService2;

    //无返回值任务测试
    @Test
    void voidTaskTest(){
        AsyncVoidTask voidTask = new AsyncVoidTask(() -> {
            System.out.println("async task executed");
        });
        AsyncVoidTask voidTask1 = new AsyncVoidTask(() -> {
            int a = 1/0;
            System.out.println("async task executed1");
        });
        AsyncVoidTask voidTask2 = new AsyncVoidTask(() -> {
            System.out.println("async task executed2");
        });
        AsyncVoidTask voidTask3 = new AsyncVoidTask(() -> {
            System.out.println("async task executed3");
        });
        asyncService.run(voidTask, voidTask1, voidTask2, voidTask3);
    }

    //带返回值的任务测试
    @Test
    void taskTest(){
        AsyncTask<String> asyncTask = new AsyncTask<>("asyncTask",() -> {
            System.out.println("async task");
            return "async task";
        });
        AsyncTask<String> asyncTask1 = new AsyncTask<>("asyncTask1",() -> {
            System.out.println("async task1");
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "async task1";
        });;
        AsyncTask<String> asyncTask2 = new AsyncTask<>("asyncTask2",() -> {
            System.out.println("async task2");
            int a = 1/0;
            return "async task2";
        });
        AsyncTask<String> asyncTask3 = new AsyncTask<>("asyncTask3",() -> {
            System.out.println("async task3");
            return "async task3";
        });
        //isBlock默认为ture 为ture时会阻塞当前线程 直到所有任务执行结束
        asyncService.run(false,asyncTask1, asyncTask2, asyncTask3,null,asyncTask);
        System.out.println(asyncTask.getTaskName() + ",result:" + asyncTask.getResult());
        System.out.println(asyncTask1.getTaskName() + ",result:" + asyncTask1.getResult());
        System.out.println(asyncTask2.getTaskName() + ",result:" + asyncTask2.getResult());
        System.out.println(asyncTask3.getTaskName() + ",result:" + asyncTask3.getResult());
    }

    //链式异步任务测试
    @Test
    void chainTasktest(){
        AsyncTaskChain task1 = new AsyncTaskChain<>("task1",t -> {
            System.out.println("task1参数:" + t);
            return () -> "result1:" + 1;
        });
        AsyncTaskChain task2 = new AsyncTaskChain<>("task2", t -> {
            System.out.println("task1的执行结果作为task2的参数,task2参数:" + t);
            return () -> "result2:" + 2;
        });
        AsyncTaskChain task3 = new AsyncTaskChain<>("task3", t -> {
            System.out.println("task2的执行结果作为task3的参数,task3参数:" + t);
            int a = 1/0;
            return () -> Collections.singletonList("result3:" + t);
        });
        AsyncTaskChain task4 = new AsyncTaskChain<>("task4", t -> {
            System.out.println("task3的执行结果作为task4的参数,task4参数:" + t);
            return () -> Collections.singletonList("result4:" + t);
        });
        asyncService.run(task1, task2, task3,task4,null);

        System.out.println("task1 result:"+task1.getResult());
        System.out.println("task2 result:"+task2.getResult());
        System.out.println("task3 result:"+task3.getResult());
        System.out.println("task4 result:"+task4.getResult());
    }
}
