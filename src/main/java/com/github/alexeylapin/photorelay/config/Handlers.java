package com.github.alexeylapin.photorelay.config;

import com.github.alexeylapin.photorelay.config.def.HandlerDef;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("photo-relay")
@Getter
@Setter
public class Handlers {

    private List<HandlerDef> handlers = new ArrayList<>();

}
