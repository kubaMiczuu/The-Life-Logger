package com.thelifelogger.agent;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WindowScraper {
    public static void main(String[] args) throws IOException, InterruptedException {

        int maxCount = 1024;
        char[] buffer = new char[maxCount];

        String apiEndpoint = "http://localhost:8080/api/activities/ping";
        HttpClient client = HttpClient.newHttpClient();

        while (true) {

            WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
            User32.INSTANCE.GetWindowText(hwnd, buffer, maxCount);
            String windowTitle = Native.toString(buffer);

            IntByReference processId = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, processId);

            WinNT.HANDLE processHandle = Kernel32.INSTANCE.OpenProcess(0x0400 | 0x0010, false, processId.getValue());
            Psapi.INSTANCE.GetModuleFileNameExW(processHandle, null, buffer, maxCount);
            Kernel32.INSTANCE.CloseHandle(processHandle);

            String processPathName = Native.toString(buffer);
            int lastSlash = processPathName.lastIndexOf('\\');
            String processName = processPathName.substring(lastSlash+1);

            if(processName.isEmpty()){
                System.out.println("Failed to send");
            } else {
                String json = String.format("{\"processName\":\"%s\", \"windowTitle\":\"%s\"}", processName, windowTitle);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiEndpoint))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                try{
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("Ping wysłany! Status: " + response.statusCode());
                } catch (Exception e) {
                    System.out.println("Błąd wysyłania: " + e.getMessage());
                }
            }

            Thread.sleep(5000);
        }
    }
}
