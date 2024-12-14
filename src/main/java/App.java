


import org.bouncycastle.util.io.pem.PemWriter;

import javax.net.ssl.*;
import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class App 
{
    private static final String[] protocols = new String[]{"TLSv1.3"};
    private static final String[] cipher_suites = new String[]{"TLS_AES_128_GCM_SHA256"};

    public static void main( String[] args ) throws IOException {


        SSLSocket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                                throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            SSLSocketFactory factory =
                    (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket =
                    (SSLSocket) factory.createSocket("yandex.by", 443);

            socket.setEnabledProtocols(protocols);
            socket.setEnabledCipherSuites(cipher_suites);

            socket.startHandshake();

            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())));

            out.println("GET / HTTP/1.0");
            out.println();
            out.flush();

            if (out.checkError())
                System.out.println("SSLSocketClient:  java.io.PrintWriter error");
            Certificate[] certificates = socket.getSession().getPeerCertificates();
            PemWriter pw = new PemWriter(out);

            for(Certificate crt: certificates){
              crt.getEncoded();
            }
            /* read response */
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null)
                socket.close();
            if (out != null)
                out.close();
            if (in != null)
                in.close();
        }
    }
}
