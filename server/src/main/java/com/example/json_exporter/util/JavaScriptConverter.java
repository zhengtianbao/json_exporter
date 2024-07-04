package com.example.json_exporter.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.Invocable;

public class JavaScriptConverter {
    public static String toJSON(String jsonString, String script) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        try {
            engine.eval(script);
            Invocable invocable = (Invocable) engine;
            String result = (String) invocable.invokeFunction("modify", jsonString);
            return result;
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
            return jsonString;
        }
    }
}
