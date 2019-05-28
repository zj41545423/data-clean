package com.my.zhj.cloud;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhj on 19/3/22.
 */
public class StessTest {

    public static void main(String[] args) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        String tempbody="{\"aa\":1.1,\"bb\":2.1}";



        AtomicInteger orderNo=new AtomicInteger(1);


        ExecutorService executor = Executors.newFixedThreadPool(10);

        Runnable run= () -> {
            String result="";
            long beg=System.currentTimeMillis();
            try {
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:8222/data-clean/api/v1/task/execution/TEST").newBuilder();
                urlBuilder.addQueryParameter("transId", "order"+orderNo.getAndIncrement());
                Request request = new Request.Builder()
                        .url(urlBuilder.build())
                        .post(RequestBody.create(MediaType.parse("application/json"), tempbody))
                        .build();

                Response response = okHttpClient.newCall(request).execute();
                result = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            long cost=System.currentTimeMillis()-beg;
            Map<String,Object> tempRes = JSON.parseObject(result, Map.class);
            System.out.println(Thread.currentThread().getName()+" 结束,耗时:"+cost+" ms,返回结果:"+tempRes);
        };



        for (int i = 0; i < 100; i++) {
            executor.execute(run);
        }


        try {
            executor.shutdown();
            if(!executor.awaitTermination(1000, TimeUnit.SECONDS)){
                executor.shutdownNow();
            }
        }catch (InterruptedException e){
            executor.shutdownNow();
        }


    }
}
