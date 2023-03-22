package com.jhchen;

import com.jhchen.domain.modul.Account;
import com.jhchen.utils.JSONUtil;
import com.jhchen.view.InitSceneView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class MineApplication extends AbstractJavaFxApplicationSupport {
    public static void main(String[] args) {
        launch(MineApplication.class, InitSceneView.class,args);
        SpringApplication.run(MineApplication.class,args);
    }
    @RequestMapping("/")
    public String home() {
        return "redirect:/swagger-ui.html";
    }
}
