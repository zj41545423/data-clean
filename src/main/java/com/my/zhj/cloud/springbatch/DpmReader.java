package com.my.zhj.cloud.springbatch;


import com.my.zhj.cloud.springjpa.entity.DpmIndicator;
import org.springframework.batch.item.ItemReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhj on 19/3/25.
 */
public class DpmReader implements ItemReader<DpmIndicator> {

        private List<DpmIndicator> list;
        private AtomicInteger index;
        private Lock lock;

        public DpmReader(List<DpmIndicator> list) {
            this.list = list;
            this.index=new AtomicInteger(0);
            this.lock=new ReentrantLock();
        }

        @Override
        public DpmIndicator read() {

            try {
                lock.lock();
                int num=index.getAndIncrement();
                if (num <list.size()){
                    return list.get(num);
                }
                return null;
            }finally {
                lock.unlock();
            }

        }


}
