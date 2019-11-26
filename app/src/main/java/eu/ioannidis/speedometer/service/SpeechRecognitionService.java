package eu.ioannidis.speedometer.service;

import android.util.Log;

public class SpeechRecognitionService {


    public static void createThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                        System.out.println("654d56sa4d6s5a4d65asd465as4d6as54d56sa4d65sa4d65as4d6as4d6as5d65as4d");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (Thread.interrupted()) {
                        return;
                    }
                }
            }
        }).start();
    }
}
