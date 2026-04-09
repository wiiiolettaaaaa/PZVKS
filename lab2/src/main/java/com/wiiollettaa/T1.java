package com.wiiollettaa;

import java.util.Arrays;
import java.util.Random;

public class T1 extends Thread {
    private final Data data;

    public T1(Data data) { this.data = data; }

    @Override
    public void run() {
        System.out.println("T1 розпочато");
        try {
            // 1. Введення даних: T1 вводить MV, MC
            Random rand = new Random();
            for (int i = 0; i < data.N; i++) {
                for (int j = 0; j < data.N; j++) {
                    data.MV[i][j] = rand.nextInt(5) + 1; // Випадкові числа від 1 до 5
                    data.MC[i][j] = rand.nextInt(5) + 1;
                }
            }

            // ТОЧКА СИНХРОНІЗАЦІЇ 1: Чекаємо завершення вводу всіма (Бар'єр)
            data.barrierInput.await();

            // 2. Локальний максимум Z_H та оновлення глобального
            int m_loc = Integer.MIN_VALUE;
            for (int i = 0; i < data.H; i++) {
                if (data.Z[i] > m_loc) m_loc = data.Z[i];
            }

            // КРИТИЧНА ДІЛЯНКА 1: Атомарне оновлення m
            data.m.accumulateAndGet(m_loc, Math::max);

            // ТОЧКА СИНХРОНІЗАЦІЇ 2: Очікуємо обчислення m усіма потоками (Event)
            data.eventMaxCalculated.countDown();
            data.eventMaxCalculated.await();

            // 3. Копіювання констант у штучних КД
            int e_copy;
            int m_copy;
            int[] X_copy;

            // КРИТИЧНА ДІЛЯНКА 2: Мютекс
            data.mutexE.lock();
            try { e_copy = data.e; } finally { data.mutexE.unlock(); }

            // КРИТИЧНА ДІЛЯНКА 3: Семафор як мютекс
            data.mutexM.acquire();
            try { m_copy = data.m.get(); } finally { data.mutexM.release(); }

            // КРИТИЧНА ДІЛЯНКА 4: Критична секція
            synchronized (data.monitorX) { X_copy = data.X; }

            // 4. Обчислення R_H
            int[][] MVh = Data.takePartOfMatrixRows(data.MV, 0, data.H);
            int[] p1 = Data.multiplyScalarByVector(m_copy, Data.multiplyMatrixByVector(MVh, data.B));

            int[][] MMh = Data.takePartOfMatrixRows(data.MM, 0, data.H);
            int[][] MMh_MC = Data.multiplyMatrixByMatrix(MMh, data.MC);
            int[] p2 = Data.multiplyScalarByVector(e_copy, Data.multiplyMatrixByVector(MMh_MC, X_copy));

            int[] Rh = Data.sumOfVectors(p1, p2);
            Data.insertSubvectorIntoVector(Rh, data.R, 0);

            // ТОЧКА СИНХРОНІЗАЦІЇ 3: Сигналізація потоку T2 (Семафор)
            data.syncT1.release();
            System.out.println("T1 завершено");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}