package com.example.mall.thirdparty;





import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallThirdPartyApplicationTests {


    @Autowired
    private OSSClient ossClient;

    @Test
    public void testOss() {

        String bucketName = "edu-test-202167";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "003.png";
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath = "E:\\my_project\\2023_code\\1217\\record\\OSS\\001\\003.png";

        ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File(filePath));

        ossClient.shutdown();

        System.out.println("完成");
    }

    @Test
    void contextLoads() {
    }

}
