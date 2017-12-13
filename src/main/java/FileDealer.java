import java.io.*;
import java.net.*;
import java.util.concurrent.Callable;

public class FileDealer implements Callable<Boolean> {
    public String fileUrl;
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String filetitle;


    public void setFiletitle(String filetitle) {
        this.filetitle = filetitle;
    }

    public Boolean call() {
        System.out.println();
        System.out.println("线程" + Thread.currentThread() + "开始下载:" + this.filetitle);
        return this.getFile(this.fileUrl, this.filetitle);
    }

    public Boolean getFile(String fileUrl,String title) {
        URL url = null;
        HttpURLConnection urlCon = null;
        File filePath = null;
        File file = null;
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;
        String fileName = null;
        try {
            url = new URL(fileUrl);
            urlCon = (HttpURLConnection)url.openConnection();
            String path = System.getProperty("user.dir");
            filePath = new File(path + "/outPut");
            if(!filePath.isDirectory()) {
                filePath.mkdir();
            }
            inputStream = new DataInputStream(urlCon.getInputStream());
            Index.fileCount = Index.fileCount + 1;

            if(Index.fileCount < 10) {
                fileName = "0" + String.valueOf(Index.fileCount) + "_" + title + ".m4a";
            } else {
                fileName = String.valueOf(Index.fileCount) + "_" + title + ".m4a";
            }
            file = new File(filePath.getAbsolutePath() + "/" + fileName);
            outputStream = new DataOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[10240];
            int count;
            while((count=inputStream.read(buffer))>0){
                outputStream.write(buffer,0,count);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(inputStream!=null) {
                    inputStream.close();
                }
                if(outputStream!=null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }
        System.out.println();
        System.out.println(Thread.currentThread() + "下载:"+ fileName  +"完成");
        return true;
    }
}
