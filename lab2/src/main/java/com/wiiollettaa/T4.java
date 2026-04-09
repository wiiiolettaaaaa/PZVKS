package com.wiiollettaa;

import java.util.Arrays;
import java.util.Random;

public class T4 extends Thread {
    private final Data data;

    public T4(Data data) { this.data = data; }

    @Override
    public void run() {
        System.out.println("T4 розпочато");
        try {
            // 1. Введення даних: T4 вводить B, X, e, Z
            Random rand = new Random();
            for (int i = 0; i < data.N; i++) {
                data.B[i] = rand.nextInt(5) + 1;
                data.X[i] = rand.nextInt(5) + 1;
                data.Z[i] = rand.nextInt(5) + 1;
            }
            data.e = rand.nextInt(5) + 1; // Скаляр теж від 1 до 5

            // ТОЧКА СИНХРОНІЗАЦІЇ 1: Бар'єр
            data.barrierInput.await();

            // 2. Локальний максимум Z_H
            int m_loc = Integer.MIN_VALUE;
            int startIdx = data.H * 3;
            int endIdx = data.N;
            for (int i = startIdx; i < endIdx; i++) {
                if (data.Z[i] > m_loc) m_loc = data.Z[i];
            }

            // КРИТИЧНА ДІЛЯНКА 1: Атомік
            data.m.accumulateAndGet(m_loc, Math::max);

            // ТОЧКА СИНХРОНІЗАЦІЇ 2: Event
            data.eventMaxCalculated.countDown();
            data.eventMaxCalculated.await();

            // 3. Копіювання констант у штучних КД
            int e_copy;
            int m_copy;
            int[] X_copy;

            data.mutexE.lock();
            try { e_copy = data.e; } finally { data.mutexE.unlock(); }

            data.mutexM.acquire();
            try { m_copy = data.m.get(); } finally { data.mutexM.release(); }

            synchronized (data.monitorX) { X_copy = data.X; }

            // 4. Обчислення R_H
            int[][] MVh = Data.takePartOfMatrixRows(data.MV, startIdx, data.H);
            int[] p1 = Data.multiplyScalarByVector(m_copy, Data.multiplyMatrixByVector(MVh, data.B));

            int[][] MMh = Data.takePartOfMatrixRows(data.MM, startIdx, data.H);
            int[][] MMh_MC = Data.multiplyMatrixByMatrix(MMh, data.MC);
            int[] p2 = Data.multiplyScalarByVector(e_copy, Data.multiplyMatrixByVector(MMh_MC, X_copy));

            int[] Rh = Data.sumOfVectors(p1, p2);
            Data.insertSubvectorIntoVector(Rh, data.R, startIdx);

            // ТОЧКА СИНХРОНІЗАЦІЇ 3: Сигналізація потоку T2 (Семафор)
            data.syncT4.release();
            System.out.println("T4 завершено");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}