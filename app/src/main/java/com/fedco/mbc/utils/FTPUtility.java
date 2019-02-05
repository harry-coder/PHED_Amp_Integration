package com.fedco.mbc.utils;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;

public class FTPUtility {

        // FTP server information
        private String host;
        private int port;
        private String username;
        private String password;

        private FTPClient ftpClient = new FTPClient();
        private int replyCode;

        private InputStream inputStream;

        public FTPUtility(String host, int port, String user, String pass) {
            this.host = host;
            this.port = port;
            this.username = "Anonymous";
            this.password = "anonymous";
        }

        /**
         * Connect and login to the server.
         *
         * @throws Exception
         */
        public void connect() throws Exception {
            try {
                ftpClient.connect(host, port);
                replyCode = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(replyCode)) {
                    throw new Exception("FTP serve refused connection.");
                }

                boolean logged = ftpClient.login(username, password);
                if (!logged) {
                    // failed to login
                    ftpClient.disconnect();
                    throw new Exception("Could not login to the server.");
                }

                ftpClient.enterLocalPassiveMode();

            } catch (IOException ex) {
                throw new Exception("I/O error: " + ex.getMessage());
            }
        }

        /**
         * Gets size (in bytes) of the file on the server.
         *
         * @param filePath
         *            Path of the file on server
         * @return file size in bytes
         * @throws Exception
         */
        public long getFileSize(String filePath) throws Exception {
            try {
                FTPFile file = ftpClient.mlistFile(filePath);
                Log.d("TAG", "file path" + filePath);
                if (file == null) {
                    throw new Exception("The file may not exist on the server!");
                }
                return file.getSize();
            } catch (IOException ex) {
                throw new Exception("Could not determine size of the file: "
                        + ex.getMessage());
            }
        }

        /**
         * Start downloading a file from the server
         *
         * @param downloadPath
         *            Full path of the file on the server
         * @throws Exception
         *             if client-server communication error occurred
         */
        public void downloadFile(String downloadPath) throws Exception {
            try {

                boolean success = ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                if (!success) {
                    throw new Exception("Could not set binary file type.");
                }


                inputStream = ftpClient.retrieveFileStream(downloadPath);

                if (inputStream == null) {
                    throw new Exception(
                            "Could not open input stream. The file may not exist on the server.");
                }
            } catch (IOException ex) {
                throw new Exception("Error downloading file: " + ex.getMessage());
            }
        }

        /**
         * Complete the download operation.
         */
        public void finish() throws IOException {
            inputStream.close();
            ftpClient.completePendingCommand();
        }

        /**
         * Log out and disconnect from the server
         */
        public void disconnect() throws Exception {
            if (ftpClient.isConnected()) {
                try {
                    if (!ftpClient.logout()) {
                        throw new Exception("Could not log out from the server");
                    }
                    ftpClient.disconnect();
                } catch (IOException ex) {
                    throw new Exception("Error disconnect from the server: "
                            + ex.getMessage());
                }
            }
        }

        /**
         * Return InputStream of the remote file on the server.
         */
        public InputStream getInputStream() {
            return inputStream;
        }
    }

