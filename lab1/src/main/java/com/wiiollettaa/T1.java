package com.wiiollettaa;

public class T1 implements Runnable {
    private final int n;
    private final int inputOption;


    public T1(int n, int inputOption) {
        super();
        this.n = n;
        this.inputOption = inputOption;
    }

    @Override
    public void run() {
        // повідомлення про початок виконання потоку T1
        System.out.println("Потік T1 почав виконання");

        int[] A = Data.getVectorOption("A", n, inputOption, 1);
        int[] B = Data.getVectorOption("B", n, inputOption, 1);
        int[] C = Data.getVectorOption("C", n, inputOption, 1);
        int[] D = Data.getVectorOption("D", n, inputOption, 1);
        int[][] MA = Data.getMatrixOption("MA", n, inputOption, 1);
        int[][] ME = Data.getMatrixOption("ME", n, inputOption, 1);

        // обчислення функції F1
        int[] E = Data.F1(A, B, C, D, MA, ME);

        // виведення результату
        if (n >= 1000) Data.writeVectorToFile(E, "E");
        else Data.printVector(E, "E");

        // повідомлення про завершення виконання потоку T1
        System.out.println("Потік T1 завершив виконання");
    }
}
