package com.wiiollettaa;

import java.util.Arrays;

public class Lab2_seq {
    public static void main(String[] args) {
        // Змінюйте N для різних тестів (1000, 1500, 2000)
        int N = 2000;

        // 1. Ініціалізація початкових даних (робимо один раз)
        int[][] MV = new int[N][N];
        int[][] MC = new int[N][N];
        int[][] MM = new int[N][N];
        int[] B = new int[N];
        int[] X = new int[N];
        int[] Z = new int[N];
        int[] R = new int[N];
        int e = 1;

        // Заповнення одиницями
        for (int i = 0; i < N; i++) {
            Arrays.fill(MV[i], 1);
            Arrays.fill(MC[i], 1);
            Arrays.fill(MM[i], 1);
        }
        Arrays.fill(B, 1);
        Arrays.fill(X, 1);
        Arrays.fill(Z, 1);

        System.out.println("Послідовне обчислення розпочато. Починаємо прогрів JVM...");

        // Виконуємо алгоритм 3 рази для оптимізації швидкості
        for (int step = 1; step <= 3; step++) {

            // --- СТАРТ ТАЙМЕРА ---
            long startTime = System.currentTimeMillis();

            // 2. Знаходження максимуму: m = max(Z)
            int m = Integer.MIN_VALUE;
            for (int i = 0; i < N; i++) {
                if (Z[i] > m) {
                    m = Z[i];
                }
            }

            // 3. Обчислення першої частини: p1 = m * (MV * B)
            int[] MV_B = new int[N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    MV_B[i] += MV[i][j] * B[j];
                }
            }

            int[] p1 = new int[N];
            for (int i = 0; i < N; i++) {
                p1[i] = MV_B[i] * m;
            }

            // 4. Обчислення другої частини: p2 = e * (MM * MC) * X
            // 4.1. Множення матриці MM на матрицю MC (Оптимізовано для кешу)
            int[][] MM_MC = new int[N][N];
            for (int i = 0; i < N; i++) {
                for (int k = 0; k < N; k++) { // <--- ЗМІНЕНО МІСЦЯМИ k ТА j
                    int temp = MM[i][k];
                    for (int j = 0; j < N; j++) { // <--- j ТЕПЕР НАЙГЛИБШИЙ ЦИКЛ
                        MM_MC[i][j] += temp * MC[k][j];
                    }
                }
            }

            int[] MMMC_X = new int[N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    MMMC_X[i] += MM_MC[i][j] * X[j];
                }
            }

            int[] p2 = new int[N];
            for (int i = 0; i < N; i++) {
                p2[i] = MMMC_X[i] * e;
            }

            // 5. Обчислення фінального результату R = p1 + p2
            for (int i = 0; i < N; i++) {
                R[i] = p1[i] + p2[i];
            }

            // --- ЗУПИНКА ТАЙМЕРА ---
            long endTime = System.currentTimeMillis();
            double time1 = (endTime - startTime) / 1000.0;

            // Виводимо в консоль інформацію ТІЛЬКИ на фінальному (третьому) кроці
            if (step == 3) {
                System.out.println("Час виконання (Час1) для N=" + N + " становить: " + time1 + " с.");
                System.out.println("Контрольний елемент результату R[0] = " + R[0]);
            }
        }
    }
}