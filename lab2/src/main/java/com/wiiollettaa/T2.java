package com.wiiollettaa;

import java.util.Arrays;
import java.util.Random;

public class T2 extends Thread {
    private final Data data;

    public T2(Data data) { this.data = data; }

    @Override
    public void run() {
        System.out.println("T2 розпочато");
        try {
            // 1. Введення даних: T2 вводить MM
            // 1. Введення даних: T2 вводить MM
            Random rand = new Random();
            for (int i = 0; i < data.N; i++) {
                for (int j = 0; j < data.N; j++) {
                    data.MM[i][j] = rand.nextInt(5) + 1;
                }
            }

            // ТОЧКА СИНХРОНІЗАЦІЇ 1: Бар'єр
            data.barrierInput.await();

            // 2. Локальний максимум Z_H
            int m_loc = Integer.MIN_VALUE;
            int startIdx = data.H;
            int endIdx = data.H * 2;
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

            // ТОЧКА СИНХРОНІЗАЦІЇ 3: Очікування завершення T1, T3, T4 (Семафори)
            data.syncT1.acquire();
            data.syncT3.acquire();
            data.syncT4.acquire();

            // ВИВЕДЕННЯ РЕЗУЛЬТАТУ
            // ВИВЕДЕННЯ РЕЗУЛЬТАТУ ТА ПОЧАТКОВИХ ДАНИХ
            if (data.N <= 16) {
                System.out.println("\n================ ДАНІ ДЛЯ ПЕРЕВІРКИ ================");
                System.out.println("Вектор B: " + Arrays.toString(data.B));
                System.out.println("Вектор X: " + Arrays.toString(data.X));
                System.out.println("Вектор Z: " + Arrays.toString(data.Z));
                System.out.println("Скаляр e = " + data.e);
                System.out.println("Знайдений глобальний максимум max(Z) = " + data.m.get());

                System.out.println("\nМатриця MV:");
                for (int[] row : data.MV) System.out.println(Arrays.toString(row));

                System.out.println("\nМатриця MC:");
                for (int[] row : data.MC) System.out.println(Arrays.toString(row));

                System.out.println("\nМатриця MM:");
                for (int[] row : data.MM) System.out.println(Arrays.toString(row));

                System.out.println("====================================================");
                System.out.println("Фінальний вектор R = " + Arrays.toString(data.R));
                System.out.println("====================================================\n");
            } else {
                System.out.println("Обчислення завершено успішно (масив завеликий для друку).");
            }
            System.out.println("T2 завершено");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}