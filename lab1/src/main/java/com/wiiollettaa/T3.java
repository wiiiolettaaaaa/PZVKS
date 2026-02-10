package com.wiiollettaa;


public class T3 implements Runnable {
    private final int n;
    private final int inputOption;

    public T3(int n, int inputOption) {
        super();
        this.n = n;
        this.inputOption = inputOption;
    }

    @Override
    public void run() {
        // повідомлення про початок виконання потоку Т3
        System.out.println("Потік T3 почав виконання");

        //отримання вхідних данних
        int[] R = Data.getVectorOption("R", n, inputOption, 3);
        int[] S = Data.getVectorOption("S", n, inputOption, 3);
        int[][] MO = Data.getMatrixOption("MO", n, inputOption, 3);
        int[][] MP = Data.getMatrixOption("MP", n, inputOption, 3);

        int[] result = Data.F3(R, S, MO, MP);

        // виведення результату
        if (n >= 1000) Data.writeVectorToFile(result, "S");
        else Data.printVector(result, "S");

        // повідомлення про завршення виконання потоку Т3
        System.out.println("Потік T3 завершив виконання");
    }
}