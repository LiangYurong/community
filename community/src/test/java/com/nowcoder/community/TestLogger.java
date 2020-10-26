package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = RunMyApplication.class)
public class TestLogger {

    private static final Logger logger= LoggerFactory.getLogger("TestLogger.class");

    @Test
    public void testLog(){
        System.out.println(logger.getName());
        logger.info("info log");
        logger.debug("debug log");
        logger.warn("warn log");
        logger.error("error log");
    }

}
