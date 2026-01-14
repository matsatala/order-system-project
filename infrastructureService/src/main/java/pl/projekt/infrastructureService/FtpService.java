package pl.projekt.infrastructureService;


import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FtpService {

    public void uploadInvoice(String orderId, String content){
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect("localhost",21);
            ftpClient.login("user","pass");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            InputStream inputStream = new ByteArrayInputStream(content.getBytes());
            String filename = "faktura_"+orderId+".txt";

            boolean done = ftpClient.storeFile(filename, inputStream);
            if (done) System.out.println("Faktura zapisana na FTP");

            ftpClient.logout();
            ftpClient.disconnect();

        }catch (IOException e){
            System.err.println("Błąd FTP: "+e.getMessage());
        }
    }
}
