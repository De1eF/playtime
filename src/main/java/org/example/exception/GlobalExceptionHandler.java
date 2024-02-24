package org.example.exception;

import org.example.service.Toast;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler{
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        String toastMessage = e.getClass().getName();
        Toast toast = new Toast(toastMessage);
        toast.showToast();
        toast.dispose();

        System.out.println(e.getMessage());

        throw new RuntimeException(e);
    }
}
