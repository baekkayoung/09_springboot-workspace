package com.kh.spring.discode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DiscodeNorifier {

    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1412996643950821467/Cips8hUTybHIP-l_2HFsryRNqRfApnAmxbVJz_IBIpm3_rtioNWM4XO4OB7JQZdDzhjE";
    private  static final int MAX_DISCORD_MESSAGE_LENGTH = 1900;

    public static void sendError(String errorMsg){


        try {
            URL url = new URL(WEBHOOK_URL);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setDoOutput(true); // HTTPConnection 객체가 서버로 데이터를 보낼 준비를 하겠다!

            // JSON 형식으로 디코 메시지 작성

            /*
                만약에 에러메시지 안에 따옴표가 있으면 json이 깨져버림!
                우리가 문자열안에서 쌍따옴표 쓰면 닫혀버림! 그래서 이스케이프 규칙을 적용을 해줘야 함!

                \" => 이렇게 줘야만 큰 따옴표 하나(")로만 인식. 큰 따옴표를 문자열 안에 넣겠다는 뜻!
                \\ => 역슬래시 하나(\)를 문자열 안에 넣겠다는 뜻!
             */


            // json 안전하게 변환된 메시지
            String safeMessage = escapeJson(errorMsg);

            if(safeMessage.length() > 1900){
                safeMessage = safeMessage.substring(0,MAX_DISCORD_MESSAGE_LENGTH);
           }
            // Discord payload
            String jsonPayload = String.format(
                    "{\"content\": \"🚨 서버 오류 발생!\\n```%s```\"}", // {"content": "🚨 서버 오류 발생!\n```%s```"} 최종적으로 이렇게 됨!
                    safeMessage
            );

            try(OutputStream os = connection.getOutputStream()){ // 출력용 스트림을 적어주면 반납까지 해줌
                os.write(jsonPayload.getBytes("UTF-8"));
                os.flush(); // 강제로 보내줘야 discord로 날아감
            }

            int responseCode = connection.getResponseCode();
            System.out.println("디코 응답코드 : " + responseCode);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String escapeJson(String text){
        if(text == null){
            return "";
        }else{

            return text
                    .replace("\\", "\\\\")   // 역슬래시
                    .replace("\"", "\\\"")   // 큰따옴표
                    .replace("\n", "\\n")    // 줄바꿈
                    .replace("\r", "");      // 캐리지 리턴 제거

        }
    }
}
