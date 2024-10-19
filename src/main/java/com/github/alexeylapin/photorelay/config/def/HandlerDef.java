package com.github.alexeylapin.photorelay.config.def;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class HandlerDef  {

//    private int index;
    private String name;
    private ConditionDef condition = new ConditionDef();
    private ActionDef action = new ActionDef();

    public void validate() {
        condition.validate();
    }

    @Getter
    @Setter
    public static class ConditionDef {

        private String type;
        private Map<String, String> with;
        private List<ConditionDef> and = new ArrayList<>();
        private List<ConditionDef> or = new ArrayList<>();
        private ConditionDef not;

        public void validate() {
            int i = 0;
            if (and != null && !and.isEmpty()) {
                i++;
            }
            if (or != null && !or.isEmpty()) {
                i++;
            }
            if (not != null) {
                i++;
            }
            if (type != null && i > 0) {
                throw new IllegalArgumentException("type, and, or, not cannot be specified together");

            }
            if (i > 1) {
                throw new IllegalArgumentException("only one of and, or, not can be specified");
            }
        }

    }

    @Getter
    @Setter
    public static class ActionDef {

        private String type;
        private Map<String, String> with;

    }

}
