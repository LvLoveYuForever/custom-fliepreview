package cn.keking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
//@ComponentScan(value = "cn.keking.*")
public class FilePreviewApplication {

    //设置中国的时区，不设置框架的默认时间有8小时的差值
    @PostConstruct
    void started(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

	public static void main(String[] args) {
//        Properties properties = System.getProperties();
//        System.out.println(properties.get("user.dir"));


        SpringApplication.run(FilePreviewApplication.class, args);
	}
}
