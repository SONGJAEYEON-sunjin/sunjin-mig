package com.kcube.trns.sunjin.batch.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FileDnldRunner {

    public static void main(String[] args) throws Exception {

        disableSSLCertificateChecking();

        String csvPath = "C:/dev/note/csv/DP_ACC_User_202506131023.csv";
        String downloadDir = "C:/dev/sealImage/prod/download";
        String errorCsvPath = "C:/dev/sealImage/prod/error/error.csv";

        List<String[]> fileUrlList = readCsv(csvPath);

        List<String[]> failedList = new ArrayList<>();
        int successCount = 0;

        for(String[] data : fileUrlList) {
            try{
                downloadFile(data[2],downloadDir);
                successCount++;
            } catch (IOException e) {
                log.error("다운로드 실패: userId : {}, sealImage : {} ", data[0], data[2], e);
                failedList.add(data);
            }
        }

        log.info("다운로드 성공 건수: {}", successCount);
        log.info("다운로드 실패 건수: {}", failedList.size());

        writeErrorCsv(failedList, errorCsvPath);
    }

    private static void writeErrorCsv(List<String[]> failedList, String path) {
        if (failedList.isEmpty()) {
            log.info("실패한 파일 없음");
            return;
        }

        try {
            Path filePath = Paths.get(path);
            Files.createDirectories(filePath.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (String[] data : failedList) {
                    writer.write(String.join(",", data));
                    writer.newLine();
                }
                log.info("실패 목록 CSV 저장 완료: {}", path);
            }
        } catch (IOException e) {
            log.error("실패 목록 CSV 저장 실패: {}", path, e);
        }
    }

    private static void downloadFile(String fileUrl, String downloadDir) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");

        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        Path path = Paths.get(downloadDir, fileName);
        Files.createDirectories(path.getParent());

        log.info("다운로드 시작: {}", fileUrl);

        try(InputStream is = conn.getInputStream(); OutputStream os = Files.newOutputStream(path)) {

            byte[] buffer = new byte[8192];

            int len;
            while ((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
        }

        log.info("다운로드 완료: {}", path.toAbsolutePath());
    }

    private static List<String[]>  readCsv(String path){

        List<String[]> fileNameList = new ArrayList<String[]>();

        try(BufferedReader br = new BufferedReader(new FileReader(path))){
            String line = null;

            while((line = br.readLine()) != null){
                log.info("line: " + line);
                String[] data = line.split(",");
                fileNameList.add(data);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return fileNameList;
    }

    private static void disableSSLCertificateChecking() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
        };

        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
}
