package com.wiiollettaa;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Data {
    public int N;
    public int P = 4;
    public int H;

    // Спільні ресурси згідно з варіантом
    public int[][] MV, MC, MM;
    public int[] B, X, Z, R;
    public int e;

    // АТОМІК: для безпечного обчислення глобального максимуму m = max(Z)
    public AtomicInteger m = new AtomicInteger(Integer.MIN_VALUE);

    // --- ЗАСОБИ СИНХРОНІЗАЦІЇ (ВЗАЄМОДІЯ ПРОЦЕСІВ) ---
    public CyclicBarrier barrierInput = new CyclicBarrier(P);
    public CountDownLatch eventMaxCalculated = new CountDownLatch(P);
    public Semaphore syncT1 = new Semaphore(0);
    public Semaphore syncT3 = new Semaphore(0);
    public Semaphore syncT4 = new Semaphore(0);

    // --- ЗАСОБИ СИНХРОНІЗАЦІЇ (КРИТИЧНІ ДІЛЯНКИ) ---
    public ReentrantLock mutexE = new ReentrantLock();
    public Semaphore mutexM = new Semaphore(1);
    public final Object monitorX = new Object();

    public Data(int n) {
        this.N = n;
        this.H = N / P;
        MV = new int[N][N];
        MC = new int[N][N];
        MM = new int[N][N];
        B = new int[N];
        X = new int[N];
        Z = new int[N];
        R = new int[N];
    }

    // --- ДОПОМІЖНІ МАТЕМАТИЧНІ МЕТОДИ ---
    public static int[] multiplyMatrixByVector(int[][] matrix, int[] vector) {
        int[] result = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < vector.length; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        return result;
    }

    public static int[][] multiplyMatrixByMatrix(int[][] m1, int[][] m2) {
        int rows = m1.length;
        int cols = m2[0].length;
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < m2.length; k++) {
                    result[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return result;
    }

    public static int[] multiplyScalarByVector(int scalar, int[] vector) {
        int[] result = new int[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i] * scalar;
        }
        return result;
    }

    public static int[] sumOfVectors(int[] v1, int[] v2) {
        int[] result = new int[v1.length];
        for (int i = 0; i < v1.length; i++) {
            result[i] = v1[i] + v2[i];
        }
        return result;
    }

    public static int[][] takePartOfMatrixRows(int[][] matrix, int startRow, int count) {
        int[][] part = new int[count][matrix[0].length];
        for (int i = 0; i < count; i++) {
            System.arraycopy(matrix[startRow + i], 0, part[i], 0, matrix[0].length);
        }
        return part;
    }

    public static void insertSubvectorIntoVector(int[] sub, int[] full, int startIndex) {
        System.arraycopy(sub, 0, full, startIndex, sub.length);
    }
}