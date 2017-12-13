
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Index {
    public static int fileCount = 0;
    public static void main(String[] args) {
        System.out.println("命令提示:1->输入网址;0->退出程序;");
        Scanner scanner = new Scanner(System.in);
        String string = null;
        String url = null;
        while (scanner.hasNextLine()) {
            string = scanner.nextLine();
            if(string.equals("1")){
                System.out.println("请输入网址:");
                url = scanner.nextLine();
                Index index = new Index();
                index.action(url);
            }
            if(string.equals("0")) {
                break;
            }
        }
        System.exit(0);
    }

    public void action(String urlStr){
        RequestClient requestClient = new RequestClient();
        if(!urlStr.contains("http")){
            urlStr = "http://" + urlStr;
        }
        String albumPage = requestClient.getPageHtml(urlStr.trim());
        Document doc = Jsoup.parse(albumPage);
        Elements elements = doc.getElementsByTag("li");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(15, 20, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        for(Element ele:elements) {
            Document eleDoc = Jsoup.parse(ele.toString());
            String soundId = eleDoc.getElementsByTag("li").attr("sound_id");
            if(soundId.equals("")) {
                continue;
            }
            String title = eleDoc.getElementsByClass("title").attr("title");
            String sourUrl = "http://www.ximalaya.com/tracks/" + soundId +".json";
            String response = requestClient.getPageHtml(sourUrl);
            String regex = "(http|https://).*?(\\.m4a)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(response);
            String voiceSource = null;

            if (matcher.find()) {
                voiceSource = matcher.group();
                FileDealer dealer = new FileDealer();
                dealer.setFileUrl(voiceSource);
                dealer.setFiletitle(title);
                executor.submit(dealer);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        executor.shutdown();

        while (!executor.isTerminated()) {
            System.out.println("提交任务总数:"+ executor.getTaskCount()+";正在进行任务数:"+ executor.getActiveCount() + ";已完成任务数:" + executor.getCompletedTaskCount()+ ";等待执行任务数:" + executor.getQueue().size());
            try {
                for (int i = 0; i < 10; i++) {
                    System.out.print("> ");
                    Thread.sleep(1000);
                }
                System.out.println();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("all finished");
        System.exit(0);
    }
}
