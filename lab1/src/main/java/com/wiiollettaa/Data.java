package com.wiiollettaa;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

public class Data {
    private static final Scanner scanner = new Scanner(System.in);

    // F1: E = (A + B) * (C + D * (MA * ME))
    public static int[] F1(int[] A, int[] B, int[] C, int[] D, int[][] MA, int[][] ME) {

        int[] part1 = addTwoVectors(A, B); // A + B

        int[][] MA_ME = multiplyTwoMatrices(MA, ME); // MA * ME
        int[] D_MA_ME = multiplyVectorAndMatrix(D, MA_ME); // D * (MA * ME)

        int[] part2 = addTwoVectors(C, D_MA_ME); // C + D*(MA*ME)

        return multiplyTwoVectors(part1, part2); // (A+B) * (...)
    }


    // F2: MF = MG * (MH * ML)
    public static int[][] F2(int[][] MG, int[][] MH, int[][] ML) {

        int[][] MH_ML = multiplyTwoMatrices(MH, ML); // MH * ML

        return multiplyTwoMatrices(MG, MH_ML); // MG * (MH * ML)
    }


    // F3: S = SORT(R * (MO * MP) + S)
    public static int[] F3(int[] R, int[] S, int[][] MO, int[][] MP) {

        int[][] MO_MP = multiplyTwoMatrices(MO, MP); // MO * MP

        int[] R_MO_MP = multiplyVectorAndMatrix(R, MO_MP); // R * (MO * MP)

        int[] result = addTwoVectors(R_MO_MP, S); // R*(...) + S

        Arrays.sort(result); // SORT(...)

        return result;
    }

    public static int getNFromConsole() {
        while (true) {
            System.out.print("Введіть n: ");
            if (scanner.hasNextInt()) return scanner.nextInt();

            System.out.println("n має бути цілим числом");
        }
    }

    public static int getInputTypeFromConsole() {
        while (true) {
            System.out.println("""
                    Оберіть варіант введення даних.\
                    
                    1 зчитування з файлу, \
                    
                    2 встановлення всіх елементів заданому значенню, \
                    
                    3 використання генератора випадкових значень\s""");
            if (scanner.hasNextInt()) return scanner.nextInt();
        }
    }

    public static int[] multiplyTwoVectors(int[] vector1, int[] vector2) {
        int[] result = new int[vector1.length];
        for (int i = 0; i < vector1.length; i++) {
            result[i] = vector1[i] * vector2[i];
        }
        return result;
    }

    public static int[] addTwoVectors(int[] vector1, int[] vector2) {
        int[] sum = new int[vector1.length];
        for (int i = 0; i < sum.length; i++) {
            sum[i] = vector1[i] + vector2[i];
        }
        return sum;
    }

