package src;


import main.java.util.ThreadColors;

public class Main {

    public static void main(String[] args) {

        Countdown countdown = new Countdown();

        Countdown c1 = new Countdown();
        Countdown c2 =  new Countdown();
        CountdownThread t1 = new CountdownThread(countdown);
        t1.setName("Thread1");
        CountdownThread t2 = new CountdownThread(countdown);
        t2.setName("Thread2");
        t1.start();
        t2.start();
    }
}

class Countdown{
    private int i;
    public  void doCountdown(){
        String color;
        switch (Thread.currentThread().getName()){
            case "Thread1":
                color = ThreadColors.CYAN;
                break;
            case "Thread2":
                color = ThreadColors.MAGENTA;
                break;
            default:
                color = ThreadColors.GREEN;
        }

        for( i=10;i>0;i--){
            System.out.println(color + Thread.currentThread().getName() + ":i = "+i);
        }
//        synchronized(this){
//            for( i=10;i>0;i--){
//                System.out.println(color + Thread.currentThread().getName() + ":i = "+i);
//            }
//        }



    }
}


class CountdownThread extends Thread{
    private Countdown threadCountdown;

    public  CountdownThread(Countdown countdown){
        this.threadCountdown = countdown;
    }

    public void run(){
        threadCountdown.doCountdown();
    }
}