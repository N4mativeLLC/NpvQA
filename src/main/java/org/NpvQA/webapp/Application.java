package org.NpvQA.webapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("org.NpvQA.webapp")
public class Application {

	
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		
	}
	

}