    public static int[][] multiplyTwoMatrices(int[][] matrix1, int[][] matrix2) {
        int rows = matrix1.length;
        int cols = matrix2[0].length;
        int common = matrix1[0].length; // спільна розмірність

        int[][] result = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = 0; // ОБОВ'ЯЗКОВО обнуляємо
                for (int k = 0; k < common; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        return result;
    }

    public static int[] multiplyVectorAndMatrix(int[] vector, int[][] matrix) {
        int size = matrix[0].length;
        int[] result = new int[size];

        for (int i = 0; i < size; i++) {
            result[i] = 0; // ОБОВ'ЯЗКОВО обнуляємо
            for (int j = 0; j < vector.length; j++) {
                result[i] += vector[j] * matrix[j][i];
            }
        }
        return result;
    }


    public static int[][] addTwoMatrices(int[][] matrix1, int[][] matrix2) {
        int[][] result = new int[matrix1.length][matrix1[0].length];

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                result[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }
        return result;
    }

    public static int[] getVectorOption(String vectorName, int n, int inputOption, int defaultValue) {
        // Якщо n < 1000, отримуємо дані з консолі та одразу повертаємо результат
        if (n < 1000) return getVectorFromConsole(vectorName, n);

        // Створюємо мапу для варіантів отримання вектора
        Map<Integer, Supplier<int[]>> vectorOptions = new HashMap<>();

        // Додаємо варіанти отримання вектора до мапи
        vectorOptions.put(1, () -> getVectorFromFile(vectorName, n)); // Зчитування з файлу
        vectorOptions.put(2, () -> getVectorFilledWithDefaultValue(n, defaultValue)); // Заповнення defaultValue значенням

        // Виконуємо відповідний метод або за замовчуванням генеруємо випадкові значення
        return vectorOptions.getOrDefault(inputOption, () -> getVectorWithRandomValues(n)).get();
    }

    public static int[][] getMatrixOption(String matrixName, int n, int inputOption, int defaultValue) {
        // Якщо n < 1000, отримуємо матрицю з консолі та одразу повертаємо результат
        if (n < 1000) return getMatrixFromConsole(matrixName, n);

        // Створюємо мапу для варіантів отримання матриці
        Map<Integer, Supplier<int[][]>> matrixOptions = new HashMap<>();

        // Додаємо варіанти отримання матриці до мапи
        matrixOptions.put(1, () -> getMatrixFromFile(matrixName, n)); // Зчитування з файлу
        matrixOptions.put(2, () -> getMatrixFilledWithDefaultValue(n, defaultValue)); // Заповнення одним значенням

        // Виконуємо відповідний метод або за замовчуванням генеруємо випадкові значення
        return matrixOptions.getOrDefault(inputOption, () -> getMatrixWithRandomValues(n)).get();
    }

    public static int[] getVectorFromFile(String arrayName, int n) {
        try (BufferedReader reader = new BufferedReader(new FileReader(arrayName + ".txt"))) {
            int[] result = new int[n];
            String[] arrayLine = reader.readLine().split(" ");
            for (int i = 0; i < n; i++) {
                result[i] = Integer.parseInt(arrayLine[i]);
            }

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int[][] getMatrixFromFile(String matrixName, int n) {
        try (BufferedReader reader = new BufferedReader(new FileReader(matrixName + ".txt"))) {
            int[][] result = new int[n][n];
            for (int i = 0; i < n; i++) {
                String[] arrayLine = reader.readLine().split(" ");
                for (int j = 0; j < n; j++) {
                    result[i][j] = Integer.parseInt(arrayLine[j]);
                }
            }

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int[] getVectorFilledWithDefaultValue(int n, int defaultValue) {
        int[] result = new int[n];
        Arrays.fill(result, defaultValue);
        return result;
    }

    private static int[][] getMatrixFilledWithDefaultValue(int n, int defaultValue) {
        int[][] result = new int[n][n];
        for (int[] row : result) {
            Arrays.fill(row, defaultValue);
        }
        return result;
    }

    private static int[] getVectorWithRandomValues(int n) {
        int[] result = new int[n];
        Random random = new Random();
        for (int i = 0; i < result.length; i++) {
            result[i] = random.nextInt(100); // заповнити значеннями віл 0 до 99 включно
        }
        return result;
    }

    private static int[][] getMatrixWithRandomValues(int n) {
        int[][] result = new int[n][n];
        Random random = new Random();
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                result[i][j] = random.nextInt(100); // заповнити значеннями віл 0 до 99 включно
            }
        }
        return result;
    }

    private static int[] getVectorFromConsole(String arrayName, int n) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int[] result = new int[n];
            for (int i = 0; i < n; i++) {
                System.out.println("Введіть " + arrayName + "[" + i + "] елемент:");
                result[i] = Integer.parseInt(reader.readLine());
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int[][] getMatrixFromConsole(String matrixName, int n) {
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            result[i] = getVectorFromConsole(matrixName + "[" + i + "]", n);
        }
        return result;
    }

    public static void printVector(int[] array, String arrayName) {
        System.out.println(arrayName + ": " + Arrays.toString(array));
    }

    public static void printMatrix(int[][] matrix, String matrixName) {
        System.out.println(matrixName + ": ");
        for (int i = 0; i < matrix.length; i++) {
            printVector(matrix[i], matrixName + "[" + i + "]");
        }
    }

    public static void writeVectorToFile(int[] array, String arrayName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arrayName + ".txt"))) {
            writer.write(Arrays.toString(array).replaceAll("[,\\[\\]]", ""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeMatrixToFile(int[][] matrix, String matrixName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(matrixName + ".txt"))) {
            for (int[] row : matrix) {
                writer.write(Arrays.toString(row).replaceAll("[,\\[\\]]", "") + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}