package com.mail.tools;

import com.creek.common.CreekPath;
import com.creek.router.annotation.CreekMethod;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    private static ThreadPoolExecutor sExecutor;

    static {
        int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 6));
        int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
        sExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 10L, TimeUnit.MILLISECONDS, new SynchronousQueue());
    }


    @CreekMethod(path = CreekPath.Tools_Thread_Pool_Execute_Runnable)
    public static void execute(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        sExecutor.execute(runnable);
    }

}
