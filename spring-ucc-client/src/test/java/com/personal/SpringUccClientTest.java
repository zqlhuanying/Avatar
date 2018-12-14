package com.personal;

import com.personal.service.ConfigService;
import com.personal.ucc.UccConfigFactory;
import com.personal.ucc.UccNamespace;
import org.junit.Test;

/**
 * @author zhuangqianliao
 */
public class SpringUccClientTest extends BaseTest {

    @Test
    public void getValueTest() throws InterruptedException {

        UccNamespace JssConfigUccNamespace = new UccNamespace("/intl_galaxy/jss-config", "WldPHiy9");

        ConfigService.registryNamespace(JssConfigUccNamespace.getNamespace(), JssConfigUccNamespace, new UccConfigFactory());
        for (; ;) {
            String jss_endpoint = ConfigService.getConfig(JssConfigUccNamespace.getNamespace()).getProperty("jss.endpoint", null);

            System.out.println("jss_endpoint: " + jss_endpoint);
            Thread.sleep(1000);
        }
    }
}
