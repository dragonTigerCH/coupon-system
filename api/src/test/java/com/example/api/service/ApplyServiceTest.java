package com.example.api.service;

import com.example.api.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ApplyServiceTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    void 한번만_응모() {

        applyService.apply(1l);

        long count = couponRepository.count();

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void 여러명_응모() throws InterruptedException {

        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32); //병렬작업을 간단하게 만들어주는 유틸리티 클래스

        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // submit은 Callable을 받아서 Future를 리턴한다.
        // Callable ? Runnable과 유사하지만 작업의 결과를 받을 수 있다.
        // Future ? 비동기적인 작업의 현재 상태를 조회하거나 결과를 가져올 수 있다.
        for (int i = 0; i < threadCount; i++){
            long userId = i;
                executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        long count = couponRepository.count();

        assertThat(count).isEqualTo(100);
    }

}