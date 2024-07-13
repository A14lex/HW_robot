import java.util.*;

public class Main {
    static int numberThread = 0;
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();//считает длины-повторы;
    /*
    генерирует текст generateRoute("RLRFR", 100);
считает количество команд поворота направо (буквы 'R');
выводит на экран результат.
число маршрутов - 1000 (число потоков)
     */

    public static void main(String[] args) throws InterruptedException {
        numberThread = 1000;
        int threadId = 0;
        Runnable runnable = () -> {
            String s = generateRoute("RLRFR", 100);
            Main main = new Main();
            int totalNumberR = main.onlyR(s);
            System.out.println("Строка " + s + "; Глобальное кол-во R в этой строке:  " + totalNumberR);


        };

        Runnable max = () -> {
            while (!Thread.interrupted()) {

                try {

                    synchronized (sizeToFreq) {
                        sizeToFreq.wait();
                        Main main = new Main();
                        main.actuallyMax();//вычисление максимального ключа в мапе
                    }


                } catch (InterruptedException e) {
                    System.out.println("Завершение");
                    Thread.currentThread().interrupt();

                }


            }

        };
        //Запуск потока, считающего максимум в мапе
        Thread threadMax = new Thread(max);
        threadMax.start();


        //заводим потоки генерации строк
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numberThread; i++) {
            Thread thread = new Thread(runnable);

            thread.setName("threadName:" + i);
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads
        ) {
            thread.join();

        }
//        System.out.println("Останавливаем поток поиска максимума");
        threadMax.interrupt();//остановили поток, считающий максимум
        //теперь надо посчитать какая частота (длина последовательности) самая длинная и расположить по убыванию
        List<Integer> list = new ArrayList<>();
        list.addAll(sizeToFreq.keySet());// в ключах посчитаны дланы последовательностей в строке
        Collections.sort(list);
        Collections.reverse(list);
//        System.out.println(list);
//        System.out.println("Кол-во R в последовательности - частота появления");//поиск с
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                System.out.println("- " + list.get(i) + ", встретилось " + sizeToFreq.get(list.get(i)) + " раз");
            } else {
                System.out.println("Самое частое количество повторений: " + list.get(i) + " ( " + sizeToFreq.get(list.get(i)) + "  раз)");
                System.out.println("Другие размеры: ");
            }
        }


    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public int onlyR(String letters) {


        StringBuilder stringBuilder;
        int totalNumberR = 0;//кол-во R вообще в строчке
        int localNumberR = 0;//кол-во R в одной последовательности


//
        for (int i = 0; i < letters.length(); i++) {
            if (letters.charAt(i) == 'R') {
                totalNumberR++;
            }
        }
        synchronized (sizeToFreq) {//заблокировал карту для других потоков
            if (sizeToFreq.containsKey(totalNumberR)) {
                int value = sizeToFreq.get(totalNumberR) + 1;
                sizeToFreq.put(totalNumberR, value);

            } else {
                if (totalNumberR != 0) {
                    sizeToFreq.put(totalNumberR, 1);

                }
            }
            sizeToFreq.notify();

            return totalNumberR;


        }


    }

    public int actuallyMax() {
        //поиск масимального ключа в мапе частот
        List<Integer> maxFreq = new ArrayList<>();
        maxFreq.addAll(sizeToFreq.keySet());
        Collections.sort(maxFreq);
        System.out.println("Текущий лидер в списке частот: " + maxFreq.get(maxFreq.size() - 1));


        return 0;
    }

}


