/**
 * ПЗВПКС
 * Лабораторна робота ЛР1.3
 * Потоки в мові Java
 * F1: 1.25: E = (A + B) * (C + D * (MA * ME))
 * F2: 2.26: MF = MG * (MH * ML)
 * F3: 3.27: S = SORT(R * (MO * MP) + S)
 * Юхненко Віолетта Андріївна
 * Група ІМ-31
 * Дата 10.02.2026
 **/
package com.wiiollettaa;

public class Lab1 {
    public static void main(String[] args) {
        // Початок роботи головного потоку
        System.out.println("Головний потік: Виконання програми розпочато");

        // Зчитування розміру вхідних даних
        int n = Data.getNFromConsole();
        int inputType = 0;

        // Якщо n >= 1000, запитуємо спосіб введення даних
        if (n >= 1000) inputType = Data.getInputTypeFromConsole();

        // Фіксація часу початку виконання програми
        double startTime = (double) System.nanoTime() / 1_000_000_000F;

        // створення потоків з Runnable
        Thread t1 = new Thread(new T1(n, inputType));
        Thread t2 = new Thread(new T2(n, inputType));
        Thread t3 = new Thread(new T3(n, inputType));

        // Призначення пріоритетів потокам
        t1.setPriority(Thread.MIN_PRIORITY);
        t2.setPriority(Thread.MAX_PRIORITY);
        t3.setPriority(Thread.NORM_PRIORITY);

        // запуск потоків
        t1.start();
        t2.start();
        t3.start();

        // очікування завершення виконання потоків
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Вивід часу виконання програми (якщо n >= 1000)
        if (n >= 1000) {
            double endTime = (double) System.nanoTime() / 1_000_000_000F;
            System.out.println("Головний потік: час виконання програми: " + String.format("%.2f с", endTime - startTime));
        }

        // Завершення роботи головного потоку
        System.out.println("Головний потік: Виконання програми завершено");
    }
}
