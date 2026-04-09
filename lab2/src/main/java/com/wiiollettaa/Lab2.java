package com.wiiollettaa;
/**
 * Лабораторна робота №2
 * Дисципліна: Паралельне програмування
 * Тема: Синхронізація потоків. Спільні ресурси.
 * Автор: Юхненко Віолетта
 * Група: ІМ-31
 * Дата: 25.02
 * Варіант: R = max(Z) * (B * MV) + e * X * (MM * MC)
 */

public class Lab2 {
    public static void main(String[] args) {
        System.out.println("Головний потік почав роботу.");

        // Розмірність векторів і матриць (для Етапу 5 використовуйте 1200, 2000, 2400)
        int N = 1000;
        Data data = new Data(N);

        T1 t1 = new T1(data);
        T2 t2 = new T2(data);
        T3 t3 = new T3(data);
        T4 t4 = new T4(data);

        // Запуск таймера для вимірювання Час4
        long startTime = System.currentTimeMillis();

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Зупинка таймера
        long endTime = System.currentTimeMillis();
        double time4 = (endTime - startTime)/1000.0;

        System.out.println("Головний потік завершив роботу.");
        System.out.println("Час виконання (Час4) для N=" + N + " становить: " + time4 + " с.");
    }
}