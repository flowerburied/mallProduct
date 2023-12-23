package com.example.mall.product;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mall.product.entity.BrandEntity;
import com.example.mall.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Resource
    OSSClient ossClient;

    @Test
    public void testOss() throws com.aliyuncs.exceptions.ClientException {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        // STS临时访问密钥AccessKey ID和AccessKey Secret。
        String accessKeyId = "STS.NTVViHBwbw9qXKD3nJgUf4j5c";
        String accessKeySecret = "8iSUsQEhJNja5PjtumyjvHN6PV3CvjcCSEU7xwfo8Ghb";
// STS安全令牌SecurityToken。
        String securityToken = "CAIS9wF1q6Ft5B2yfSjIr5fjHdP8r6hDwPuaWm31124fa9pK26+egTz2IHhMeXVrBe8ct/8zn2hY7fYdlqx6T55OSBR7lXG2T80Fnzm6aq/t5uaXj9Vd+rDHdEGXDxnkprywB8zyUNLafNq0dlnAjVUd6LDmdDKkLTfHWN/z/vwBVNkMWRSiZjdrHcpfIhAYyPUXLnzML/2gQHWI6yjydBM35VUs1j0hsvjhnpLGukPk4QekmrNPlePYOYO5asRgBpB7Xuqu0fZ+Hqi7i3YItkQRqPgs1fcYom+X5YHCGSJU6hGCdvLO9dFyJxM8faUgBaNevGFhcV+STRC4GoABhmfuLbpRIDKnhxbTH2phlCpITcS6va1UtycyXkvTyp968JKA82tZQgajfybFQ/liR0sBMPNYX7Xi6PrnlL0MDYD8J9KvF6IDIdinxK0TldeY8Cw5lESXHcH7CD2RCyKa64LU2pcs1wOQHscUUHV16o+9PVSvqGXck5MMvtrc3Q4gAA==";

// 使用代码嵌入的STS临时访问密钥和安全令牌配置访问凭证。
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret, securityToken);
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
//        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "edu-test-202167";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "001.png";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath= "E:\\my_project\\2023_code\\1217\\record\\OSS\\001\\001.png";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

        try {
            InputStream inputStream = new FileInputStream(filePath);
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

    }


    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();

//        brandEntity.setName("XMi");
//        System.out.println(brandEntity);
//        brandService.save(brandEntity);

//        brandEntity.setBrandId(1L);
//        brandEntity.setName("apple");
//        brandService.updateById(brandEntity);


        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item) -> {
            System.out.println("success${}" + item);
        });


    }

}
